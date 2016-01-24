package com.dss.miguel.trivialmiguelfernandezvillegas;

import java.util.ArrayList;

/**
 * Created by Miguel on 05/01/2016.
 */
public class Question {

    public static final int TEXT_QUESTION = 1;
    public static final int IMAGE_QUESTION = 2;

    private int id;
    private int type;
    private String question;
    private ArrayList<String> wrong_answers;
    private String right_answer;

    public Question(){
    }


    public Question(int id, int type, String question, String right_answer, ArrayList<String> wrong_answers){
        this.id=id;
        this.type=type;
        this.question = question;
        this.right_answer = right_answer;
        this.wrong_answers = wrong_answers;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getWrong_answers() {
        return wrong_answers;
    }

    public void setWrong_answers(ArrayList<String> wrong_answers) {
        this.wrong_answers = wrong_answers;
    }

    public String getRight_answer() {
        return right_answer;
    }

    public void setRight_answer(String right_answer) {
        this.right_answer = right_answer;
    }


}
