package com.example.admin.expertexam;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class StartUpActivity extends AppCompatActivity {

    Animation topAnim, midAnim, bottomAnim;
    TextView tvTop, tvBottom;
    ImageView ivMid;
    FirebaseDatabase db;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_startup);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        midAnim = AnimationUtils.loadAnimation(this, R.anim.middle_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        tvTop = (TextView) findViewById(R.id.textView);
        ivMid = (ImageView) findViewById(R.id.imageView4);
        tvBottom = (TextView) findViewById(R.id.textView2);

        tvTop.setAnimation(topAnim);
        ivMid.setAnimation(midAnim);
        tvBottom.setAnimation(bottomAnim);

        fAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = fAuth.getCurrentUser();
                Intent i = null;
                if (currentUser == null) {
                    i = new Intent(StartUpActivity.this, LoginActivity.class);
                }
                else {
                     i = new Intent(StartUpActivity.this, Dashboard.class);
                }
                startActivity(i);
                finish();

            }
        }, 4000);
    }
}
