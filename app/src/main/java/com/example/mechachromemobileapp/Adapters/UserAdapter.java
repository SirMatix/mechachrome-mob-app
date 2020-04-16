package com.example.mechachromemobileapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mechachromemobileapp.Models.User;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends FirestoreRecyclerAdapter<User, UserAdapter.UserHolder> {

    private OnItemClickListener listener;

    public UserAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserAdapter.UserHolder holder, int position, @NonNull User model) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        // check if the document isn't equal to currently viewing user
        if(!getSnapshots().getSnapshot(position).getId().equals(fUser.getUid())) {
            String full_name = model.getFname() + " " + model.getLname();
            holder.userName.setText(full_name);

            if(model.getImgUrl().equals("default")) {
                holder.userImage.setImageResource(R.drawable.ic_account);
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(model.getImgUrl()) //set the user image url
                        .into(holder.userImage); //destination path
            }
        } else {
            holder.userName.setVisibility(View.GONE);  // if it is equal set visibility to GONE
            holder.userImage.setVisibility(View.GONE); // if it is equal set visibility to GONE
        }
    }

    @NonNull
    @Override
    public UserAdapter.UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user_item,
                parent, false);
        return new UserAdapter.UserHolder(v);
    }

    class UserHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircleImageView userImage;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_full_name);
            userImage = itemView.findViewById(R.id.profile_picture);

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

    public void setOnItemClickListener(UserAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


}
