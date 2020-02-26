package com.example.mechachromemobileapp;

import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class CustomItemAnimation extends DefaultItemAnimator {

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        //this metod will be called when a new item will be added to the list
        // we will handle to add animation to the item here
        // first le`s create a custom animation
        //no let`s apply the animation to the viewholder
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(
                holder.itemView.getContext(),
                R.anim.viewholder_add_anim
        ));
        return super.animateRemove(holder);
    }
}
