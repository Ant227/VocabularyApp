package com.example.vocabularyapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.VpnService;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VocabularyAdapter extends FirebaseRecyclerAdapter<Vocabulary, VocabularyAdapter.VocabularyHolder> {
    private Context context;
    private String bookName;
    private int noFWords = 0;




    public VocabularyAdapter(@NonNull FirebaseRecyclerOptions<Vocabulary> options, Context context,String bookName) {
        super(options);
        this.context = context;
        this.bookName = bookName;
    }

    @Override
    protected void onBindViewHolder(@NonNull VocabularyHolder holder, int position, @NonNull Vocabulary model) {

        holder.vocabularyTextView.setText(model.getWord());

        final String postKey = getRef(position).getKey();


        noFWords = position;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToClickVocabularyActivity(postKey, noFWords);
            }
        });



    }



    private void checkingLearnedWords(final String postKey) {


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
       String currentUid = mAuth.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUid);

        userRef.child("words").child(bookName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postKey).exists()){
                    String userWord = dataSnapshot.child(postKey).child("word").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToClickVocabularyActivity(String postKey,int words) {
        Intent intent = new Intent(context, ClickVocabularyActivity.class);
        intent.putExtra("postkey", postKey);
        intent.putExtra("noFWords",String.valueOf(words));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);

    }

    @NonNull
    @Override
    public VocabularyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.word_layout, parent, false);
        return new VocabularyHolder(view);
    }

    public class VocabularyHolder extends RecyclerView.ViewHolder {

        public TextView vocabularyTextView;

        public VocabularyHolder(@NonNull View itemView) {
            super(itemView);

            vocabularyTextView = itemView.findViewById(R.id.word_layout_textView);
        }
    }
}
