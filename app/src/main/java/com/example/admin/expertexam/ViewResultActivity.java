package com.example.admin.expertexam;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewResultActivity extends AppCompatActivity {
    public static final String EXTRA_SELECTED_RESULT = "com.example.admin.expertexam.SELECTED_RESULT";
    private Result result;
    @BindView(R.id.result_recycler_view)
    RecyclerView recyclerView;
    private ResultAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_result);
        ButterKnife.bind(this);
        result = (Result) getIntent().getSerializableExtra(EXTRA_SELECTED_RESULT);
//        recyclerView = findViewById(R.id.result_recycler_view);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new ResultAdapter(this, result);
        recyclerView.setAdapter(mAdapter);

        RecyclerItemDecoration recyclerItemDecoration = new RecyclerItemDecoration(this, getResources().getDimensionPixelSize(R.dimen.header_height), true, getSectionCallback(result));
        recyclerView.addItemDecoration(recyclerItemDecoration);
    }

    private RecyclerItemDecoration.SectionCallback getSectionCallback(final Result result) {
        return new RecyclerItemDecoration.SectionCallback() {
            @Override
            public boolean isSectionHeader(int pos) {
                if (pos == 0 || pos == 1)
                    return true;
                int questionNumber = result.getShuffleQuestion(pos - 1);
                int lastQuestionNumber = result.getShuffleQuestion(pos - 2);
                return result.getQuestionBank().getQuestion(questionNumber).getQuestionText() != result.getQuestionBank().getQuestion(lastQuestionNumber).getQuestionText();
            }

            @Override
            public String getSectionHeaderName(int pos) {
                if (pos == 0) {
                    return "Title: " + result.getQuestionBank().getTitle();
                }
                return "Question " + pos +"/" + result.getQuestionBank().getQuestionSize();
            }
        };
    }
    class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultHolder> {
        private static final int HEADER = 0;
        private static final int CONTENT = 1;
        Result result;
        Context context;

        public ResultAdapter(Context context,Result result) {
            this.context = context;
            this.result = result;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return HEADER;
            return CONTENT;
        }

        @NonNull
        @Override
        public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == HEADER){
                view = LayoutInflater.from(context).inflate(R.layout.result_header_layout, parent, false);
            }
            else {
                view = LayoutInflater.from(context).inflate(R.layout.result_item_layout, parent, false);
            }
            ResultHolder resultHolder = new ResultHolder(view, viewType);
            return resultHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ResultHolder resultHolder, int i) {
            if (resultHolder.viewType == HEADER) {
                String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                int numberOfCorrect = result.getNumberOfCorrect();
                String startedDateTime = result.getStartDateTime();
                String endedDateTime = result.getEndDateTime();
                int totalQuestion = result.getQuestionBank().getQuestionSize();

                resultHolder.tvHeaderStarted.setText("Started: " + startedDateTime);
                resultHolder.tvHeaderEnded.setText("Ended: " + endedDateTime);
                resultHolder.tvHeaderUser.setText("User: " + username);
                resultHolder.tvHeaderScore.setText("Score: " + numberOfCorrect + "/" + totalQuestion);
            }
            else {
                int questionNumber = result.getShuffleQuestion(i - 1);
                Question question = result.getQuestionBank().getQuestion(questionNumber);

                String questionText = question.getQuestionText();
                int selectedAnswer = result.getSelectedOption(i - 1);
                int finalAnswer = question.getAnswer();

                String finalAnswerText = question.getOptionString(finalAnswer);
                boolean isCorrect = selectedAnswer == finalAnswer;

                resultHolder.tvQuestionText.setText(questionText);
                resultHolder.tvFinalAnswer.setText("Correct Answer: " + finalAnswerText);
                if (!isCorrect) {
                    String selectedAnswerText = "N/A";
                    if (selectedAnswer != -1) {
                        selectedAnswerText = question.getOptionString(selectedAnswer);
                    }
                    resultHolder.tvSelectedAnswer.setVisibility(View.VISIBLE);
                    resultHolder.tvSelectedAnswer.setText("Selected Answer: " + selectedAnswerText);
                    resultHolder.tvStatus.setText("Oh no. You got it wrong");
                    resultHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.wrong_answer));
                }
            }

        }

        @Override
        public int getItemCount() {
            return result.getQuestionBank().getQuestionSize() + 1;
        }

        public class ResultHolder extends RecyclerView.ViewHolder {
            private int viewType;
            private TextView tvHeaderUser, tvHeaderStarted, tvHeaderEnded, tvHeaderScore;
            private TextView tvQuestionText, tvSelectedAnswer, tvFinalAnswer, tvStatus;
            public ResultHolder(@NonNull View itemView, int viewType) {
                super(itemView);
                this.viewType = viewType;
                if (viewType == HEADER) {
                    tvHeaderUser = itemView.findViewById(R.id.header_user);
                    tvHeaderStarted = itemView.findViewById(R.id.header_started);
                    tvHeaderEnded = itemView.findViewById(R.id.header_ended);
                    tvHeaderScore = itemView.findViewById(R.id.header_score);
                }
                else {
                    tvQuestionText = itemView.findViewById(R.id.tv_result_question_text);
                    tvSelectedAnswer = itemView.findViewById(R.id.tv_selected_answer);
                    tvFinalAnswer = itemView.findViewById(R.id.tv_final_answer);
                    tvStatus= itemView.findViewById(R.id.tv_status);
                }
            }
        }

    }
}