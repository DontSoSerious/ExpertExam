package com.example.admin.expertexam;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText mEtCurrentPassword;
    private EditText mEtNewPassword;
    private EditText mEtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mEtCurrentPassword = (EditText) findViewById(R.id.et_current_password);
        mEtConfirmPassword = (EditText) findViewById(R.id.et_new_confirm_password);
        mEtNewPassword = (EditText) findViewById(R.id.et_new_password);
    }

    public void save_onClick(View view) {

        boolean check = true;
        check = checkInput(mEtCurrentPassword) && check;
        check = checkInput(mEtNewPassword) && check;
        check = checkInput(mEtConfirmPassword) && check;

        if (check) {
            String currentPassword = mEtCurrentPassword.getText().toString();
            final String newPassword = mEtNewPassword.getText().toString();
            String confirmPassword = mEtConfirmPassword.getText().toString();
            if (!newPassword.equals(confirmPassword)) {
                mEtNewPassword.setError("Both new password and confirm password are not same");
                return;
            }
            final FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();
            String email = account.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

            account.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        account.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    setResult(RESULT_OK);
                                    finish();
                                }
                                else {
                                    Toast.makeText(ChangePasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(ChangePasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public boolean checkInput(EditText editText) {
        String input = editText.getText().toString();
        if (input.isEmpty()) {
            editText.setError("Field cannot be empty");
            return false;
        }
        else {
            editText.setError(null);
        }
        switch (editText.getId()) {
            case R.id.et_new_password:
                if (input.length() < 8) {
                    editText.setError("Password cannot be less than 8 characters");
                    return false;
                }
                else {
                    editText.setError(null);
                }
                break;
            case R.id.et_new_confirm_password:
                if (input.length() < 8) {
                    editText.setError("Confirm Password cannot be less than 8 characters");
                    return false;
                }
                else {
                    editText.setError(null);
                }
                break;
        }
        return true;
    }
}
