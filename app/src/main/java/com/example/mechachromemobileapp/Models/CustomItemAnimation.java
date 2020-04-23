package com.example.mechachromemobileapp.Models;

import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mechachromemobileapp.R;

public class CustomItemAnimation extends DefaultItemAnimator {

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {

        //customizing the remove button

        holder.itemView.setAnimation(AnimationUtils.loadAnimation(
                holder.itemView.getContext(),
                R.anim.viewholder_remove_animation
        ));

        return super.animateRemove(holder);
    }
    //this method will be called when a new item will be added to the list
    // we will handle to add animation to the item here
    // first let`s create a custom animation
    //no let`s apply the animation to the viewholder
    public boolean animateAdd(RecyclerView.ViewHolder holder){
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(
                holder.itemView.getContext(),
                R.anim.viewholder_add_anim
        ));

        return super.animateAdd(holder);}

    // we can also customize the duration of added animation

    @Override
    public long getMoveDuration() {

        //the default duration is 120 ms
        //i change it to 500
        return 500;
    }


    @Override
    public long getRemoveDuration() {
        return 500;
    }
}
