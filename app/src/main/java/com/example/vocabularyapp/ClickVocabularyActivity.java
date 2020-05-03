package com.example.vocabularyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClickVocabularyActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, wordRef;
    private Intent getIntent;

    private String wordRefKey, bookNameString;
    private String currentUid;

    private TextView userName, bookName, wordCount, lessThan, wordValue, greaterThan , meaningValue, bookExample;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_vocabulary);

        getIntent = getIntent();
        wordRefKey = getIntent.getStringExtra("postkey");


        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUid);
        userName = findViewById(R.id.click_vocabulary_username);
        bookName = findViewById(R.id.click_vocabulary_book_name);
        wordCount = findViewById(R.id.click_vocabulary_word_count);
        lessThan = findViewById(R.id.click_vocabulary_less_than);
        wordValue = findViewById(R.id.click_vocabulary_vocabulary);
        greaterThan = findViewById(R.id.click_vocabulary_greater_than);
        meaningValue = findViewById(R.id.click_vocabulary_meaning);
        bookExample = findViewById(R.id.click_vocabulary_book_example);

       settingUserInfoAndWord();


    }

    private void settingUpWord() {

        wordRef = FirebaseDatabase.getInstance().getReference().child("books")
                .child(bookNameString).child("words").child(wordRefKey);


        wordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String word = dataSnapshot.child("word").getValue().toString();
                    String meaning = dataSnapshot.child("meaning").getValue().toString();
                    String example = dataSnapshot.child("example").getValue().toString();

                    wordValue.setText(word);
                    meaningValue.setText(meaning);
                    bookExample.setText(example);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void settingUserInfoAndWord() {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("name").getValue().toString();
                    userName.setText(username);
                    bookNameString = dataSnapshot.child("book_status").getValue().toString();
                    bookName.setText(bookNameString);

                    settingUpWord();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
