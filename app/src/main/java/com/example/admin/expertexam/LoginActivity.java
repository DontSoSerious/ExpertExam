package com.example.admin.expertexam;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity{
    public static final int INTENT_REGISTER = 1;
    private EditText mEtEmail, mEtPassword;
    private TextView mTvSignUp;
    private Button mBtnLogin;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEtEmail = (EditText) findViewById(R.id.et_email);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mTvSignUp = (TextView) findViewById(R.id.tv_sign_up_hyperlink);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mTvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(i, INTENT_REGISTER);
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEtEmail.getText().toString().trim();
                String password = mEtPassword.getText().toString();
                mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser account = mFirebaseAuth.getCurrentUser();
                            if (account.isEmailVerified()) {
                                Intent i = new Intent(getApplicationContext(), Dashboard.class);
                                startActivity(i);
                            }
                            else {
                                account.sendEmailVerification();
                                Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                mFirebaseAuth.signOut();
                            }
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Your email or password are invalid", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INTENT_REGISTER:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "You have successfully register the account. Please verify your account", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private boolean checkInput(EditText editText) {
        String input = editText.getText().toString();
        boolean check = true;
        if (input.isEmpty()) {
            editText.setError("Fields cannot be empty");
            check = false;
        }
        else {
            editText.setError(null);
        }
        switch (editText.getId()) {
            case R.id.et_email:
                break;
            case R.id.et_password:
                break;
        }
        return check;
    }
}
