package com.example.vocabularyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;


public class BookAdapter extends FirebaseRecyclerAdapter<Book, BookAdapter.BookViewHolder> {

    public Context mcontext;


    public BookAdapter(@NonNull FirebaseRecyclerOptions<Book> options,Context context) {
        super(options);
        mcontext = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull Book model) {
            holder.bookName.setText(model.getBookname());

        Toast.makeText(mcontext, model.getBookname(), Toast.LENGTH_SHORT).show();

//        Picasso.get().load(model.getBook_pic())
//                .placeholder(R.drawable.app_logo)
//                .fit()
//                .centerCrop()
//                .into(holder.bookPic);

    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.all_book_layout,parent,false);
        return new BookViewHolder(view);
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
}
