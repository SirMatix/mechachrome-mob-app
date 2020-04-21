package com.example.mechachromemobileapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.Models.ChatMessage;
import com.example.mechachromemobileapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;
/**
 * MessageAdapter Class
 *
 * handles displaying chat messages information based on FirebaseUI
 */
public class MessageAdapter extends FirestoreRecyclerAdapter<ChatMessage, MessageAdapter.MessageHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MessageAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options) {
        super(options);
    }

    /**
     * method responsible for displaying data on layout
     *
     * @param holder   ---> MessageHolder class that holds layout variables for each element in RecyclerView
     * @param position ---> position of element in RecyclerView
     * @param model    ---> ChatMessage class
     */
    @Override
    protected void onBindViewHolder(@NonNull MessageAdapter.MessageHolder holder, int position, @NonNull ChatMessage model) {
        holder.messageText.setText(model.getMessageText());
        holder.userImage.setImageResource(R.drawable.ic_account);
    }

    /**
     *
     * this method inflates custom layout for each element in recycler view,
     * inflates different layout based on viewType number
     *
     * @param parent
     * @param viewType integer
     * @return new message holder
     */
    @NonNull
    @Override
    public MessageAdapter.MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right,
                    parent, false);
            return new MessageAdapter.MessageHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left,
                    parent, false);
            return new MessageAdapter.MessageHolder(v);
        }

    }

    /**
     * This class holds variables from custom layout
     */
    class MessageHolder extends RecyclerView.ViewHolder {
        // class variables
        TextView messageText;
        CircleImageView userImage;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            // identifying layout elements and saving them to class variables
            messageText = itemView.findViewById(R.id.show_message);
            userImage = itemView.findViewById(R.id.profile_picture);
        }
    }

    /**
     * Method that checks if sender is equal to current user
     *
     * @param position gets current element position
     * @return constant integer message type
     */
    @Override
    public int getItemViewType(int position) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (getItem(position).getMessageSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
