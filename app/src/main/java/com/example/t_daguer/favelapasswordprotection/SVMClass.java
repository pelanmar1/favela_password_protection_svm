package com.example.t_daguer.favelapasswordprotection;

import java.util.ArrayList;
import java.util.Random;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class SVMClass {

    int numSamples;
    int numFeatures;
    svm_model model;
    svm_parameter param;

    public SVMClass()
    {
        numSamples = 200;
        numFeatures = 8;

        param = new svm_parameter();
        param.svm_type = svm_parameter.ONE_CLASS;   // default values
        param.kernel_type = svm_parameter.RBF;
        param.gamma = 1;
        param.nu = 0.05;
        param.C = 1;
    }

    public void train(ArrayList<ArrayList<Float>> a, ArrayList<Float> totalTime, ArrayList<Integer> delHits)
    {
        numSamples = a.size();
        if(a.size() > 0)
            numFeatures = a.get(0).size()+2;
        else
            return;
        param.gamma = 1/numFeatures;

        svm_problem prob = new svm_problem();

        prob.x = new svm_node[numSamples][numFeatures];
        svm_node temp;
        for(int i=0;i<numSamples;i++)
        {
            for(int j=0;j<numFeatures;j++)
            {
                temp = new svm_node();
                temp.index = (j+1);
                if(j < a.size()) {
                    System.out.println("j:"+j+", a.get(0):"+a.get(0));
                    temp.value = a.get(i).get(j);
                }
                else if(j == a.size())
                    temp.value = totalTime.get(i);
                else
                    temp.value = delHits.get(i);
                prob.x[i][j]=temp;
            }
        }
        System.out.println("Train X:"+prob.x[0][0].value);

        model = svm.svm_train(prob, param);
    }

    public boolean test(ArrayList<Float> a, float totalTime, int delHits)
    {
        numSamples = 1;
        if(a.size() > 0)
            numFeatures = a.size()+2;
        else
            return false;

        svm_node temp;
        svm_node[] test_x = new svm_node[numFeatures];
        for(int j=0;j<numFeatures;j++) {
            temp = new svm_node();
            temp.index = (j+1);
            if(j < a.size())
                temp.value = a.get(j);
            else if(j == a.size())
                temp.value = totalTime;
            else
                temp.value = delHits;
            test_x[j] = temp;
        }

        double d = svm.svm_predict(model, test_x);
        System.out.println("Prediction: "+d);

        if(d == 1)
            return true;
        else
            return false;
    }

}
