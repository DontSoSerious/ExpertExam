package com.example.admin.expertexam;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddExamActivity extends AppCompatActivity {
    public static final String EXTRA_QUESTION_BANK = "com.example.admin.expertexam.QUESTION_BANK";

    private TextView tvQuestionTitle;
    private EditText etQuestionText;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private EditText et1;
    private EditText et2;
    private EditText et3;
    private EditText et4;

    private Button btnPrev;
    private Button btnSubmit;
    private Button btnNext;

    QuestionBank questionBank;
    int currentIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);

        QuestionBank temp = (QuestionBank) getIntent().getSerializableExtra(EXTRA_QUESTION_BANK);
        questionBank = new QuestionBank(temp);
        currentIndex = 0;
        tvQuestionTitle = (TextView) findViewById(R.id.tv_question_title);
        etQuestionText = (EditText) findViewById(R.id.et_question_text);
        rb1 = (RadioButton) findViewById(R.id.rb_option1);
        rb2 = (RadioButton) findViewById(R.id.rb_option2);
        rb3 = (RadioButton) findViewById(R.id.rb_option3);
        rb4 = (RadioButton) findViewById(R.id.rb_option4);
        et1 = (EditText) findViewById(R.id.et_option1);
        et2 = (EditText) findViewById(R.id.et_option2);
        et3 = (EditText) findViewById(R.id.et_option3);
        et4 = (EditText) findViewById(R.id.et_option4);
        btnPrev = (Button) findViewById(R.id.btn_previous);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnNext = (Button) findViewById(R.id.btn_next);
        if (questionBank.getQuestionSize() > 0) {
            getSupportActionBar().setTitle("Edit Question Bank");
            btnSubmit.setText("Update");
        }

        rb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkRadioButton(rb1);
                }
            }
        });

        rb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkRadioButton(rb2);
                }
            }
        });

        rb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkRadioButton(rb3);
                }
            }
        });

        rb4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkRadioButton(rb4);
                }
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String questionText = etQuestionText.getText().toString().trim();
                String a1 = et1.getText().toString().trim();
                String a2 = et2.getText().toString().trim();
                String a3 = et3.getText().toString().trim();
                String a4 = et4.getText().toString().trim();
                if (questionText.isEmpty() && a1.isEmpty() && a2.isEmpty() && a3.isEmpty() && a4.isEmpty()) {
                    questionBank.removeQuestion(currentIndex);
                }
                else {
                    boolean check = beforeProceed();
                    if (!check)
                        return;
                }
                currentIndex--;
                updateUI();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex != questionBank.getQuestionSize() - 1) {
                    String questionText = etQuestionText.getText().toString().trim();
                    String a1 = et1.getText().toString().trim();
                    String a2 = et2.getText().toString().trim();
                    String a3 = et3.getText().toString().trim();
                    String a4 = et4.getText().toString().trim();
                    if (questionText.isEmpty() && a1.isEmpty() && a2.isEmpty() && a3.isEmpty() && a4.isEmpty()) {
                        questionBank.removeQuestion(currentIndex);
                        updateUI();
                        return;
                    }
                }
                boolean check = beforeProceed();
                if (!check)
                    return;
                currentIndex++;
                updateUI();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = beforeProceed();
                if (!check)
                    return;
                AlertDialog alertDialog = new AlertDialog.Builder(AddExamActivity.this)
                        .setTitle("Confirmation Message")
                        .setMessage("Are you confirm you want to add these questions into database?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseQuery.insertQuestionbank(questionBank);
                                setResult(RESULT_OK);
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                alertDialog.show();

            }
        });
        updateUI();
    }

    private boolean beforeProceed() {
        boolean check = true;
        check = checkInput(etQuestionText) && check;
        check = checkInput(et1) && check;
        check = checkInput(et2) && check;
        check = checkInput(et3) && check;
        check = checkInput(et4) && check;
        check = checkAnswerTextDuplicated() && check;
        int answer = -1;
        if (rb1.isChecked()) {
            answer = 0;
        }
        else if (rb2.isChecked()) {
            answer = 1;
        }
        else if (rb3.isChecked()) {
            answer = 2;
        }
        else if (rb4.isChecked()) {
            answer = 3;
        }
        else {
            Toast.makeText(this, "Please select the answer for this question", Toast.LENGTH_SHORT).show();
            check = false;
        }
        if (check) {
            String questionText = etQuestionText.getText().toString().trim();
            String ans1 = et1.getText().toString().trim();
            String ans2 = et2.getText().toString().trim();
            String ans3 = et3.getText().toString().trim();
            String ans4 = et4.getText().toString().trim();
            Question question = questionBank.getQuestion(currentIndex);
            question.setQuestionText(questionText);
            question.setAnswer(answer);
            ArrayList<String> temp = new ArrayList<>();
            temp.add(ans1);
            temp.add(ans2);
            temp.add(ans3);
            temp.add(ans4);
            question.setOptions(temp);
        }
        return check;
    }

    private boolean checkInput(EditText editText) {
        String input = editText.getText().toString().trim();
        if (input.isEmpty()) {
            editText.setError("Field cannot be empty");
            return false;
        }
        return true;
    }

    private boolean checkDuplicateAnswerOfTwoEditText(EditText editText1, EditText editText2) {
        String ans1 = editText1.getText().toString().trim();
        String ans2 = editText2.getText().toString().trim();

        if (!ans1.isEmpty() && !ans2.isEmpty() && ans1.equals(ans2)) {
            editText1.setError("The answer is repeated");
            editText2.setError("The answer is repeated");
            return false;
        }
        return true;
    }

    private boolean checkAnswerTextDuplicated() {
        boolean check = true;
        check = checkDuplicateAnswerOfTwoEditText(et1, et2) && check;
        check = checkDuplicateAnswerOfTwoEditText(et1, et3) && check;
        check = checkDuplicateAnswerOfTwoEditText(et1, et4) && check;
        check = checkDuplicateAnswerOfTwoEditText(et2, et3) && check;
        check = checkDuplicateAnswerOfTwoEditText(et2, et4) && check;
        check = checkDuplicateAnswerOfTwoEditText(et3, et4) && check;
        return check;
    }

    private void updateUI() {
        if (currentIndex == 0) {
            btnPrev.setVisibility(View.INVISIBLE);
        }
        else {
            btnPrev.setVisibility(View.VISIBLE);
        }
        if (currentIndex == questionBank.getQuestionSize()) {
            btnNext.setText("Add");
            Question question = new Question();
            questionBank.addQuestion(question);
            etQuestionText.setText(null);
            et1.setText(null);
            et2.setText(null);
            et3.setText(null);
            et4.setText(null);
            rb1.setChecked(false);
            rb2.setChecked(false);
            rb3.setChecked(false);
            rb4.setChecked(false);
        }
        else {
            btnNext.setText("Next");
            Question question = questionBank.getQuestion(currentIndex);
            etQuestionText.setText(question.getQuestionText());
            et1.setText(question.getOptionString(0));
            et2.setText(question.getOptionString(1));
            et3.setText(question.getOptionString(2));
            et4.setText(question.getOptionString(3));
            switch (question.getAnswer()) {
                case 0:
                    checkRadioButton(rb1);
                    break;
                case 1:
                    checkRadioButton(rb2);
                    break;
                case 2:
                    checkRadioButton(rb3);
                    break;
                case 3:
                    checkRadioButton(rb4);
                    break;
            }
        }
        tvQuestionTitle.setText("Question " + (currentIndex + 1) + "/" + (questionBank.getQuestionSize()));
    }

    private void checkRadioButton(RadioButton rb) {
        switch (rb.getId()) {
            case R.id.rb_option1:
                rb1.setChecked(true);
                rb2.setChecked(false);
                rb3.setChecked(false);
                rb4.setChecked(false);
                break;
            case R.id.rb_option2:
                rb1.setChecked(false);
                rb2.setChecked(true);
                rb3.setChecked(false);
                rb4.setChecked(false);
                break;
            case R.id.rb_option3:
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(true);
                rb4.setChecked(false);
                break;
            case R.id.rb_option4:
                rb1.setChecked(false);
                rb2.setChecked(false);
                rb3.setChecked(false);
                rb4.setChecked(true);
                break;
        }
    }
}
