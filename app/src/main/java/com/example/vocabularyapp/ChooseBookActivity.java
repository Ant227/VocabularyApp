package com.example.vocabularyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ChooseBookActivity extends AppCompatActivity {

    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private Query query;

    private Button finishBtn;

    private RelativeLayout selectedBookLayout,recyclerLayout;

    private ImageView selectedBookPic;
    private TextView selectedBookName, selectedBookWordCount, selectedBookDayCount;

    private String Book_Name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_book);

        finishBtn = findViewById(R.id.choose_book_finishBtn);
         selectedBookLayout = findViewById(R.id.choose_book_selected_book_relativeLayout);
         recyclerLayout = findViewById(R.id.recycler_layout);
         selectedBookPic = findViewById(R.id.choose_book_pic);
         selectedBookName = findViewById(R.id.choose_book_name);
         selectedBookWordCount = findViewById(R.id.choose_book_word_count);
         selectedBookDayCount = findViewById(R.id.choose_book_day_count);


         query = FirebaseDatabase.getInstance()
                .getReference()
                .child("books");


        FirebaseRecyclerOptions<Book> options =
                new FirebaseRecyclerOptions.Builder<Book>()
                        .setQuery(query, Book.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Book,BookViewHolder>(options) {

            @NonNull
            @Override
            public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(ChooseBookActivity.this).inflate(R.layout.all_book_layout,parent,false);
                return new BookViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull final Book model) {
                holder.bookName.setText(model.getBookname());
                holder.wordCount.setText(model.getWordcount()+ " words");
                holder.dayCount.setText(model.getDaycount()+" days");

                Picasso.get().load(model.bookpic).placeholder(R.drawable.app_logo)
                        .fit()
                        .centerCrop()
                        .into(holder.bookPic);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerLayout.setVisibility(View.GONE);
                        selectedBookLayout.setVisibility(View.VISIBLE);

                        Book_Name = model.getBookname();
                        selectedBookName.setText(Book_Name);
                        selectedBookWordCount.setText(model.getWordcount()+ " words");
                        selectedBookDayCount.setText(model.getDaycount()+" days");

                        Picasso.get().load(model.bookpic).placeholder(R.drawable.app_logo)
                                .fit()
                                .centerCrop()
                                .into(selectedBookPic);
                    }
                });




            }
        };

        recyclerView =  findViewById(R.id.choose_book_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChooseBookActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);


        selectedBookPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedBookLayout.setVisibility(View.GONE);
                recyclerLayout.setVisibility(View.VISIBLE);
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selectedBookName.equals("Book Name")){

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String currentUid = mAuth.getUid();
                    final DatabaseReference userBookRef = FirebaseDatabase.getInstance().getReference()
                            .child("users").child(currentUid).child("user_book");

                    userBookRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("book_status",Book_Name);

                            userBookRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        sendUserToMainActivity();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }
        });




    }

    private void sendUserToMainActivity() {
        Intent intentMain = new Intent(ChooseBookActivity.this, MainActivity.class);
        intentMain.putExtra("bookname",Book_Name);
        intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentMain);
        finish();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder{
        public ImageView bookPic;
        public TextView bookName, wordCount, dayCount;


        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            bookPic = itemView.findViewById(R.id.all_book_layout_book_pic);
            bookName = itemView.findViewById(R.id.all_book_layout_book_name);
            wordCount = itemView.findViewById(R.id.all_book_layout_word_count);
            dayCount = itemView.findViewById(R.id.all_book_layout_day_count);

        }
    }



    @Override
    protected void onStart() {
        adapter.startListening();
        super.onStart();



    }

    @Override
    protected void onStop() {
        adapter.stopListening();
        super.onStop();
    }
}
