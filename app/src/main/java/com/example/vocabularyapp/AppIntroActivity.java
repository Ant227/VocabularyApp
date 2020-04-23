package com.example.vocabularyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AppIntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);
    }

    public void goToRegisterActivity(View view){
        startActivity(new Intent(AppIntroActivity.this,RegisterActivity.class));
    }

    public void goToLoginActivity(View view){
        startActivity(new Intent(AppIntroActivity.this,LoginActivity.class));
    }
}
