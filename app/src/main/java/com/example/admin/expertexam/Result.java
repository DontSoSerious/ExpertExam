package com.example.admin.expertexam;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Result implements Serializable{

    private QuestionBank questionBank;
    private int[] selectedOption;
    private int[] shuffledQuestion;
    private String startDateTime;
    private String endDateTime;

    public Result() {
    }

    public Result(QuestionBank questionBank) {
        Calendar c = Calendar.getInstance();
        startDateTime = new SimpleDateFormat("dd MMM yyyy hh:mm a").format(c.getTime());
        this.questionBank = questionBank;
        selectedOption = new int[questionBank.getQuestionSize()];
        for (int i = 0; i < questionBank.getQuestionSize(); i++) {
            selectedOption[i] = -1;
        }
        shuffledQuestion = questionBank.shuffleIndex();
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


    public int getShuffleQuestion(int index) {
        return shuffledQuestion[index];
    }

    public int[] getShuffleQuestions() {
        return shuffledQuestion;
    }

    public void setShuffleQuestions(int[] shuffleQuestion) {
        this.shuffledQuestion = shuffleQuestion;
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
