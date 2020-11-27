package com.example.admin.expertexam;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditProfileActivity extends AppCompatActivity {
    private FirebaseUser account;
    private EditText tvUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        account = FirebaseAuth.getInstance().getCurrentUser();
        tvUsername = findViewById(R.id.edit_username);
        tvUsername.setText(account.getDisplayName());
    }

    public void btnUpdate_onClick(View view) {
        if (checkInput(tvUsername)) {
            String username = tvUsername.getText().toString().trim();
            if (username != account.getDisplayName()) {
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build();
                account.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            tvUsername.setError(task.getException().getMessage());
                        }
                        else {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                });
            }
        }
    }

    private boolean checkInput(EditText editText) {
        String input = editText.getText().toString().trim();
        if (input.isEmpty()) {
            editText.setError("Field cannot be empty");
            return false;
        }
        else {
            editText.setError(null);
        }
        return true;
    }
}
