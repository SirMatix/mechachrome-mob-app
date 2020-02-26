package com.example.mechachromemobileapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;


import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.booksviewholder> {

    List<Books> mdata;

    public BooksAdapter(List<Books> mdata) {
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public booksviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);

        return new booksviewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull booksviewholder holder, int position) {


        Glide.with(holder.itemView.getContext())
                .load(mdata.get(position).getDrawableResources()) //set the img book url
                .transforms(new CenterCrop() , new RoundedCorners(16))
                .into(holder.imgBook); //destination path

        holder.ratingBar.setRating((float) 4.5);
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }




    public class booksviewholder extends RecyclerView.ViewHolder{

        ImageView imgBook, imgFav;
        TextView title, author, pages, rate;
        RatingBar ratingBar;

        public booksviewholder(@NonNull View itemView) {
            super(itemView);

            imgBook = itemView.findViewById(R.id.item_book_img);
            title = itemView.findViewById(R.id.item_book_title);
            author = itemView.findViewById(R.id.item_book_author);
            pages = itemView.findViewById(R.id.item_book_pagesrev);
            rate = itemView.findViewById(R.id.item_book_score);
            ratingBar = itemView.findViewById(R.id.item_book_ratingBar);



        }
    }
}