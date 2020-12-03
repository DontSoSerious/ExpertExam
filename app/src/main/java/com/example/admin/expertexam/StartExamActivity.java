package com.example.admin.expertexam;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class StartExamActivity extends AppCompatActivity {

    private TextView tvQuestionTitle;
    private TextView tvQuestionText;
    private RadioGroup rgOptions;
    private RadioButton optionA;
    private RadioButton optionB;
    private RadioButton optionC;
    private RadioButton optionD;
    private Button btnLeft;
    private Button btnRight;
    private int currentIndex;
    private int[][] randomizdeOptions;
    private Result examResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_exam);
        Bundle bundle = getIntent().getExtras();
        QuestionBank qb = (QuestionBank) bundle.getSerializable(Dashboard.EXTRA_QUESTION_BANK);
        getSupportActionBar().setTitle(qb.getTitle());

        examResult = new Result(qb);
        int qbSize = examResult.getQuestionBank().getQuestionSize();
        randomizdeOptions = new int[qbSize][4];
        for (int i = 0; i < qbSize; i++) {
            randomizdeOptions[i] = shuffleOptionsIndex();
        }
        currentIndex = 0;

        tvQuestionText = (TextView) findViewById(R.id.tv_question_text);
        tvQuestionTitle = (TextView) findViewById(R.id.tv_question_title);
        optionA = (RadioButton) findViewById(R.id.rb_optionA);
        optionB = (RadioButton) findViewById(R.id.rb_optionB);
        optionC = (RadioButton) findViewById(R.id.rb_optionC);
        optionD = (RadioButton) findViewById(R.id.rb_optionD);
        rgOptions = (RadioGroup) findViewById(R.id.rg_options);
        btnLeft = (Button) findViewById(R.id.btn_left);
        btnRight = (Button) findViewById(R.id.btn_right);

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeProceed();
                currentIndex--;
                updateUI();
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeProceed();
                if (currentIndex == examResult.getQuestionBank().getQuestionSize() - 1) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(StartExamActivity.this);
                    alertBuilder.setCancelable(false)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to submit?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String endDateTime = new SimpleDateFormat("dd MMM yyyy hh:mm a").format(Calendar.getInstance().getTime());
                                    examResult.setEndDateTime(endDateTime);
                                    FirebaseQuery.insertResult(examResult);
                                    Intent i = new Intent(StartExamActivity.this, ViewResultActivity.class);
                                    i.putExtra(ViewResultActivity.EXTRA_SELECTED_RESULT, examResult);
                                    startActivity(i);
                                    finish();
                                }
                            })
                            .setNegativeButton("Cancel", null);
                    alertBuilder.show();
                }
                else {
                    currentIndex++;
                    updateUI();
                }
            }
        });
        updateUI();
    }

    private void updateUI() {
        if (currentIndex == 0) {
            btnLeft.setVisibility(View.INVISIBLE);
        }
        else {
            btnLeft.setVisibility(View.VISIBLE);
        }

        if (currentIndex == examResult.getQuestionBank().getQuestionSize() - 1) {
            btnRight.setText("Submit");
        }
        else {
            btnRight.setText("Next");
        }

        if (examResult.getSelectedOption(currentIndex) == -1) {
            rgOptions.clearCheck();
        }
        else {
            int selected = examResult.getSelectedOption(currentIndex);
            for (int i = 0; i < 4; i++) {
                if (selected == randomizdeOptions[currentIndex][0]) {
                    optionA.setChecked(true);
                }
                else if (selected == randomizdeOptions[currentIndex][1]) {
                    optionB.setChecked(true);
                }
                else if (selected == randomizdeOptions[currentIndex][2]) {
                    optionC.setChecked(true);
                }
                else if (selected == randomizdeOptions[currentIndex][3]) {
                    optionD.setChecked(true);
                }
            }
        }

        Question selectedQuestion = examResult.getQuestionBank().getQuestion(examResult.getShuffleQuestion(currentIndex));
        tvQuestionTitle.setText("Question " + (currentIndex + 1) + "/" + examResult.getQuestionBank().getQuestionSize());
        tvQuestionText.setText(selectedQuestion.getQuestionText());
        optionA.setText(selectedQuestion.getOptionString(randomizdeOptions[currentIndex][0]));
        optionB.setText(selectedQuestion.getOptionString(randomizdeOptions[currentIndex][1]));
        optionC.setText(selectedQuestion.getOptionString(randomizdeOptions[currentIndex][2]));
        optionD.setText(selectedQuestion.getOptionString(randomizdeOptions[currentIndex][3]));
    }

    private void beforeProceed() {
        int checkedId = rgOptions.getCheckedRadioButtonId();
        if (checkedId == optionA.getId()) {
            examResult.setSelectedOption(currentIndex, randomizdeOptions[currentIndex][0]);
        }
        else if (checkedId == optionB.getId()) {
            examResult.setSelectedOption(currentIndex, randomizdeOptions[currentIndex][1]);
        }
        else if (checkedId == optionC.getId()) {
            examResult.setSelectedOption(currentIndex, randomizdeOptions[currentIndex][2]);
        }
        else if (checkedId == optionD.getId()) {
            examResult.setSelectedOption(currentIndex, randomizdeOptions[currentIndex][3]);
        }
    }

    public int[] shuffleOptionsIndex() {
        List<Integer> temp = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            temp.add(i);
        }
        Collections.shuffle(temp);
        int[] shuffledOptions = new int[4];
        for (int i = 0; i < 4; i++) {
            shuffledOptions[i] = temp.get(i);
        }
        return shuffledOptions;
    }
}
