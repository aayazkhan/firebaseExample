package com.firebase.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SinglePostActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.imgViewtUserImage)
    ImageView imgViewtUserImage;

    @BindView(R.id.textName)
    TextView textName;

    @BindView(R.id.imgViewtPostImage)
    ImageView imgViewtPostImage;

    @BindView(R.id.textPostTitle)
    TextView textPostTitle;

    @BindView(R.id.textPostDescription)
    TextView textPostDescription;

    @BindView(R.id.imgViewLike)
    ImageView imageViewLike;

    @BindView(R.id.imgViewComment)
    ImageView imageViewComment;

    @BindView(R.id.textViewLike)
    TextView textViewLike;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReferenceUsers;
    private DatabaseReference postUserDatabaseReference;

    private DatabaseReference databaseReferencePosts;
    private DatabaseReference currentPostDatabaseReference;

    private String post_id;
    private boolean like;
    private long likeCount = 0;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_USERS);
        databaseReferenceUsers.keepSynced(true);

        databaseReferencePosts = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_POSTS);
        databaseReferencePosts.keepSynced(true);

        savedInstanceState = getIntent().getExtras();

        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey("post_id")) {

                post_id = savedInstanceState.getString("post_id");
                currentPostDatabaseReference = databaseReferencePosts.child(post_id);

                currentPostDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String postTitle = dataSnapshot.child("title").getValue().toString();
                        String postDesc = dataSnapshot.child("description").getValue().toString();
                        final String postImage_url = dataSnapshot.child("image_url").getValue().toString();
                        String postDateTime = dataSnapshot.child("datetime").getValue().toString();
                        String postUid = dataSnapshot.child("UID").getValue().toString();

                        textPostTitle.setText(postTitle);
                        textPostDescription.setText(postDesc);

                        DataSnapshot snapshotPostLike = dataSnapshot.child("/" + MyApplication.tbl_POST_LIKE);

                        like = snapshotPostLike.hasChild(user.getUid());

                        if (like) {
                            imageViewLike.setImageDrawable(getResources().getDrawable(R.mipmap.color_heart));
                        } else {
                            imageViewLike.setImageDrawable(getResources().getDrawable(R.mipmap.simple_heart));
                        }

                        likeCount = snapshotPostLike.getChildrenCount();

                        textViewLike.setText(likeCount + " likes");

                        Picasso.with(SinglePostActivity.this).load(postImage_url).networkPolicy(NetworkPolicy.OFFLINE).into(imgViewtPostImage, new Callback() {

                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Picasso.with(SinglePostActivity.this).load(postImage_url).into(imgViewtPostImage);
                            }

                        });

                        postUserDatabaseReference = databaseReferenceUsers.child(postUid);

                        postUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String postUserName = dataSnapshot.child("UserName").getValue().toString();
                                final String postUserProfilePic = dataSnapshot.child("ProfilePic").getValue().toString();

                                textName.setText(postUserName);

                                Picasso.with(SinglePostActivity.this).load(postUserProfilePic).networkPolicy(NetworkPolicy.OFFLINE).into(imgViewtUserImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

                                        Picasso.with(SinglePostActivity.this).load(postUserProfilePic).into(imgViewtUserImage);
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        if (user.getUid().equalsIgnoreCase(postUid)) {
                            btnSubmit.setVisibility(View.VISIBLE);
                        } else {
                            btnSubmit.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    @OnClick(R.id.imgViewLike)
    public void onImgViewLike() {
        DatabaseReference databaseReferencePostLike = databaseReferencePosts.child(post_id + "/" + MyApplication.tbl_POST_LIKE);
        if (like) {
            imageViewLike.setImageDrawable(getResources().getDrawable(R.mipmap.simple_heart));
            like = false;
            databaseReferencePostLike.child(user.getUid()).removeValue();
            likeCount = likeCount - 1;
        } else {
            imageViewLike.setImageDrawable(getResources().getDrawable(R.mipmap.color_heart));
            like = true;
            databaseReferencePostLike.child(user.getUid()).setValue(simpleDateFormat.format(new Date()));
            likeCount = likeCount + 1;
        }

        textViewLike.setText(likeCount + " likes");
    }

    @OnClick(R.id.imgViewComment)
    public void onImgViewCOmment() {
        Intent intent = new Intent(SinglePostActivity.this, CommentActivity.class);
        intent.putExtra("post_id", post_id);
        startActivity(intent);
    }

    @OnClick(R.id.btnSubmit)
    public void onBtnSubmit() {
        currentPostDatabaseReference.removeValue();
        Toast.makeText(SinglePostActivity.this, "Deleted.", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
