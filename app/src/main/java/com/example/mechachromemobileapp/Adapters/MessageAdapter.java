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

public class MessageAdapter extends FirestoreRecyclerAdapter<ChatMessage, MessageAdapter.MessageHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    FirebaseUser fUser;

    public MessageAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageAdapter.MessageHolder holder, int position, @NonNull ChatMessage model) {
        holder.messageText.setText(model.getMessageText());
        holder.userImage.setImageResource(R.drawable.ic_account);
    }

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

    class MessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        CircleImageView userImage;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.show_message);
            userImage = itemView.findViewById(R.id.profile_picture);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (getItem(position).getMessageSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
