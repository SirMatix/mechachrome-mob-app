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

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends FirestoreRecyclerAdapter<User, UserAdapter.UserHolder> {


    public UserAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserAdapter.UserHolder holder, int position, @NonNull User model) {
        String full_name = model.getFname() + " " + model.getLname();
        holder.userName.setText(full_name);

        if(model.getImgUrl().equals("default")) {
            holder.userImage.setImageResource(R.drawable.ic_account);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(model.getImgUrl()) //set the user image url
                    .into(holder.userImage); //destination path
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
            userName = itemView.findViewById(R.id.user_name);
            userImage = itemView.findViewById(R.id.profile_picture);
        }
    }


}
