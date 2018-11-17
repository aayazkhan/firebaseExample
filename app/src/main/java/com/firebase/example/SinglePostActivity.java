package com.firebase.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

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

import butterknife.BindView;
import butterknife.ButterKnife;
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

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReferenceUsers;
    private DatabaseReference currentUserDatabaseReference;
    private DatabaseReference postUserDatabaseReference;

    private DatabaseReference databaseReferencePosts;
    private DatabaseReference currentPostDatabaseReference;

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

        currentUserDatabaseReference = databaseReferenceUsers.child(user.getUid());

        databaseReferencePosts = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_POSTS);
        databaseReferencePosts.keepSynced(true);

        savedInstanceState = getIntent().getExtras();

        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey("post_id")) {

                String post_id = savedInstanceState.getString("post_id");
                currentPostDatabaseReference = databaseReferencePosts.child(post_id);

                currentPostDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String postTitle = dataSnapshot.child("title").getValue().toString();
                        String postDesc = dataSnapshot.child("description").getValue().toString();
                        final String postImage_url = dataSnapshot.child("image_url").getValue().toString();
                        String postDateTime = dataSnapshot.child("datetime").getValue().toString();
                        String postUid = dataSnapshot.child("UID").getValue().toString();

                        textPostTitle.setText(postTitle);
                        textPostDescription.setText(postDesc);

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

                        postUserDatabaseReference.addValueEventListener(new ValueEventListener() {
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


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

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
        unbinder.unbind();
        super.onDestroy();
    }

}
