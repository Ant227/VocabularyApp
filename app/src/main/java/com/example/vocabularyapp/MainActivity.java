package com.example.vocabularyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private FirebaseRecyclerAdapter adapter;

    private ImageView userProfile;
    private TextView userName, bookName, wordCount;
    private TextView date, lessThan, dayCount, greaterThan;

    private RecyclerView recyclerView;

    private Calendar calendar;
    private String currentDate, book_status;
    private RecyclerView.LayoutManager layoutManager;
    private int part = 1;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
        userProfile = findViewById(R.id.main_user_profile);
        userName = findViewById(R.id.main_username);
        bookName = findViewById(R.id.main_book_name);
        wordCount = findViewById(R.id.main_word_count);
        date = findViewById(R.id.main_date);
        lessThan = findViewById(R.id.main_less_than);
        dayCount = findViewById(R.id.main_day);
        greaterThan = findViewById(R.id.main_greater_than);
        recyclerView = findViewById(R.id.main_recyclerview);
        linearLayout = findViewById(R.id.main_date_layout);
        linearLayout.setVisibility(View.INVISIBLE);


        settingUserInfo();

        calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        date.setText(currentDate);
        dayCount.setText("Part " + part);



        lessThan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (part > 1) {
                    part--;
                    dayCount.setText("Part " + part);
                } else {
                    Toast.makeText(MainActivity.this, "This is the first page", Toast.LENGTH_SHORT).show();
                }

                updateQuery((part * 6) - 5);
            }
        });

        greaterThan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                part++;
                dayCount.setText("Part " + part);
                updateQuery((part * 6) - 5);
                if (part > 4) {
                    Toast.makeText(MainActivity.this, "No more data!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void updateQuery(int start) {

        if(!TextUtils.isEmpty(book_status)){
            Query query = FirebaseDatabase.getInstance().getReference()
                    .child("books").child(book_status).child("words")
                    .orderByKey()
                    .startAt(String.valueOf(start)).limitToFirst(6);

            FirebaseRecyclerOptions<Vocabulary> options =
                    new FirebaseRecyclerOptions.Builder<Vocabulary>()
                            .setQuery(query, Vocabulary.class)
                            .build();

            adapter.updateOptions(options);
        }

    }

    private void displayingVocabulary(int start) {

        if(book_status != null){

            Query query = FirebaseDatabase.getInstance().getReference()
                    .child("books").child(book_status).child("words")
                    .orderByKey()
                    .startAt(String.valueOf(start)).limitToFirst(6);

            FirebaseRecyclerOptions<Vocabulary> options =
                    new FirebaseRecyclerOptions.Builder<Vocabulary>()
                            .setQuery(query, Vocabulary.class)
                            .build();

            adapter = new VocabularyAdapter(options, getApplicationContext());

            recyclerView.setHasFixedSize(true);
            layoutManager = new GridLayoutManager(MainActivity.this,2);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            adapter.startListening();

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        linearLayout.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    }


    private void settingUserInfo() {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (!(mAuth.getCurrentUser() == null)) {
                        String currentUid = mAuth.getUid();
                        String username = dataSnapshot.child(currentUid)
                                .child("name").getValue().toString();
                        userName.setText(username);

                        book_status = dataSnapshot.child(currentUid)
                                .child("book_status").getValue().toString();
                        bookName.setText(book_status);

                        int imageId = Integer.valueOf(dataSnapshot.child(currentUid).child("profile").getValue().toString());
                        userProfile.setImageResource(imageId);

                       displayingVocabulary(part);
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
        } else {
            checkSelectedBook();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void checkSelectedBook() {
        userRef.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String book_status = dataSnapshot.child("book_status").getValue().toString();
                    if (book_status.equals("none")) {
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

    public void logout(View view) {
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
