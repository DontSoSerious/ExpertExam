package com.example.admin.expertexam;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Result implements Serializable{

    private QuestionBank questionBank;
    private int[] selectedOption;
    private int[][] randomizeOptions;
    private int[] shuffleQuestion;
    private String startDateTime;
    private String endDateTime;

    public Result() {
    }

    public Result(QuestionBank questionBank) {
        Calendar c = Calendar.getInstance();
        startDateTime = new SimpleDateFormat("dd MMM yyyy hh:mm a").format(c.getTime());
        this.questionBank = questionBank;
        selectedOption = new int[questionBank.getQuestionSize()];
        randomizeOptions = new int[questionBank.getQuestionSize()][4];
        for (int i = 0; i < questionBank.getQuestionSize(); i++) {
            selectedOption[i] = -1;
            randomizeOptions[i] = questionBank.getQuestion(i).shuffleOptions();
        }
        shuffleQuestion = questionBank.shuffleIndex();
    }


    public QuestionBank getQuestionBank() {
        return questionBank;
    }

    public void setQuestionBank(QuestionBank questionBank) {
        this.questionBank = questionBank;
    }

    public int getSelectedOption(int index) {
        return selectedOption[index];
    }

    public int[] getSelectedOptions() {
        return selectedOption;
    }

    public void setSelectedOption(int questionNumber, int value) {
        this.selectedOption[questionNumber] = value;
    }

    public void setSelectedOptions(int[] selectedOptions) {
        this.selectedOption = selectedOptions;
    }

    public boolean isQuestionOptionsRandomize(int questionNumber) {
        return randomizeOptions[questionNumber][0] == -1;
    }

    public int getRandomizeOption(int questionNumber, int index) {
        return randomizeOptions[questionNumber][index];
    }

    public void setRandomizeOptions(int questionNumber, int[] shuffledOption) {
        this.randomizeOptions[questionNumber] = shuffledOption;
    }

    public int getShuffleQuestion(int index) {
        return shuffleQuestion[index];
    }

    public int[] getShuffleQuestions() {
        return shuffleQuestion;
    }

    public void setShuffleQuestions(int[] shuffleQuestion) {
        this.shuffleQuestion = shuffleQuestion;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public int getNumberOfCorrect() {
        int countCorrect = 0;
        for (int i = 0; i < getQuestionBank().getQuestionSize(); i++) {
            if (getQuestionBank().getQuestion(getShuffleQuestion(i)).getAnswer() == getSelectedOption(i)) {
                countCorrect++;
            }
        }
        return countCorrect;
    }
}
