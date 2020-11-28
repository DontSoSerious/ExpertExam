package com.example.admin.expertexam;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;

public class ViewResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ResultsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_results);
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Result> results = FirebaseQuery.fetchResults(dataSnapshot);
                Collections.reverse(results);
                Log.e("ERROR", results.size() + "");
                recyclerView = findViewById(R.id.results_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(ViewResultsActivity.this));
                mAdapter = new ResultsAdapter(results);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsHolder> {

        private List<Result> results;
        public ResultsAdapter(List<Result> results) {
            this.results = results;
        }

        @NonNull
        @Override
        public ResultsHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = LayoutInflater.from(ViewResultsActivity.this).inflate(R.layout.results_item_layout, parent, false);
            return new ResultsHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ResultsHolder resultsHolder, int i) {
            final Result selectedResult = results.get(i);
            resultsHolder.tvScore.setText(selectedResult.getNumberOfCorrect() + "/" + selectedResult.getQuestionBank().getQuestionSize());
            resultsHolder.tvTitle.setText(selectedResult.getQuestionBank().getTitle());

            resultsHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ViewResultsActivity.this, ViewResultActivity.class);
                    i.putExtra(ViewResultActivity.EXTRA_SELECTED_RESULT, selectedResult);
                    startActivity(i);
                }
            });

        }

        @Override
        public int getItemCount() {
            return results.size();
        }

        public class ResultsHolder extends RecyclerView.ViewHolder {
            private RelativeLayout relativeLayout;
            private TextView tvTitle;
            private TextView tvScore;
            public ResultsHolder(View view) {
                super(view);
                relativeLayout = view.findViewById(R.id.result_relative_layout);
                tvTitle = view.findViewById(R.id.tv_results_title);
                tvScore = view.findViewById(R.id.tv_results_score);
            }
        }
    }
}
