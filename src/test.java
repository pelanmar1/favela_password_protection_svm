import java.util.Random;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;


public class test {
	
	public static void main(String[] args) {
		
		// Train data 
		int numSamples = 200;
		int numFeatures = 8;
		
		
		svm_parameter param = new svm_parameter();
		// default values
		param.svm_type = svm_parameter.ONE_CLASS;
		param.kernel_type = svm_parameter.RBF;
		param.gamma = 1;
		param.nu = 0.05;
		
		
		
		svm_problem prob = new svm_problem();
		
		prob.x = new svm_node[numSamples][numFeatures];
		svm_node temp;
		for(int i=0;i<numSamples;i++) {
			for(int j=0;j<numFeatures;j++) {
				temp = new svm_node();
				temp.index = (j+1);
				temp.value = 1;
				prob.x[i][j]=temp;
			}
		}
		System.out.println("Train X:"+prob.x[0][0].value);

		svm_model model = svm.svm_train(prob, param);
		
		svm_node[] test_x = new svm_node[numFeatures];
		for(int j=0;j<numFeatures;j++) {
			temp = new svm_node();
			temp.index = (j+1);
			temp.value = 1;
			test_x[j] = temp;
		}
		
		double d = svm.svm_predict(model, test_x);
		System.out.println("Prediction: "+d);
		
		
	}
	
	
	
}
