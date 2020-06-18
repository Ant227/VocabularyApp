package com.example.vocabularyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.HashMap;

public class ClickVocabularyActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef, wordRef;
    private Intent getIntent;

    private String wordRefKey, bookNameString;
    private String currentUid, audioUrl;

    private TextView userName, bookName, meaningValue, bookExample, userCurrentPart;
    private Button wordValue,backBtn,nextBtn;
    private ImageView userProfile;
    private int count, wordPosition = 0;

    private TextView[] mDots;
    private LinearLayout mDotsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_vocabulary);

        getIntent = getIntent();
        wordRefKey = getIntent.getStringExtra("postkey");
        String countString = getIntent.getStringExtra("noFWords");

        count = Integer.parseInt(countString);

        wordPosition = Integer.parseInt(wordRefKey) % 6;
        if (wordPosition == 0) {
            wordPosition = 6;
        }


        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUid);
        userName = findViewById(R.id.click_vocabulary_username);
        bookName = findViewById(R.id.click_vocabulary_book_name);
        wordValue = findViewById(R.id.click_vocabulary_vocabulary);
        meaningValue = findViewById(R.id.click_vocabulary_meaning);
        bookExample = findViewById(R.id.click_vocabulary_book_example);
        userProfile = findViewById(R.id.click_vocabulary_profile);
        mDotsLayout = findViewById(R.id.dotsLayout);
        backBtn = findViewById(R.id.click_vocabulary_back_button);
        nextBtn = findViewById(R.id.click_vocabulary_next_button);
        userCurrentPart = findViewById(R.id.click_vocabulary_user_current_part);

        settingUserInfoAndWord();

        wordValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.stop();
                    mediaPlayer.setDataSource(audioUrl);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    mediaPlayer.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        addDotsIndicator(count + 1, wordPosition - 1);

        if((count+1)-wordPosition == count ){
            backBtn.setVisibility(View.INVISIBLE);
        }
        if(wordPosition == count+1){
            nextBtn.setText("Finish");
        }

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    nextBtn.setText("Next");

                    if(wordPosition == 2){
                        backBtn.setVisibility(View.INVISIBLE);
                    }
                    wordPosition = wordPosition - 1;
                    wordRefKey = String.valueOf(Integer.parseInt(wordRefKey) - 1) ;
                    settingUpWord(wordRefKey);
                    addDotsIndicator(count + 1, wordPosition - 1);
                }
            });

           nextBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                       if(wordPosition < count+1){
                           if(wordPosition == count){
                               nextBtn.setText("Finish");
                           }
                           wordPosition = wordPosition + 1;

                           if(wordPosition != 1){
                               backBtn.setVisibility(View.VISIBLE);
                           }

                           wordRefKey = String.valueOf(Integer.parseInt(wordRefKey) + 1) ;
                           settingUpWord(wordRefKey);
                           addDotsIndicator(count + 1, wordPosition - 1);
                       }

               }
           });

    }

    private void settingUpWord(String wordRefKey) {

        wordRef = FirebaseDatabase.getInstance().getReference().child("books")
                .child(bookNameString).child("words").child(wordRefKey);


        wordRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String wordString = dataSnapshot.child("word").getValue().toString();
                    String meaning = dataSnapshot.child("meaning").getValue().toString();
                    String example = dataSnapshot.child("example").getValue().toString();
                    audioUrl = dataSnapshot.child("audio").getValue(String.class);

                    wordValue.setText(wordString);
                    meaningValue.setText(meaning);
                    bookExample.setText(example);

                    savingWords(wordString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void savingWords(final String word) {
        final HashMap<String, Object> userLearnedWord = new HashMap<>();

        userLearnedWord.put("word", word);

        userRef.child("words").child(bookNameString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(wordRefKey).exists()){

                    nextBtn.setEnabled(false);
                    nextBtn.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            nextBtn.setEnabled(true);
                       }
                    }, 5000);



                    userRef.child("words").child(bookNameString).child(wordRefKey).updateChildren(userLearnedWord)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ClickVocabularyActivity.this,
                                                "mark ' "+word+" ' as learnt, so please don't quit the before you have learnt this word",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(ClickVocabularyActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(ClickVocabularyActivity.this, "already learnt", Toast.LENGTH_SHORT).show();
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


                    userRef.child("words").child(bookNameString).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int totalWords = (int) dataSnapshot.getChildrenCount();

                            int userPart = totalWords / 6 + 1;
                            userCurrentPart.setText("Current Part : "+ userPart);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addDotsIndicator(int count, int position) {

        mDots = new TextView[count];
        mDotsLayout.removeAllViews();

        for (int i = 0; i < count; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorArrow));
            mDotsLayout.addView(mDots[i]);
        }

        mDots[position].setTextColor(getResources().getColor(R.color.colorBlack));

    }


}
