package com.dss.miguel.trivialmiguelfernandezvillegas;

/**
 * Created by Miguel on 05/01/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DBHelper
 *
 * Sirve de ayuda para crear y modificar una base de datos local que podemos crear y modificar en tiempo de ejecución.
 * Funciona mediante el modelo de datos Question. Se incluyen todas las funciones CRUD.
 *
 * http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 *
 * Conexión a base de datos existente
 *
 * http://blog.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
 *
 * Para conocer el Path
 *
 * http://stackoverflow.com/questions/9109438/how-to-use-an-existing-database-with-an-android-application
 *
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_PATH = "";
    private static final String DATABASE_NAME = "tabla_questions";
    private static final String TABLE_QUESTIONS = "questions_table";

    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_QUESTION = "question";
    private static final String KEY_R_ANSW = "right_answer";
    private static final String KEY_W_ANSW_1 = "wrong_answer_1";
    private static final String KEY_W_ANSW_2 = "wrong_answer_2";
    private static final String KEY_W_ANSW_3 = "wrong_answer_3";

    private SQLiteDatabase questions_db;
    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        if(android.os.Build.VERSION.SDK_INT >= 17){
            DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        }
        else
        {
            DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
    }


    public void createDataBase() throws IOException{
        boolean dbExist = checkDataBase();

        if(!dbExist){
            this.getReadableDatabase();

            try{
                copyDataBase();
            }catch(IOException e ){
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;

        try{
            String myPath = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            e.printStackTrace();
        }

        if(checkDB !=null)
            checkDB.close();

        return checkDB !=null ? true : false;
    }


    private void copyDataBase() throws IOException{

        InputStream myInput = context.getResources().openRawResource(R.raw.tabla_questions);
        String outFileName = DATABASE_PATH + DATABASE_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException{
        String myPath = DATABASE_PATH + DATABASE_NAME;
        questions_db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close(){
        if(questions_db!=null)
            questions_db.close();

        super.close();
    }


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_QUESTIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_QUESTION + " TEXT,"
                + KEY_R_ANSW + " TEXT," + KEY_W_ANSW_1 + " TEXT,"
                + KEY_W_ANSW_2 + " TEXT," + KEY_W_ANSW_3 + " TEXT," + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        */
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        onCreate(db);
        */
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    void addQuestion(Question question) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID, question.getId());
        values.put(KEY_TYPE, question.getType());
        values.put(KEY_QUESTION, question.getQuestion());
        values.put(KEY_R_ANSW, question.getRight_answer());
        values.put(KEY_W_ANSW_1, question.getWrong_answers().get(0));
        values.put(KEY_W_ANSW_2, question.getWrong_answers().get(1));
        values.put(KEY_W_ANSW_3, question.getWrong_answers().get(2));


        if (questions_db != null) {
            questions_db.insert(TABLE_QUESTIONS, null, values);
        }
    }


    Question getQuestion(int id){
        Cursor cursor = questions_db.query(TABLE_QUESTIONS, new String[]{KEY_ID, KEY_TYPE, KEY_QUESTION, KEY_R_ANSW, KEY_W_ANSW_1, KEY_W_ANSW_2, KEY_W_ANSW_3}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if(cursor!=null)
            cursor.moveToFirst();

        ArrayList<String> wrong_answers = new ArrayList<>();
        wrong_answers.add(cursor.getString(4));
        wrong_answers.add(cursor.getString(5));
        wrong_answers.add(cursor.getString(6));

        Question question = new Question(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), cursor.getString(2), cursor.getString(3), wrong_answers);



        return question;
    }

    public List<Question> getAllQuestions(){
        List<Question> questions = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTIONS;

        Cursor cursor = questions_db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ArrayList<String> wrong_answers = new ArrayList<>();
                wrong_answers.add(cursor.getString(4));
                wrong_answers.add(cursor.getString(5));
                wrong_answers.add(cursor.getString(6));

                Question question = new Question(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), cursor.getString(2), cursor.getString(3), wrong_answers);



                questions.add(question);
            } while (cursor.moveToNext());
        }

        return questions;
    }

    public int updateQuestion(Question question){

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, question.getType());
        values.put(KEY_QUESTION, question.getQuestion());
        values.put(KEY_R_ANSW, question.getRight_answer());
        values.put(KEY_W_ANSW_1, question.getWrong_answers().get(0));
        values.put(KEY_W_ANSW_2, question.getWrong_answers().get(1));
        values.put(KEY_W_ANSW_3, question.getWrong_answers().get(2));

        return questions_db.update(TABLE_QUESTIONS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(question.getId())});

    }

    public void deleteQuestion(Question question) {
        questions_db.delete(TABLE_QUESTIONS, KEY_ID + " = ?",
                new String[]{String.valueOf(question.getId())});
    }


    public int getQuestionsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_QUESTIONS;
        Cursor cursor = questions_db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }
}
