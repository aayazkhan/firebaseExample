package com.firebase.example;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.example.Auth.LoginActivity;
import com.firebase.example.account.AccountProfile;
import com.firebase.example.account.FollowingListActivity;
import com.firebase.example.account.SearchUser;
import com.firebase.example.account.SetupAccount;
import com.firebase.example.account.UserProfile;
import com.firebase.example.model.Post;
import com.firebase.example.model.User;
import com.firebase.example.viewHolder.PostViewHolder;
import com.firebase.example.viewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PostActivity extends AppCompatActivity {

    private static final int NEW_POST_REQUEST = 1;
    private static final int PROFILE = 2;
    private static final int SEARCH = 3;

    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recyclerViewPosts)
    RecyclerView recyclerViewPosts;

    //private FirebaseRecyclerAdapter recyclerAdapter;
    private RecyclerViewAdapter recyclerViewAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReferenceUsers;
    private DatabaseReference databaseReferenceFollowing;

    private DatabaseReference databaseReferencePosts;

    private ProgressDialog progressDialog;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_USERS);
        databaseReferenceUsers.keepSynced(true);

        databaseReferencePosts = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_POSTS);
        databaseReferencePosts.keepSynced(true);

        databaseReferenceFollowing = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_FOLLOWING);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(PostActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        };

        recyclerViewPosts.setHasFixedSize(true);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPosts.addItemDecoration(new DividerItemDecoration(PostActivity.this, DividerItemDecoration.VERTICAL));

        recyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<Post>());
        recyclerViewPosts.setAdapter(recyclerViewAdapter);

        if (user != null) {
            Query queryFollowings = databaseReferenceFollowing.orderByChild("UID").equalTo(user.getUid());

            queryFollowings.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    try {
                        databaseReferencePosts.orderByChild("UID").equalTo(dataSnapshot.child("FollowUID").getValue().toString())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        try {

                                            for (DataSnapshot snapshotPost : dataSnapshot.getChildren()) {
                                                final Post post = new Post();

                                                post.setID(snapshotPost.getKey());
                                                post.setTitle(snapshotPost.child("title").getValue().toString());
                                                post.setDescription(snapshotPost.child("description").getValue().toString());
                                                post.setImage_url(snapshotPost.child("image_url").getValue().toString());
                                                post.setDatetime(snapshotPost.child("datetime").getValue().toString());
                                                post.setUID(snapshotPost.child("UID").getValue().toString());


                                                DataSnapshot snapshotPostLike = dataSnapshot.child(post.getID() + "/" + MyApplication.tbl_POST_LIKE);

                                                post.setLike(snapshotPostLike.hasChild(user.getUid()));
                                                post.setLikeCount(snapshotPostLike.getChildrenCount());

                                                databaseReferenceUsers.child(post.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        User user = new User();

                                                        user.setID(dataSnapshot.child("ID").getValue().toString());
                                                        user.setFirstName(dataSnapshot.child("FirstName").getValue().toString());
                                                        user.setLastName(dataSnapshot.child("LastName").getValue().toString());
                                                        user.setEmail(dataSnapshot.child("Email").getValue().toString());
                                                        user.setMobile(dataSnapshot.child("Mobile").getValue().toString());
                                                        user.setProfilePic(dataSnapshot.child("ProfilePic").getValue().toString());
                                                        user.setUserName(dataSnapshot.child("UserName").getValue().toString());
                                                        user.setUID(dataSnapshot.child("UID").getValue().toString());

                                                        post.setUser(user);

                                                        recyclerViewAdapter.getPosts().add(post);
                                                        Collections.sort(recyclerViewAdapter.getPosts());
                                                        recyclerViewAdapter.notifyDataSetChanged();
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }

                                        } catch (Exception e) {
                                            System.out.println("Exception:" + e);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    } catch (Exception e) {
                        System.out.println("Exception:" + e);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    System.out.println(dataSnapshot);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    try {
                        String followUID = dataSnapshot.child("FollowUID").getValue().toString();

                        for (int i = 0; i < recyclerViewAdapter.getPosts().size(); i++) {
                            if (recyclerViewAdapter.getPosts().get(i).getUID().equalsIgnoreCase(followUID)) {
                                recyclerViewAdapter.getPosts().remove(i--);
                            }
                        }
                        recyclerViewAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        System.out.println("Exception:" + e);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    System.out.println(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        checkUserExist();

        mAuth.addAuthStateListener(authStateListener);
        updateUI(user);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }

    private void checkUserExist() {

        if (user != null) {

            databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(user.getUid())) {

                        Intent intent = new Intent(PostActivity.this, SetupAccount.class);

                        if (user.getDisplayName() != null) {

                            if (user.getDisplayName().contains(" ")) {
                                intent.putExtra("firstName", user.getDisplayName().split(" ")[0]);
                                intent.putExtra("lastName", user.getDisplayName().split(" ")[1]);
                            } else {
                                intent.putExtra("firstName", user.getDisplayName());
                            }
                        }

                        intent.putExtra("email", user.getEmail());

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_post_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (R.id.action_add == item.getItemId()) {
            Intent intent = new Intent(PostActivity.this, NewPostActivity.class);
            startActivityForResult(intent, NEW_POST_REQUEST);
        }

        if (R.id.action_profile == item.getItemId()) {
            Intent intent = new Intent(PostActivity.this, UserProfile.class);
            intent.putExtra("user_id", user.getUid());
            startActivityForResult(intent, PROFILE);
        }

        if (R.id.action_search == item.getItemId()) {
            Intent intent = new Intent(PostActivity.this, SearchUser.class);
            startActivityForResult(intent, SEARCH);
        }

        if (R.id.action_setting == item.getItemId()) {
            Toast.makeText(getApplicationContext(), " WIP ", Toast.LENGTH_LONG).show();
        }

        if (R.id.action_logout == item.getItemId()) {
            mAuth.signOut();
            LoginManager.getInstance().logOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_POST_REQUEST && resultCode == RESULT_OK) {

        }
    }

    private void updateUI(FirebaseUser user) {
        //TODO
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<PostViewHolder> {

        private DatabaseReference databaseReferencePostLike;

        private ArrayList<Post> posts;

        public RecyclerViewAdapter(ArrayList<Post> posts) {
            this.posts = posts;
        }

        public ArrayList<Post> getPosts() {
            return posts;
        }

        @Override
        public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post_row, parent, false);

            PostViewHolder postViewHolder = new PostViewHolder(itemView);

            return postViewHolder;
        }

        @Override
        public void onBindViewHolder(final PostViewHolder postViewHolder, int position) {
            final Post post = posts.get(position);

            postViewHolder.setImage(PostActivity.this, post.getImage_url());
            postViewHolder.setTitle(post.getTitle());
            postViewHolder.setDescription(post.getDescription());

            postViewHolder.setUserNameImage(PostActivity.this, post.getUser().getProfilePic());
            postViewHolder.setUserName(post.getUser().getUserName());

            postViewHolder.getLinearLayoutUser().setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(PostActivity.this, UserProfile.class);
                    intent.putExtra("user_id", post.getUser().getUID());
                    startActivity(intent);
                }
            });

            postViewHolder.getLinearLayoutPost().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(PostActivity.this, SinglePostActivity.class);
                    intent.putExtra("post_id", post.getID());
                    startActivity(intent);
                }
            });

            postViewHolder.setImageViewLike(PostActivity.this, post.isLike());
            postViewHolder.setLikeCount(post.getLikeCount());

            postViewHolder.getImageViewLike().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    databaseReferencePostLike = databaseReferencePosts.child(post.getID() + "/" + MyApplication.tbl_POST_LIKE);
                    if (post.isLike()) {
                        postViewHolder.setImageViewLike(PostActivity.this, false);
                        post.setLike(false);
                        databaseReferencePostLike.child(user.getUid()).removeValue();
                        post.setLikeCount(post.getLikeCount() - 1);
                    } else {
                        postViewHolder.setImageViewLike(PostActivity.this, true);
                        post.setLike(true);
                        databaseReferencePostLike.child(user.getUid()).setValue(simpleDateFormat.format(new Date()));
                        post.setLikeCount(post.getLikeCount() + 1);
                    }
                    postViewHolder.setLikeCount(post.getLikeCount());

                }
            });

        }

        @Override
        public int getItemCount() {
            return posts.size();
        }
    }
}
