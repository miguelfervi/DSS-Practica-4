package com.dss.miguel.trivialmiguelfernandezvillegas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final int NUM_QUESTIONS=5;
    public static final int NUM_AVAIBLE_QUESTIONS = 8;
    private Button startButton, statsButton, otrosButton;


    public static DBHelper db;
    private ArrayList<Integer> id_questions;
    private Question question;
    private int i_question=0;
    private int score=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        id_questions = randomArrayList(NUM_QUESTIONS);
        initDB();

        question = db.getQuestion(id_questions.get(i_question));

        initView();
    }


    private void initView(){
        startButton = (Button) findViewById(R.id.button_start);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (question.getType() == Question.IMAGE_QUESTION) {
                    Intent intent = new Intent(MainActivity.this, TrivialImageActivity.class);
                    intent.putIntegerArrayListExtra("id_questions", id_questions);
                    intent.putExtra("i_question", i_question);
                    intent.putExtra("score", score);
                    intent.putExtra("time_answer", 0);
                    startActivity(intent);
                    /*finish();*/
                } else {
                    Intent intent = new Intent(MainActivity.this, TrivialActivity.class);
                    intent.putIntegerArrayListExtra("id_questions", id_questions);
                    intent.putExtra("i_question", i_question);
                    intent.putExtra("score", score);
                    intent.putExtra("time_answer", 0);
                    startActivity(intent);

                }
            }
        });

        statsButton = (Button) findViewById(R.id.button_stats);

        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                startActivity(intent);
                /*finish();*/
            }
        });

        otrosButton = (Button) findViewById(R.id.button_muro);

        otrosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://ploppy.igme.es/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });




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

    public static DBHelper getDB(){
        return db;
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
        /*if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.imageButton) {
            Toast toast1 = Toast.makeText(getApplicationContext(),
                    "Funcionalidades en desarrollo", Toast.LENGTH_SHORT);
            toast1.show();
        }*/

        return super.onOptionsItemSelected(item);
    }


}
