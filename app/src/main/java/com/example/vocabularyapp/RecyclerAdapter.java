package com.example.vocabularyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder> {

    private int[] images;
    private CircularImageView circularImageView;
    private int selectedLogoLocation;

    public RecyclerAdapter(int[] images,CircularImageView circularImageView){

        this.images = images;
        this.circularImageView = circularImageView;
        this.selectedLogoLocation = images[0];
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.logo_layout,parent,false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(view);

        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        final int image_id = images[position];

        holder.circularImageView.setImageResource(image_id);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circularImageView.setImageResource(image_id);
                setImageID(image_id);
            }
        });
    }

    private void setImageID(int image_id) {
        this.selectedLogoLocation = image_id;
    }

    public int returnSelectedLogo(){
        return selectedLogoLocation;
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{

        CircularImageView circularImageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            circularImageView = itemView.findViewById(R.id.circularImageView);
        }
    }
}
