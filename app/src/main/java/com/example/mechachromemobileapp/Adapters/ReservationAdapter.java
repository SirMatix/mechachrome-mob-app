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

    @Override
    protected void onBindViewHolder(@NonNull ReservationHolder holder, int position, @NonNull Reservation model) {
        holder.bookAuthor.setText(model.getBook_author());
        holder.bookTitle.setText(model.getBook_title());
        String date_from = DateFormat.format("dd-MM-yyyy HH:mm:ss", model.getReserved_from()).toString();
        holder.reservedFrom.setText(date_from);
        String date_to = DateFormat.format("dd-MM-yyyy HH:mm:ss", model.getReserved_to()).toString();
        holder.reservedTo.setText(date_to);

        if(!model.getIs_active() && model.getIs_done() || !model.getIs_done() && model.getIs_cancelled()) {
            holder.reservationEnded.setVisibility(View.VISIBLE);
        }

    }

    @NonNull
    @Override
    public ReservationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_reservation_item,
                parent, false);
        return new ReservationHolder(view);
    }

    class ReservationHolder extends RecyclerView.ViewHolder {
        TextView bookTitle, bookAuthor, reservedFrom, reservedTo, reservationEnded;

        public ReservationHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.reservation_book_title);
            bookAuthor = itemView.findViewById(R.id.reservation_book_author);
            reservedFrom = itemView.findViewById(R.id.reserved_from_date);
            reservedTo = itemView.findViewById(R.id.reserved_to_date);
            reservationEnded =itemView.findViewById(R.id.reservation_ended);

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
