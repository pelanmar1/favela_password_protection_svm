package com.example.t_daguer.favelapasswordprotection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SVMClass svm = new SVMClass();

    Button train;
    Button addToSet;
    Button test;

    EditText passwordBox;
    ArrayList<Float> arrayTimeBetweenHits = new ArrayList<Float>();
    long latestHitTime = -1;
    long firstHitTime = -1;
    float timeFromFirstHitToSubmit;
    int deletesHits = 0;

    // Training set
    ArrayList<ArrayList<Float>> training_timeBetweenHits = new ArrayList<ArrayList<Float>>();
    ArrayList<Float> training_totalTimes = new ArrayList<>();
    ArrayList<Integer> training_deletes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        train = (Button)findViewById(R.id.trainBtn);
        train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                train();
                Toast.makeText(getApplicationContext(), "Trained", Toast.LENGTH_SHORT).show();
            }
        });

        addToSet = (Button)findViewById(R.id.addToSet);
        addToSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intentionally, this is calculated once submit is pressed, not at the last keystroke
                timeFromFirstHitToSubmit = getTimeDiff(firstHitTime);

                if(addToTrainingSet(arrayTimeBetweenHits, timeFromFirstHitToSubmit, deletesHits))
                    resetInput();
            }
        });

        test = (Button)findViewById(R.id.testBtn);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(test((ArrayList<Float>)arrayTimeBetweenHits.clone(), timeFromFirstHitToSubmit, deletesHits))
                    Toast.makeText(getApplicationContext(), "You're the same person", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "-x INTRUDER x-", Toast.LENGTH_SHORT).show();
                resetInput();
            }
        });

        passwordBox = (EditText)findViewById(R.id.passwordTextBox);
        passwordBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i1 == 1)
                    deletesHits++;
                if(latestHitTime == -1)
                    firstHitTime = System.nanoTime();
                else
                    arrayTimeBetweenHits.add(getTimeDiff(latestHitTime));
                latestHitTime = System.nanoTime();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void train(){
        System.out.println(training_timeBetweenHits);
        svm.train(training_timeBetweenHits, training_totalTimes, training_deletes);
    }

    private boolean addToTrainingSet(ArrayList<Float> a, float totalTime, int delHits){

        ArrayList<Float> tmp = (ArrayList<Float>)a.clone();

        training_timeBetweenHits.add(tmp);
        training_totalTimes.add(totalTime);
        training_deletes.add(delHits);

        return true;
    }

    private boolean test(ArrayList<Float> a, float totalTime, int delHits){
        System.out.println("BURH: "+a);
        return svm.test(a, totalTime, delHits);
    }

    private float getTimeDiff(long t){
        return (System.nanoTime() - t)/1000000000f;
    }

    private void resetInput(){
        latestHitTime = -1;
        firstHitTime = -1;
        timeFromFirstHitToSubmit = -1;
        deletesHits = 0;
        arrayTimeBetweenHits.clear();
        passwordBox.setText("");
    }

}
