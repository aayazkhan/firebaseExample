package com.firebase.example;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.example.Auth.LoginActivity;
import com.firebase.example.model.Post;
import com.firebase.example.viewHolder.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PostActivity extends AppCompatActivity {

    private static final int NEW_POST_REQUEST = 1;

    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recyclerViewPosts)
    RecyclerView recyclerViewPosts;

    private FirebaseRecyclerAdapter recyclerAdapter;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReferenceUsers;
    private DatabaseReference databaseReferencePosts;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_USERS);
        databaseReferenceUsers.keepSynced(true);

        databaseReferencePosts = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_POSTS);
        databaseReferencePosts.keepSynced(true);

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

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        recyclerAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>
                (Post.class, R.layout.post_row, PostViewHolder.class, databaseReferencePosts) {

            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Post model, int position) {
                viewHolder.setImage(PostActivity.this, model.getImage_url());
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
            }

        };

        recyclerViewPosts.setAdapter(recyclerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_post, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (R.id.action_add == item.getItemId()) {
            Intent intent = new Intent(PostActivity.this, NewPostActivity.class);
            startActivityForResult(intent, NEW_POST_REQUEST);
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
            //recyclerAdapter.
        }
    }

    private void updateUI(FirebaseUser user) {
        //TODO

        if (user != null) {
            Toast.makeText(getApplicationContext(), user.getProviderId(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
