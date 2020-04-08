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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInboxAdapter extends RecyclerView.Adapter<UserInboxAdapter.UserInboxViewHolder> {

    private ArrayList<User> mUserList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;

    }

    public static class UserInboxViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView userImage;
        public TextView userFullName;

        public UserInboxViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            userImage = itemView.findViewById(R.id.profile_picture);
            userFullName = itemView.findViewById(R.id.user_full_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public UserInboxAdapter(ArrayList<User> userList) {
        mUserList = userList;
    }

    @NonNull
    @Override
    public UserInboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user_item, parent, false);
        return new UserInboxViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserInboxViewHolder holder, int position) {
        User currentUser = mUserList.get(position);

        String currentUserFullName = currentUser.getFname() + " " + currentUser.getLname();
        holder.userFullName.setText(currentUserFullName);

        String userImgUrl = currentUser.getImgUrl();

        if(userImgUrl.equals("default")){
            holder.userImage.setImageResource(R.drawable.ic_account);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(userImgUrl) //set the user image url
                    .into(holder.userImage); //destination path
        }
    }



    @Override
    public int getItemCount() {
        return mUserList.size();
    }
}
