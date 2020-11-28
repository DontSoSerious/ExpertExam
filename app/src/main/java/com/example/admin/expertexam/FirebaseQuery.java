package com.example.admin.expertexam;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.List;

public class FirebaseQuery {
    public static List<QuestionBank> fetchQuestionBanks(DataSnapshot dataSnapshot) {
        List<QuestionBank> questionBanks = new ArrayList<>();
        dataSnapshot = dataSnapshot.child("question_bank");
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            QuestionBank qb = fetchQuestionBank(ds);
            questionBanks.add(qb);
        }
        return questionBanks;
    }

    public static void insertResult(Result result) {
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("result");
        String uuid = reff.push().getKey();
        DatabaseReference childReff = reff.child(uuid);
        childReff.child("userUUID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        childReff.child("questionbank").setValue(result.getQuestionBank().getTitle());

        List<Integer> selectedOptions = arrayToList(result.getSelectedOptions());
        List<Integer> shuffleQuestions = arrayToList(result.getShuffleQuestions());

        childReff.child("selectedOption").setValue(selectedOptions);
        childReff.child("shuffleQuestion").setValue(shuffleQuestions);
        childReff.child("startDateTime").setValue(result.getStartDateTime());
        childReff.child("endDateTime").setValue(result.getEndDateTime());
    }

    public static List<Result> fetchResults(DataSnapshot dataSnapshot) {
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DataSnapshot resultDS = dataSnapshot.child("result");
        DataSnapshot questionBankDS = dataSnapshot.child("question_bank");

        List<Result> results = new ArrayList<>();
        for (DataSnapshot childDS: resultDS.getChildren()) {
            if (childDS.child("userUUID").getValue().toString().equals(uuid)) {
                Result result = new Result();
                result.setEndDateTime(childDS.child("endDateTime").getValue().toString());
                result.setStartDateTime(childDS.child("startDateTime").getValue().toString());
                GenericTypeIndicator<List<Integer>> genericTypeIndicator = new GenericTypeIndicator<List<Integer>>() {};
                result.setSelectedOptions(listToArray(childDS.child("selectedOption").getValue(genericTypeIndicator)));
                result.setShuffleQuestions(listToArray(childDS.child("shuffleQuestion").getValue(genericTypeIndicator)));

                String questionBankTitle = childDS.child("questionbank").getValue().toString();
                result.setQuestionBank(fetchQuestionBank(questionBankDS.child(questionBankTitle)));
                results.add(result);
            }
        }
        return results;
    }


    private static List<Integer> arrayToList(int[] array) {
        List<Integer> temp = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            temp.add(array[i]);
        }
        return temp;
    }

    private static int[] listToArray(List<Integer> list) {
        int[] temp = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            temp[i] = list.get(i).intValue();
        }
        return temp;
    }

    private static QuestionBank fetchQuestionBank(DataSnapshot ds) {
        QuestionBank qb = new QuestionBank();
        qb.setTitle(ds.getKey());

        List<Question> questions = new ArrayList<>();
        for (DataSnapshot ds1: ds.getChildren()) {
            int answer = Integer.parseInt(ds1.child("answer").getValue().toString());
            String questionText = ds1.child("questiontext").getValue().toString();
            ArrayList<String> options = new ArrayList<>();
            options.add(ds1.child("options").child("0").getValue().toString());
            options.add(ds1.child("options").child("1").getValue().toString());
            options.add(ds1.child("options").child("2").getValue().toString());
            options.add(ds1.child("options").child("3").getValue().toString());
            Question question = new Question(questionText, options, answer);
            questions.add(question);
        }
        qb.setQuestions(questions);
        return qb;
    }

    public static void insertQuestionbank(QuestionBank qb) {
        DatabaseReference questionBankDS = FirebaseDatabase.getInstance().getReference().child("question_bank").child(qb.getTitle());
        for (int i = 0; i < qb.getQuestionSize(); i++) {
            Question question = qb.getQuestion(i);
            DatabaseReference subDS = questionBankDS.child(String.valueOf(i));
            subDS.child("answer").setValue(question.getAnswer());
            subDS.child("questiontext").setValue(question.getQuestionText());
            for (int j = 0; j < 4; j++) {
                subDS.child("options").child(String.valueOf(j)).setValue(question.getOptionString(j));
            }
        }
    }
}
