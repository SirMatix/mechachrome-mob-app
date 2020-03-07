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

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BooksViewHolder> {

    private List<Books> booksData;
    private OnItemClickListener booksListner;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        booksListner = listener;
    }

    public class BooksViewHolder extends RecyclerView.ViewHolder{

        ImageView bookImage;
        TextView title, author, pages, score;
        RatingBar ratingBar;

        public BooksViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            // getting the data from the layout
            bookImage = itemView.findViewById(R.id.item_book_img);
            title = itemView.findViewById(R.id.item_book_title);
            author = itemView.findViewById(R.id.item_book_author);
            pages = itemView.findViewById(R.id.item_book_pagesrev);
            score = itemView.findViewById(R.id.item_book_score);
            ratingBar = itemView.findViewById(R.id.item_book_ratingBar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }

                }
            });
        }
    }

    public BooksAdapter(List<Books> booksData) {
        this.booksData = booksData;
    }

    @NonNull
    @Override
    public BooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BooksViewHolder(view, booksListner);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksViewHolder holder, int position) {

        Books book = booksData.get(position);

        Glide.with(holder.itemView.getContext())
                .load(book.getImgUrl()) //set the img book url
                .transforms(new CenterCrop() , new RoundedCorners(16))
                .into(holder.bookImage); //destination path

        holder.title.setText(book.getTitle());
        holder.author.setText("By " + book.getAuthor());
        holder.pages.setText(book.getPages() + " Pages | " + book.getNumReviews() + " reviews");
        holder.ratingBar.setRating((float)book.getRating());
    }

    @Override
    public int getItemCount() {
        return booksData.size();
    }



}