package com.example.admin.expertexam;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtEmail;
    private EditText mEtPassword;
    private EditText mEtConfirmPassword;
    private EditText mEtUsername;
    private Button mBtnRegister;
    private FirebaseAuth mFirebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mEtEmail = (EditText) findViewById(R.id.et_email);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mEtConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        mEtUsername = (EditText) findViewById(R.id.et_username);
        mBtnRegister = (Button) findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        boolean check = true;
        check = checkInput(mEtUsername) && check;
        check = checkInput(mEtEmail) && check;
        check = checkInput(mEtPassword) && check;
        check = checkInput(mEtConfirmPassword) && check;


        if (check) {
            final String username = mEtUsername.getText().toString().trim();
            String email = mEtEmail.getText().toString().trim();
            String password = mEtPassword.getText().toString();
            String confirmPassword = mEtConfirmPassword.getText().toString();

            if (!password.equals(confirmPassword)) {
                mEtPassword.setError("Both password and confirm password are not same");
            }
            else {
                mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserProfileChangeRequest userProfile = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username).build();
                            mFirebaseAuth.getCurrentUser().updateProfile(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Log.i(getApplication().getPackageName(), "New user is created");
                                    else {
                                        Log.e(getApplication().getPackageName(), task.getException().getMessage());
                                    }
                                }
                            });
                            mFirebaseAuth.getCurrentUser().sendEmailVerification();
                            mFirebaseAuth.signOut();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private boolean checkInput(EditText editText) {
        String input = editText.getText().toString();
        if (input.isEmpty()) {
            editText.setError("Fields cannot be empty");
            return false;
        }
        else {
            editText.setError(null);
        }
        switch (editText.getId()) {
            case R.id.et_email:
                break;
            case R.id.et_password:
                if (input.length() < 8) {
                    editText.setError("Password cannot be less than 8 characters");
                    return false;
                }
                else {
                    editText.setError(null);
                }
                break;
            case R.id.et_confirm_password:
                if (input.length() < 8) {
                    editText.setError("Confirm Password cannot be less than 8 characters");
                    return false;
                }
                else {
                    editText.setError(null);
                }
                break;
            case R.id.et_username:
                break;
        }
        return true;
    }
}
