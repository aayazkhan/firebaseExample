package com.firebase.example.viewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.example.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by ayyazkhan on 04/11/18.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {

    private View itemView;
    private ImageView imageViewPostImage, imgViewtUserImage;
    private TextView textUserName, textTitle, textDescription, textViewLike;

    private LinearLayout linearLayoutUser, linearLayoutPost;

    private ImageView imageViewLike, imageViewComment;

    public PostViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        imgViewtUserImage = (ImageView) itemView.findViewById(R.id.imgViewtUserImage);
        textUserName = (TextView) itemView.findViewById(R.id.textName);

        imageViewPostImage = (ImageView) itemView.findViewById(R.id.imgViewtPostImage);
        textTitle = (TextView) itemView.findViewById(R.id.textPostTitle);
        textDescription = (TextView) itemView.findViewById(R.id.textPostDescription);

        imageViewLike = (ImageView) itemView.findViewById(R.id.imgViewLike);
        imageViewComment = (ImageView) itemView.findViewById(R.id.imgViewComment);

        textViewLike = (TextView) itemView.findViewById(R.id.textViewLike);

        linearLayoutUser = (LinearLayout) itemView.findViewById(R.id.linearLayoutUser);
        linearLayoutPost = (LinearLayout) itemView.findViewById(R.id.linearLayoutPost);
    }

    public View getItemView() {
        return itemView;
    }

    public void setImage(final Context context, final String imageURL) {

        Picasso.with(context).load(imageURL).networkPolicy(NetworkPolicy.OFFLINE).into(imageViewPostImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

                Picasso.with(context).load(imageURL).into(imageViewPostImage);
            }
        });

    }

    public void setTitle(String title) {
        textTitle.setText(title);
    }

    public void setDescription(String description) {
        textDescription.setText(description);
    }

    public void setUserNameImage(final Context context, final String imageURL) {

        Picasso.with(context).load(imageURL).networkPolicy(NetworkPolicy.OFFLINE).into(imgViewtUserImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

                Picasso.with(context).load(imageURL).into(imgViewtUserImage);
            }
        });

    }

    public void setUserName(String userName) {
        textUserName.setText(userName);
    }

    public void setTitleVisibility(int visibility) {
        textTitle.setVisibility(visibility);
    }

    public void setDescriptionVisibility(int visibility) {
        textDescription.setVisibility(visibility);
    }

    public void setUserNameVisibility(int visibility) {
        textUserName.setVisibility(visibility);
    }

    public void setUserNameImageVisibility(int visibility) {
        imgViewtUserImage.setVisibility(visibility);
    }

    public ImageView getImageViewLike() {
        return imageViewLike;
    }

    public ImageView getImageViewComment() {
        return imageViewComment;
    }

    public void setImageViewLike(Context context, boolean like) {
        if (like) {
            imageViewLike.setImageDrawable(context.getResources().getDrawable(R.mipmap.color_heart));
        } else {
            imageViewLike.setImageDrawable(context.getResources().getDrawable(R.mipmap.simple_heart));
        }
    }

    public void setLikeCount(long count) {
        textViewLike.setText(count + " likes");
    }


    public LinearLayout getLinearLayoutUser() {
        return linearLayoutUser;
    }

    public LinearLayout getLinearLayoutPost() {
        return linearLayoutPost;
    }
}
