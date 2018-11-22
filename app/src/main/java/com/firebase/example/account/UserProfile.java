package com.firebase.example.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.example.MyApplication;
import com.firebase.example.PostActivity;
import com.firebase.example.R;
import com.firebase.example.SinglePostActivity;
import com.firebase.example.model.Post;
import com.firebase.example.viewHolder.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class UserProfile extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.textUserName)
    TextView textUserName;

    @BindView(R.id.imgUserProfilePic)
    ImageView imgProfilePic;

    @BindView(R.id.textPostCount)
    TextView textPostCount;

    @BindView(R.id.textFollowerFollowing)
    TextView textFollowerFollowing;

    @BindView(R.id.userPostList)
    RecyclerView userPostList;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReferenceUsers;
    private DatabaseReference currentUserDatabaseReference;
    private DatabaseReference databaseReferencePosts;
    private DatabaseReference databaseReferenceFollowings;
    private DatabaseReference userDatabaseReferenceFollowings;
    private DatabaseReference userDatabaseReference;

    private FirebaseRecyclerAdapter recyclerAdapter;
    private GridLayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> adapter;

    private ProgressDialog progressDialog;

    private String user_id;

    private int postCount, devicewidth;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_USERS);
        databaseReferenceUsers.keepSynced(true);

        databaseReferencePosts = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_POSTS);
        databaseReferencePosts.keepSynced(true);

        databaseReferenceFollowings = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_FOLLOWING);
        databaseReferenceFollowings.keepSynced(true);

        currentUserDatabaseReference = databaseReferenceUsers.child(user.getUid());

        layoutManager = new GridLayoutManager(UserProfile.this, 3);

        userPostList.setLayoutManager(layoutManager);

        userPostList.addItemDecoration(new DividerItemDecoration(UserProfile.this, DividerItemDecoration.VERTICAL));
        userPostList.addItemDecoration(new DividerItemDecoration(UserProfile.this, DividerItemDecoration.HORIZONTAL));

        DisplayMetrics displaymetrics = new DisplayMetrics();
        UserProfile.this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        //if you need three fix imageview in width
        devicewidth = (displaymetrics.widthPixels / 3) - 20;

        savedInstanceState = getIntent().getExtras();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("user_id")) {

                user_id = savedInstanceState.getString("user_id");

                userDatabaseReference = databaseReferenceUsers.child(user_id);

                userDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {
                            textUserName.setText(dataSnapshot.child("UserName").getValue().toString());

                            final String userProfilePic = dataSnapshot.child("ProfilePic").getValue().toString();

                            Picasso.with(UserProfile.this).load(userProfilePic).networkPolicy(NetworkPolicy.OFFLINE).into(imgProfilePic, new Callback() {

                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError() {
                                    Picasso.with(UserProfile.this).load(userProfilePic).into(imgProfilePic);
                                }

                            });

                            userDatabaseReferenceFollowings = databaseReferenceFollowings.child(user.getUid() + "|" + user_id);

                            userDatabaseReferenceFollowings.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        textFollowerFollowing.setText("Un follow");
                                    } else {
                                        textFollowerFollowing.setText("follow");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            Query queryUserPost = databaseReferencePosts.orderByChild("UID").equalTo(user_id);

                            queryUserPost.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    postCount = (int) dataSnapshot.getChildrenCount();
                                    textPostCount.setText("" + postCount);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            adapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                                    Post.class, R.layout.post_row, PostViewHolder.class, queryUserPost) {

                                @Override
                                protected void populateViewHolder(PostViewHolder viewHolder, Post post, final int position) {


                                    viewHolder.setTitleVisibility(View.GONE);
                                    viewHolder.setDescriptionVisibility(View.GONE);
                                    viewHolder.setUserNameVisibility(View.GONE);
                                    viewHolder.setUserNameImageVisibility(View.GONE);

                                    viewHolder.setImage(UserProfile.this, post.getImage_url());

                                    viewHolder.getItemView().getLayoutParams().width = devicewidth;
                                    viewHolder.getItemView().getLayoutParams().height = devicewidth;

                                    viewHolder.getItemView().setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            String POST_KEY = getRef(position).getKey();

                                            Intent intent = new Intent(UserProfile.this, SinglePostActivity.class);
                                            intent.putExtra("post_id", POST_KEY);
                                            startActivity(intent);

                                        }
                                    });

                                }
                            };

                            userPostList.setAdapter(adapter);


                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }

    }


    @OnClick(R.id.textFollowerFollowing)
    public void onTextFollowerFollowing() {

        if (textFollowerFollowing.getText().toString().equalsIgnoreCase("follow")) {

            userDatabaseReferenceFollowings.child("FollowUID").setValue(user_id);
            userDatabaseReferenceFollowings.child("UID").setValue(user.getUid());
            userDatabaseReferenceFollowings.child("datetime").setValue(simpleDateFormat.format(new Date()));

        } else {
            userDatabaseReferenceFollowings.removeValue();
        }

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
