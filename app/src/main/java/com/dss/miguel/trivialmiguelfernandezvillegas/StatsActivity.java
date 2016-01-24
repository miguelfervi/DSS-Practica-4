package com.dss.miguel.trivialmiguelfernandezvillegas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Miguel on 05/01/2016.
 */
public class StatsActivity extends AppCompatActivity {

    private int all_questions, right_questions, wrong_questions, sum_time;
    private float  success_percentage, mean_response_time;

    private TextView tv_all, tv_right, tv_wrong, tv_success, tv_mean;
    private Button back_menu, delete_stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        initView();
        addButtonListeners();
        loadScore();
        addTextScore();

    }

    private void initView(){
        tv_all = (TextView) findViewById(R.id.all_questions);
        tv_right = (TextView) findViewById(R.id.right_questions);
        tv_wrong = (TextView) findViewById(R.id.wrong_questions);
        tv_success = (TextView) findViewById(R.id.success_percentage);
        tv_mean = (TextView) findViewById(R.id.mean_response_time);
        back_menu = (Button) findViewById(R.id.back_menu);
        delete_stats = (Button) findViewById(R.id.delete_stats);
    }

    private void addButtonListeners(){
        back_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        delete_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });
    }

    private void loadScore(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ScoreActivity.PREFS_KEY, Context.MODE_PRIVATE);

        all_questions = sharedPreferences.getInt(ScoreActivity.ALL_QUESTIONS, 0);
        right_questions = sharedPreferences.getInt(ScoreActivity.RIGHT_QUESTIONS, 0);
        sum_time = sharedPreferences.getInt(ScoreActivity.SUM_TIME, 0);

        wrong_questions = all_questions - right_questions;
        if(all_questions != 0) {
            success_percentage = ((float ) right_questions) / all_questions * 100;
            mean_response_time = ((float ) sum_time) / all_questions;

        }else{
            success_percentage = 0;
            mean_response_time = 0;
        }

    }

    private void addTextScore(){
        tv_all.setText("Total preguntas : " + Integer.toString(all_questions));
        tv_right.setText("Respuestas correctas : " + Integer.toString(right_questions));
        tv_wrong.setText("Respuestas incorrectas : " + Integer.toString(wrong_questions));
        tv_success.setText("Porcentaje de acierto : " + String.format("%.01f", (float)success_percentage) + " %");
        tv_mean.setText("Tiempo medio de respuesta : " +  String.format("%.01f",(float) mean_response_time) + " segundos");
    }

    private void deleteScore(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ScoreActivity.PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(ScoreActivity.ALL_QUESTIONS, 0);
        editor.putInt(ScoreActivity.RIGHT_QUESTIONS, 0);
        editor.putInt(ScoreActivity.SUM_TIME, 0);

        editor.commit();

    }

    private void showDeleteDialog(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteScore();
                        loadScore();
                        addTextScore();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro que quieres borrar tus estadísticas?").setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


}
