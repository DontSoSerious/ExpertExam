package com.example.admin.expertexam;

import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Question implements Serializable{
    private String questionText;
    private String[] options;
    private int answer;

    public Question() {
    }

    public Question(Question q) {
        this.questionText = q.getQuestionText();
        this.options = new String[4];
        for (int i = 0; i < 4; i++) {
            this.options[i] = q.getOptionString(i);
        }
        this.answer = q.getAnswer();
    }

    public Question(String question_text, ArrayList<String> options, int answer) {
        this.questionText = question_text;
        this.options = new String[options.size()];
        for (int i = 0; i < options.size(); i++) {
            this.options[i] = options.get(i);
        }
        this.answer = answer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionString(int index) {
        return options[index];
    }

    public void setOptions(ArrayList<String> options) {
        this.options = new String[options.size()];
        for (int i = 0; i < options.size(); i++) {
            this.options[i] = options.get(i);
        }
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }
}
