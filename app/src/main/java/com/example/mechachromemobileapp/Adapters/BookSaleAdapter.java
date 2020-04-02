package com.example.mechachromemobileapp.Adapters;

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
import com.example.mechachromemobileapp.Models.BookSaleModel;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class BookSaleAdapter extends FirestoreRecyclerAdapter<BookSaleModel, BookSaleAdapter.BookSaleHolder> {

    private OnItemClickListener listener;

    public BookSaleAdapter(@NonNull FirestoreRecyclerOptions<BookSaleModel> options) {
            super(options);
            }

    @Override
    protected void onBindViewHolder(@NonNull BookSaleHolder holder, int position, @NonNull BookSaleModel model) {

            Glide.with(holder.itemView.getContext())
            .load(model.getImgUrl()) //set the img book url
            .transforms(new CenterCrop() , new RoundedCorners(16))
            .into(holder.bookImage); //destination path

            holder.title.setText(model.getTitle());
            String byauthor = "By " + model.getAuthor();
            holder.author.setText(byauthor);
            String pagesrev = model.getPages() + " Pages | " + model.getNumReviews() + " reviews";
            holder.pages.setText(pagesrev);
            holder.ratingBar.setRating(model.getRating());
            }

    @NonNull
    @Override
    public BookSaleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
            return new BookSaleHolder(view);
            }

    public void deleteItem(int position) {
            getSnapshots().getSnapshot(position).getReference().delete();
            }

    class BookSaleHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView title, author, pages, score;
        RatingBar ratingBar;

        public BookSaleHolder(@NonNull View itemView) {
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
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(BookSaleAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}


