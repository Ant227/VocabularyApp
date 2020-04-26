package com.example.vocabularyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef ;


    private TextView userName, bookName, wordCount;
    private TextView date,lessThan,dayCount,greaterThan;

    private Calendar calendar;
    private String currentDate;
    private int day = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.main_username);
        bookName = findViewById(R.id.main_book_name);
        wordCount = findViewById(R.id.main_word_count);
        date = findViewById(R.id.main_date);
        lessThan = findViewById(R.id.main_less_than);
        dayCount = findViewById(R.id.main_day);
        greaterThan = findViewById(R.id.main_greater_than);

        settingUserName();
        settingBookName();

        calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        date.setText(currentDate);
        dayCount.setText("Day " + day);

        lessThan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(day > 1){
                    day--;
                    calendar.add(Calendar.DATE,-1);
                    String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
                    date.setText(currentDate);
                    dayCount.setText("Day " + day);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "This is the first page", Toast.LENGTH_SHORT).show();
                }

            }
        });

        greaterThan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day++;
                calendar.add(Calendar.DATE,1);
                String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
                date.setText(currentDate);
                dayCount.setText("Day " + day);
            }
        });

    }

    private void settingBookName()
    {
        userRef.child(mAuth.getUid()).child("user_book").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String book_status = dataSnapshot.child("book_status").getValue().toString();
                    bookName.setText(book_status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void settingUserName() {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(!(mAuth.getCurrentUser() == null)){
                        String currentUid = mAuth.getUid();
                        String username = dataSnapshot.child(currentUid)
                                .child("name").getValue().toString();
                        userName.setText(username);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {


        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(MainActivity.this, AppIntroActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }
        else
        {
            checkSelectedBook();
        }
    }

    private void checkSelectedBook()
    {
        userRef.child(mAuth.getUid()).child("user_book").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String book_status = dataSnapshot.child("book_status").getValue().toString();
                    if(book_status.equals("none")){
                        sendUserToChooseBookActivity();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToChooseBookActivity() {
        Intent intent = new Intent(MainActivity.this, ChooseBookActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void logout(View view)
    {
        mAuth.signOut();
        SendUserToAppIntroActivity();

    }

    private void SendUserToAppIntroActivity() {
        Intent intent = new Intent(MainActivity.this, AppIntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

}
