package com.example.mechachromemobileapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

/**
 * BookSaleAdapter()
 *
 * handles displaying books for sale data in RecyclerView based on FirebasrUI
 */
public class BookSaleAdapter extends FirestoreRecyclerAdapter<BookSaleModel, BookSaleAdapter.BookSaleHolder> {

    private OnItemClickListener listener;

    public BookSaleAdapter(@NonNull FirestoreRecyclerOptions<BookSaleModel> options) {
            super(options);
    }
    /**
     * method responsible for displaying data on layout
     *
     * @param holder   ---> BookSaleHolder class that holds layout variables for each element in RecyclerView
     * @param position ---> position of element in RecyclerView
     * @param model    ---> BookSale class
     */
    @Override
    protected void onBindViewHolder(@NonNull BookSaleHolder holder, int position, @NonNull BookSaleModel model) {
        //setting book sale image
        Glide.with(holder.itemView.getContext())
        .load(model.getImgUrl()) //set the img book url
        .transform(new CenterCrop() , new RoundedCorners(16))
        .into(holder.bookImage); //destination path

        // setting book sale title
        holder.title.setText(model.getTitle());
        // setting book sale author
        String authorString = "Author: " + model.getAuthor();
        holder.author.setText(authorString);
        // setting book sale pages
        String pagesString = "Pages: " + model.getPages();
        holder.pages.setText(pagesString);
        // setting book sale price
        String priceString = "Price: £" + model.getPrice();
        holder.price.setText(priceString);
        // setting book sale condition
        String conditionString = "Condition: " + model.getCondition();
        holder.condition.setText(conditionString);

        // if book is sold making label visible
        if(!model.isSold()) {
            holder.sold.setVisibility(View.GONE);
        } else {
            holder.sold.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     * this method inflates custom layout for each element in recycler view
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public BookSaleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // here we define which layout we want to use for each of our recyclerView elements
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_book_sale, parent, false);
            return new BookSaleHolder(view);
    }

    public void deleteItem(int position) {
            getSnapshots().getSnapshot(position).getReference().delete();
    }

    /**
     * This class holds variables from custom layout
     */
    class BookSaleHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView title, author, pages, price, condition, sold;

        public BookSaleHolder(@NonNull View itemView) {
            super(itemView);

            // getting the data from the layout
            bookImage = itemView.findViewById(R.id.item_book_img);
            title = itemView.findViewById(R.id.item_book_title);
            author = itemView.findViewById(R.id.item_book_author);
            condition = itemView.findViewById(R.id.item_book_condition);
            pages = itemView.findViewById(R.id.item_book_pages);
            price = itemView.findViewById(R.id.item_book_price);
            sold = itemView.findViewById(R.id.book_sold_text);

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


