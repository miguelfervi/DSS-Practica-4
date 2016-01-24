package com.dss.miguel.trivialmiguelfernandezvillegas;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Miguel on 05/01/2016.
 */

public class TrivialImageActivity extends AppCompatActivity {

    private static final int NUM_AVAIBLE_QUESTIONS = 7;
    private static final int NUM_QUESTIONS=5;
    private TextView textView;
    private ImageButton option1, option2, option3, option4;
    private Button back_menu, next_question;
    private Question question;
    private ArrayList<String> answers;
    private DBHelper db;
    private ArrayList<Integer> id_questions;
    private int i_question;
    private int score;
    private int pos_right_answer;
    private boolean stop;
    private int time;
    private int time_answer;

    // Progressbar
    private ProgressBar progressBar;
    private TextView progressBar_text;
    private int progressStatus;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivial_image);

        id_questions = randomArrayList(NUM_QUESTIONS);
        id_questions = getIntent().getIntegerArrayListExtra("id_questions");
        i_question = getIntent().getIntExtra("i_question", 0);
        score = getIntent().getIntExtra("score", 0);
        time_answer = getIntent().getIntExtra("time_answer", 0);
        db = MainActivity.getDB();

        initQuestionView();
        updateQuestion();

    }

    private void initProgressBar(){
        progressStatus = 0;
        stop = false;
        time = 15;
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar_text = (TextView) findViewById(R.id.progressbar_text);
        progressBar_text.setText(Integer.toString(time));
        // Start long running operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 150) {
                    progressStatus += 2;
                    // Update the progress bar and display the
                    //current value in the text view
                    if(stop) {
                        time_answer += 15 - time;
                        break;
                    }
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            if(progressStatus%10==0) {
                                time--;
                                progressBar_text.setText(Integer.toString(time));
                            }
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        //Just to display the progress slowly
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(time==0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            time_answer += 15;
                            removeButtonListeners();
                            initListenersAfterResponse();

                        }
                    });
                }
            }
        }).start();
    }

    private void initQuestionView(){
        textView = (TextView) findViewById(R.id.text_question);
        option1 = (ImageButton) findViewById(R.id.image_option1);
        option2 = (ImageButton) findViewById(R.id.image_option2);
        option3 = (ImageButton) findViewById(R.id.image_option3);
        option4 = (ImageButton) findViewById(R.id.image_option4);
        back_menu = (Button) findViewById(R.id.back_menu);
        next_question = (Button) findViewById(R.id.next_question);
    }

    private void addTextQuestion(){
        textView.setText(question.getQuestion());

        answers = new ArrayList<>();
        answers = question.getWrong_answers();
        answers.add(question.getRight_answer());

        // Shuffle the arraylist
        long seed = System.nanoTime();
        Collections.shuffle(answers, new Random(seed));

        option1.setImageResource(getResourceId(answers.get(0), "drawable", getPackageName()));
        option2.setImageResource(getResourceId(answers.get(1), "drawable", getPackageName()));
        option3.setImageResource(getResourceId(answers.get(2), "drawable", getPackageName()));
        option4.setImageResource(getResourceId(answers.get(3), "drawable", getPackageName()));

        pos_right_answer = answers.indexOf(question.getRight_answer());
    }


    private void right_and_next(){  // !!!

        removeButtonListeners();
        Toast toast_acierto = Toast.makeText(getApplicationContext(),
                "Has acertado", Toast.LENGTH_SHORT);
        toast_acierto.show();


        //tmp.setBackground(getResources().getDrawable(R.drawable.custom_btn_right));
       // showRightAnswer(pos_right_answer);
        next_question.setBackground(getResources().getDrawable(R.drawable.custom_btn));;

        if(i_question==NUM_QUESTIONS)
            next_question.setText("Fin");

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (i_question < NUM_QUESTIONS) {
            question = db.getQuestion(id_questions.get(i_question));
            if (question.getType() == Question.TEXT_QUESTION) {
                Intent intent = new Intent(TrivialImageActivity.this, TrivialActivity.class);
                intent.putIntegerArrayListExtra("id_questions", id_questions);
                intent.putExtra("i_question", i_question);
                intent.putExtra("score", score);
                startActivity(intent);
                finish();
            } else {
                updateQuestion();
            }
        } else {
            Intent intent = new Intent(TrivialImageActivity.this, ScoreActivity.class);
            intent.putExtra("score", score);
            startActivity(intent);
            finish();
        }



    }
    private void showRightAnswer(int pos){

        switch(pos){
            case 0 : option1.setBackground(getResources().getDrawable(R.drawable.custom_btn_right));

                break;
            case 1 : option2.setBackground(getResources().getDrawable(R.drawable.custom_btn_right));

                break;
            case 2 : option3.setBackground(getResources().getDrawable(R.drawable.custom_btn_right));

                break;
            case 3 : option4.setBackground(getResources().getDrawable(R.drawable.custom_btn_right));

                break;
        }

    }
    public void sonido_fallo() {
        MediaPlayer mp_wrong = MediaPlayer.create(this, R.raw.fallo);
        mp_wrong.start();
    }

    public void sonido_acierto() {
        MediaPlayer mp_right = MediaPlayer.create(this, R.raw.acierto);
        mp_right.start();
    }

    boolean correcta=false;

    private void addButtonListeners(){
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop=true;
                if (answers.get(0).equals(question.getRight_answer())) {
                    option1.setBackground(getResources().getDrawable(R.drawable.custom_btn_right));

                    score++;
                    correcta=true;
                    sonido_acierto();
                } else {
                    option1.setBackground(getResources().getDrawable(R.drawable.custom_btn_wrong));
                    showRightAnswer(pos_right_answer);
                    sonido_fallo();
                }
                if(correcta){right_and_next();correcta=false;}
                else{
                    removeButtonListeners();
                    initListenersAfterResponse();
                }
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop=true;
                if (answers.get(1).equals(question.getRight_answer())) {
                    option2.setBackground(getResources().getDrawable(R.drawable.custom_btn_right));

                    score++;
                    correcta=true;
                    sonido_acierto();
                } else {
                    option2.setBackground(getResources().getDrawable(R.drawable.custom_btn_wrong));
                    showRightAnswer(pos_right_answer);
                    sonido_fallo();
                }
                if(correcta){right_and_next();correcta=false;}
                else{
                    removeButtonListeners();
                    initListenersAfterResponse();
                }
            }
        });
        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop=true;
                if (answers.get(2).equals(question.getRight_answer())) {
                    option3.setBackground(getResources().getDrawable(R.drawable.custom_btn_right));

                    score++;
                    correcta=true;
                    sonido_acierto();
                } else {
                    option3.setBackground(getResources().getDrawable(R.drawable.custom_btn_wrong));
                    showRightAnswer(pos_right_answer);
                    sonido_fallo();
                }
                if(correcta){right_and_next();correcta=false;}
                else{
                    removeButtonListeners();
                    initListenersAfterResponse();
                }
            }
        });
        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop=true;
                if (answers.get(3).equals(question.getRight_answer())) {
                    option4.setBackground(getResources().getDrawable(R.drawable.custom_btn_right));

                    score++;
                    correcta=true;
                    sonido_acierto();
                } else {
                    option4.setBackground(getResources().getDrawable(R.drawable.custom_btn_wrong));
                    showRightAnswer(pos_right_answer);
                    sonido_fallo();
                }
                if(correcta){right_and_next();correcta=false;}
                else{
                    removeButtonListeners();
                    initListenersAfterResponse();
                }
            }
        });
        back_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrivialImageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        next_question.setOnClickListener(null);

    }

    private void restartButtonBackground(){
        option1.setBackground(getResources().getDrawable(R.drawable.custom_btn));
        option2.setBackground(getResources().getDrawable(R.drawable.custom_btn));
        option3.setBackground(getResources().getDrawable(R.drawable.custom_btn));
        option4.setBackground(getResources().getDrawable(R.drawable.custom_btn));
        next_question.setBackground(getResources().getDrawable(R.drawable.custom_btn_deactivated));
    }


    private void initBackgroundAfterResponse() {
        next_question.setBackground(getResources().getDrawable(R.drawable.custom_btn));
    }

    private void initListenersAfterResponse(){
        initBackgroundAfterResponse();


        if(i_question==NUM_QUESTIONS)
            next_question.setText("Fin");
        next_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i_question < NUM_QUESTIONS) {
                    question = db.getQuestion(id_questions.get(i_question));
                    if (question.getType() == Question.TEXT_QUESTION) {
                        Intent intent = new Intent(TrivialImageActivity.this, TrivialActivity.class);
                        intent.putIntegerArrayListExtra("id_questions", id_questions);
                        intent.putExtra("i_question", i_question);
                        intent.putExtra("score", score);
                        startActivity(intent);
                        finish();
                    } else {
                        updateQuestion();
                    }
                } else {
                    Intent intent = new Intent(TrivialImageActivity.this, ScoreActivity.class);
                    intent.putExtra("score", score);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }

    private void updateQuestion(){
        question = db.getQuestion(id_questions.get(i_question));
        i_question++;


        addTextQuestion();
        addButtonListeners();
        restartButtonBackground();
        initProgressBar();
    }




    private void removeButtonListeners(){
        option1.setOnClickListener(null);
        option2.setOnClickListener(null);
        option3.setOnClickListener(null);
        option4.setOnClickListener(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<Integer> randomArrayList(int n){
        ArrayList<Integer> list = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < n; i++)
        {
            int aux = random.nextInt(NUM_AVAIBLE_QUESTIONS)+1;
            while(list.contains(aux)){
                aux = random.nextInt(NUM_AVAIBLE_QUESTIONS)+1;
            }
            list.add(aux);
        }
        return list;
    }

    public static int getNumQuestions() {
        return NUM_QUESTIONS;
    }


    private int getResourceId(String pVariableName, String pResourcename, String pPackageName)
    {
        try {
            return getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


}
