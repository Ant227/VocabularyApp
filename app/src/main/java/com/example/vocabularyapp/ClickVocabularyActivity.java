package com.example.vocabularyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
    private ImageView userProfile;
    private int count,wordPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_vocabulary);

        getIntent = getIntent();
        wordRefKey = getIntent.getStringExtra("postkey");

        wordPosition = Integer.parseInt(wordRefKey);

        count = getCount();

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
        userProfile = findViewById(R.id.click_vocabulary_profile);

       settingUserInfoAndWord();

       greaterThan.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(count != 0){
                   count--;
                   wordPosition++;
                   settingUpWord(String.valueOf(wordPosition));

               }
               else {
                   count = 4;
                   wordPosition = wordPosition - 4;
                   settingUpWord(String.valueOf(wordPosition));
               }

           }
       });

       lessThan.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(count != 4){
                   count++;
                   wordPosition--;
                   settingUpWord(String.valueOf(wordPosition));
               }
               else {
                   count = 0;
                   wordPosition = wordPosition + 4;
                   settingUpWord(String.valueOf(wordPosition));
               }
           }
       });
    }

    private int getCount() {
        int position = wordPosition;
        int i = 0;
        while (position%5 != 0){
            i++;
            position++;
        }
        return i;
    }

    private void settingUpWord(String wordRefKey) {

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

                    int imageId = Integer.valueOf(dataSnapshot.child("profile").getValue().toString());
                    userProfile.setImageResource(imageId);
                    settingUpWord(wordRefKey);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
