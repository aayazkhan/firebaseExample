package com.firebase.example.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.example.MyApplication;
import com.firebase.example.R;
import com.firebase.example.model.User;
import com.firebase.example.viewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FollowingListActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recyclerViewUsers)
    RecyclerView recyclerViewUsers;

    private RecyclerViewAdapter recyclerViewAdapter;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReferenceUsers;
    private DatabaseReference databaseReferenceFollowing;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_list);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_USERS);

        databaseReferenceFollowing = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_FOLLOWING);

        Query queryFollowings = databaseReferenceFollowing.orderByChild("UID").equalTo(user.getUid());

        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.addItemDecoration(new DividerItemDecoration(FollowingListActivity.this, DividerItemDecoration.VERTICAL));

        recyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<User>());

        recyclerViewUsers.setAdapter(recyclerViewAdapter);

        queryFollowings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    databaseReferenceUsers.child(snapshot.child("FollowUID").getValue().toString())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = new User();
                                    user.setFirstName(dataSnapshot.child("FirstName").getValue().toString());
                                    user.setLastName(dataSnapshot.child("LastName").getValue().toString());
                                    user.setProfilePic(dataSnapshot.child("ProfilePic").getValue().toString());

                                    recyclerViewAdapter.getUsers().add(user);
                                    recyclerViewAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (android.R.id.home == item.getItemId()) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<UserViewHolder> {

        private ArrayList<User> users;

        public RecyclerViewAdapter(ArrayList<User> users) {

            this.users = users;

        }

        public ArrayList<User> getUsers() {
            return users;
        }

        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_row, parent, false);

            UserViewHolder userViewHolder = new UserViewHolder(itemView);

            return userViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int position) {

            User user = users.get(position);

            userViewHolder.setImage(FollowingListActivity.this, user.getProfilePic());
            userViewHolder.setName(user.getFirstName() + " " + user.getLastName());
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }
}
