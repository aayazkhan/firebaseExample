package com.firebase.example.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.firebase.example.Auth.LoginActivity;
import com.firebase.example.MyApplication;
import com.firebase.example.PostActivity;
import com.firebase.example.R;
import com.firebase.example.model.Post;
import com.firebase.example.model.User;
import com.firebase.example.viewHolder.PostViewHolder;
import com.firebase.example.viewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SearchUser extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recyclerViewPosts)
    RecyclerView recyclerViewPosts;

    private FirebaseRecyclerAdapter recyclerAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReferenceUsers;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_USERS);
        databaseReferenceUsers.keepSynced(true);

        recyclerViewPosts.setHasFixedSize(true);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        recyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>
                (User.class, R.layout.user_row, UserViewHolder.class, databaseReferenceUsers) {

            @Override
            protected void populateViewHolder(UserViewHolder postViewHolder, User user, int position) {
                postViewHolder.setImage(SearchUser.this, user.getProfilePic());
                postViewHolder.setName(user.getFirstName() + " " + user.getLastName());
            }

        };

        recyclerViewPosts.setAdapter(recyclerAdapter);
    }

    private void updateUI(FirebaseUser user) {
        //TODO
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
