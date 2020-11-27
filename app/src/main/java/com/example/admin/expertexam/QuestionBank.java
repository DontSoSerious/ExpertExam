package com.example.admin.expertexam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionBank implements Serializable{
    private String title;
    private List<Question> questions;

    public QuestionBank() {
        questions = new ArrayList<>();
    }

    public QuestionBank(String title) {
        questions = new ArrayList<>();
        this.title = title;
    }

    public QuestionBank(QuestionBank qb) {
        questions = new ArrayList<>();
        this.title = qb.getTitle();
        for (int i = 0; i < qb.getQuestionSize(); i++) {
            Question q = new Question(qb.getQuestion(i));
            this.questions.add(q);
        }
    }
    public QuestionBank(String title, List<Question> question) {
        this.title = title;
        this.questions = question;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    public Question getQuestion(int index) {
        return questions.get(index);
    }

    public void removeQuestion(int index) {
        questions.remove(index);
    }
    public int getQuestionSize() {
        return this.questions.size();
    }

    public int[] shuffleIndex() {
        List<Integer> numbers = new ArrayList<>();
        int[] shuffledNumbers = new int[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        for (int i = 0; i < questions.size(); i++) {
            shuffledNumbers[i] = numbers.get(i);
        }
        return shuffledNumbers;
    }
}
