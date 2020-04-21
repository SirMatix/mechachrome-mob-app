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
/**
 * UserAdapter Class
 *
 * handles displaying users information based on FirebaseUI
 */
public class UserAdapter extends FirestoreRecyclerAdapter<User, UserAdapter.UserHolder> {

    private OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UserAdapter(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    /**
     * method responsible for displaying data on layout, goes through entries in
     * Firestore and displays matching elements
     *
     * @param holder   ---> UserAdapter class that holds layout variables for each element in RecyclerView
     * @param position ---> position of element in RecyclerView
     * @param model    ---> User class
     */
    @Override
    protected void onBindViewHolder(@NonNull UserAdapter.UserHolder holder, int position, @NonNull User model) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        // check if the document isn't equal to currently viewing user
        if(!getSnapshots().getSnapshot(position).getId().equals(fUser.getUid())) {
            String full_name = model.getFname() + " " + model.getLname();
            holder.userName.setText(full_name);

            // checks imgUrl variable to set user image
            if(model.getImgUrl().equals("default")) {
                holder.userImage.setImageResource(R.drawable.ic_account);
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(model.getImgUrl()) //set the user image url
                        .into(holder.userImage); //destination path
            }
        } else {
            // gets currently viewing user layout elements visibility to gone
            holder.userName.setVisibility(View.GONE);  // if it is equal set visibility to GONE
            holder.userImage.setVisibility(View.GONE); // if it is equal set visibility to GONE
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
    public UserAdapter.UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user_item,
                parent, false);
        return new UserAdapter.UserHolder(v);
    }

    /**
     * This class holds variables from custom layout
     */
    class UserHolder extends RecyclerView.ViewHolder {
        // class variables
        TextView userName;
        CircleImageView userImage;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            // identifying layout elements and saving them to class variables
            userName = itemView.findViewById(R.id.user_full_name);
            userImage = itemView.findViewById(R.id.profile_picture);

            // setting onClickListener to make possible clicking on each element in RecyclerView
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