package com.example.mechachromemobileapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Models.Day;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class DayAdapter extends FirestoreRecyclerAdapter<Day, DayAdapter.DayHolder> {
    private OnItemClickListener listener;

    public DayAdapter(@NonNull FirestoreRecyclerOptions<Day> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull DayHolder holder, int position, @NonNull Day model) {
        holder.name.setText(model.getName());
        holder.timeframe1.setText(model.getTimeframe1());
        holder.timeframe2.setText(model.getTimeframe2());
        holder.timeframe3.setText(model.getTimeframe3());
        holder.timeframe4.setText(model.getTimeframe4());
        holder.timeframe5.setText(model.getTimeframe5());
        holder.timeframe6.setText(model.getTimeframe6());
    }

    @NonNull
    @Override
    public DayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_day,
                parent, false);
        return new DayHolder(v);
    }

    class DayHolder extends RecyclerView.ViewHolder {
        TextView name, timeframe1, timeframe2, timeframe3, timeframe4, timeframe5, timeframe6;

        public DayHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            timeframe1 = itemView.findViewById(R.id.timeframe1);
            timeframe2 = itemView.findViewById(R.id.timeframe2);
            timeframe3 = itemView.findViewById(R.id.timeframe3);
            timeframe4 = itemView.findViewById(R.id.timeframe4);
            timeframe5 = itemView.findViewById(R.id.timeframe5);
            timeframe6 = itemView.findViewById(R.id.timeframe6);

            timeframe1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position, "timeframe1");

                    }
                }
            });
            timeframe2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position, "timeframe2");

                    }
                }
            });
            timeframe3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position, "timeframe3");

                    }
                }
            });
            timeframe4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position, "timeframe4");

                    }
                }
            });
            timeframe5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position, "timeframe5");

                    }
                }
            });
            timeframe6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position, "timeframe6");

                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position, String timeframe);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }



}
