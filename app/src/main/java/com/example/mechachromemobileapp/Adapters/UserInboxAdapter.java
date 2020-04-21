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

/**
 * UserInboxAdapter Class
 *
 * handles displaying users information
 */
public class UserInboxAdapter extends RecyclerView.Adapter<UserInboxAdapter.UserInboxViewHolder> {

    // global variables
    private ArrayList<User> mUserList;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * This class holds variables from custom layout
     */
    public static class UserInboxViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView userImage;
        private TextView userFullName;

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

    /**
     * This method inflates the layout for each custom object in RecyclerView
     *
     * @param parent
     * @param viewType
     * @return UserInboxViewHolder with applied view
     */
    @NonNull
    @Override
    public UserInboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user_item, parent, false);
        return new UserInboxViewHolder(v, mListener);
    }

    /**
     * method responsible for displaying data on layout,
     * goes through entries in ArrayList and displays their data
     *
     * @param holder   ---> UserInboxViewHolder class that holds layout variables for each element in RecyclerView
     * @param position ---> position of element in RecyclerView
     */
    @Override
    public void onBindViewHolder(@NonNull UserInboxViewHolder holder, int position) {
        // getting current user
        User currentUser = mUserList.get(position);

        // creating user full name variable and setting it to layout element
        String currentUserFullName = currentUser.getFname() + " " + currentUser.getLname();
        holder.userFullName.setText(currentUserFullName);

        // getting user image
        String userImgUrl = currentUser.getImgUrl();

        // checking value of user image variable
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
