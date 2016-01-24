package com.dss.miguel.trivialmiguelfernandezvillegas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class ScoreActivity extends AppCompatActivity {

    public static final String PREFS_KEY = "myPrefs";
    public static final String ALL_QUESTIONS = "all_questions";
    public static final String RIGHT_QUESTIONS = "right_questions";
    public static final String SUM_TIME = "sum_time";
    private int score, time_answer;
    private TextView score_text;
    private Button back_menu, est_button, other_game;
    private static Activity context;
    private HashMap<String, Integer> total_results;

    public static DBHelper db;
    private ArrayList<Integer> id_questions;
    private Question question;
    private int i_question=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        score = getIntent().getIntExtra("score", 0);
        time_answer = getIntent().getIntExtra("time_answer", 0);
        score_text = (TextView) findViewById(R.id.score_text);
        other_game = (Button) findViewById(R.id.other_game);
        back_menu = (Button) findViewById(R.id.back_menu);

        score_text.setText("Has acertado " + score + " de " + TrivialActivity.getNumQuestions() + " preguntas.");
        if(score == TrivialActivity.getNumQuestions())
            score_text.setText("Has acertado " + score + " de " + TrivialActivity.getNumQuestions() + " preguntas. Trivial completado con éxito.");


        id_questions = randomArrayList(MainActivity.NUM_QUESTIONS);
        initDB();
        question = db.getQuestion(id_questions.get(i_question));
        final int score0=0;

        other_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (question.getType() == Question.IMAGE_QUESTION) {
                    Intent intent = new Intent(ScoreActivity.this, TrivialImageActivity.class);
                    intent.putIntegerArrayListExtra("id_questions", id_questions);
                    intent.putExtra("i_question", i_question);
                    intent.putExtra("score", score0);
                    intent.putExtra("time_answer", 0);
                    startActivity(intent);
                    /*finish();*/
                } else {
                    Intent intent = new Intent(ScoreActivity.this, TrivialActivity.class);
                    intent.putIntegerArrayListExtra("id_questions", id_questions);
                    intent.putExtra("i_question", i_question);
                    intent.putExtra("score", score0);
                    intent.putExtra("time_answer", 0);
                    startActivity(intent);
                    /*finish();*/
                }
            }
        });

        back_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        est_button = (Button) findViewById(R.id.estadisticas_button);
        est_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this, StatsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        context = this;



        updatePlayerStats();
    }

    private void updatePlayerStats(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(ALL_QUESTIONS, sharedPreferences.getInt(ALL_QUESTIONS, 0) + MainActivity.NUM_QUESTIONS);
        editor.putInt(RIGHT_QUESTIONS, sharedPreferences.getInt(RIGHT_QUESTIONS, 0) + score);
        editor.putInt(SUM_TIME, sharedPreferences.getInt(SUM_TIME, 0) + time_answer);



        editor.commit();

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
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



    private void initDB(){
        db = new DBHelper(this);
        try {
            db.createDataBase();
            db.openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> randomArrayList(int n){
        ArrayList<Integer> list = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < n; i++)
        {
            int aux = random.nextInt(MainActivity.NUM_AVAIBLE_QUESTIONS)+1;
            while(list.contains(aux)){
                aux = random.nextInt(MainActivity.NUM_AVAIBLE_QUESTIONS)+1;
            }
            list.add(aux);
        }
        return list;
    }
}
