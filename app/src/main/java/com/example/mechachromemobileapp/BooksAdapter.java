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

    private List<Books> booksData;

    public BooksAdapter(List<Books> booksData) {
        this.booksData = booksData;
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

        Books book = booksData.get(position);

        Glide.with(holder.itemView.getContext())
                .load(book.getDrawableResources()) //set the img book url
                .transforms(new CenterCrop() , new RoundedCorners(16))
                .into(holder.imgBook); //destination path

        holder.title.setText(book.getTitle());
        holder.author.setText("By " + book.getAuthor());
        holder.pages.setText(book.getPages() + " Pages | " + book.getReview() + " reviews");
        holder.ratingBar.setRating(book.getRating());
    }

    @Override
    public int getItemCount() {
        return booksData.size();
    }




    public class booksviewholder extends RecyclerView.ViewHolder{

        ImageView imgBook, imgFav;
        TextView title, author, pages, rate;
        RatingBar ratingBar;

        public booksviewholder(@NonNull View itemView) {
            super(itemView);

            // getting the data from the layout
            imgBook = itemView.findViewById(R.id.item_book_img);
            title = itemView.findViewById(R.id.item_book_title);
            author = itemView.findViewById(R.id.item_book_author);
            pages = itemView.findViewById(R.id.item_book_pagesrev);
            rate = itemView.findViewById(R.id.item_book_score);
            ratingBar = itemView.findViewById(R.id.item_book_ratingBar);
        }
    }
}