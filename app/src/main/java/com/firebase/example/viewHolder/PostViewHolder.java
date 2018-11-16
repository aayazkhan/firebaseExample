package com.firebase.example.viewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.example.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by ayyazkhan on 04/11/18.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {

    public View itemView;

    public PostViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;

    }

    public void setImage(final Context context, final String imageURL) {
        final ImageView imageView = (ImageView) itemView.findViewById(R.id.imgViewtPostImage);

        Picasso.with(context).load(imageURL).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

                Picasso.with(context).load(imageURL).into(imageView);
            }
        });

    }

    public void setTitle(String title) {
        TextView textTitle = (TextView) itemView.findViewById(R.id.textPostTitle);
        textTitle.setText(title);
    }

    public void setDescription(String description) {
        TextView textDescription = (TextView) itemView.findViewById(R.id.textPostDescription);
        textDescription.setText(description);
    }

    public void setUserNameImage(final Context context, final String imageURL) {
        final ImageView imageView = (ImageView) itemView.findViewById(R.id.imgViewtUserImage);

        Picasso.with(context).load(imageURL).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

                Picasso.with(context).load(imageURL).into(imageView);
            }
        });

    }

    public void setUserName(String userName) {
        TextView textUserName = (TextView) itemView.findViewById(R.id.textName);
        textUserName.setText(userName);
    }

}
