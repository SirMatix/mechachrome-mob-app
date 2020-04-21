package com.example.mechachromemobileapp.Adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Models.Reservation;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * ReservationAdapter Class
 *
 * handles displaying reservation information based on FirebaseUI
 */
public class ReservationAdapter extends FirestoreRecyclerAdapter<Reservation, ReservationAdapter.ReservationHolder> {

    private OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ReservationAdapter(@NonNull FirestoreRecyclerOptions<Reservation> options) {
        super(options);
    }

    /**
     * method responsible for displaying data on layout
     *
     * @param holder   ---> ReservationHolder class that holds layout variables for each element in RecyclerView
     * @param position ---> position of element in RecyclerView
     * @param model    ---> Reservation class
     */
    @Override
    protected void onBindViewHolder(@NonNull ReservationHolder holder, int position, @NonNull Reservation model) {
        // setting reservation data
        holder.bookAuthor.setText(model.getBook_author());
        holder.bookTitle.setText(model.getBook_title());
        String date_from = DateFormat.format("dd-MM-yyyy HH:mm:ss", model.getReserved_from()).toString();
        holder.reservedFrom.setText(date_from);
        String date_to = DateFormat.format("dd-MM-yyyy HH:mm:ss", model.getReserved_to()).toString();
        holder.reservedTo.setText(date_to);

        // displaying reservation ended text
        if(!model.getIs_active() && model.getIs_done() || !model.getIs_done() && model.getIs_cancelled()) {
            holder.reservationEnded.setVisibility(View.VISIBLE);
        }

    }

    /**
     * this method inflates custom layout for each element in recycler view
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ReservationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_reservation_item,
                parent, false);
        return new ReservationHolder(view);
    }

    /**
     * This class holds variables from custom layout
     */
    class ReservationHolder extends RecyclerView.ViewHolder {
        // class variables
        TextView bookTitle, bookAuthor, reservedFrom, reservedTo, reservationEnded;

        public ReservationHolder(@NonNull View itemView) {
            super(itemView);
            // identifying layout elements and saving them to class variables
            bookTitle = itemView.findViewById(R.id.reservation_book_title);
            bookAuthor = itemView.findViewById(R.id.reservation_book_author);
            reservedFrom = itemView.findViewById(R.id.reserved_from_date);
            reservedTo = itemView.findViewById(R.id.reserved_to_date);
            reservationEnded =itemView.findViewById(R.id.reservation_ended);

            // setting onClickListener to make possible clicking on each element in RecyclerView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}
