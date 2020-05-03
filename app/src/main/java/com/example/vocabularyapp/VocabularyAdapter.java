package com.example.vocabularyapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class VocabularyAdapter extends FirebaseRecyclerAdapter<Vocabulary, VocabularyAdapter.VocabularyHolder> {
    public Context context;

    public VocabularyAdapter(@NonNull FirebaseRecyclerOptions<Vocabulary> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull VocabularyHolder holder, int position, @NonNull Vocabulary model) {

        holder.vocabularyTextView.setText(model.getWord());

        final String postKey = getRef(position).getKey();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToClickVocabularyActivity(postKey);
            }
        });
    }

    private void SendUserToClickVocabularyActivity(String postKey) {
        Intent intent = new Intent(context, ClickVocabularyActivity.class);
        intent.putExtra("postkey", postKey);
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
