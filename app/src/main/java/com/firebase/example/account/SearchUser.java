package com.firebase.example.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.example.MyApplication;
import com.firebase.example.NewPostActivity;
import com.firebase.example.PostActivity;
import com.firebase.example.R;
import com.firebase.example.model.User;
import com.firebase.example.viewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SearchUser extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recyclerViewUsers)
    RecyclerView recyclerViewUsers;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_USERS);
        databaseReferenceUsers.keepSynced(true);

        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.addItemDecoration(new DividerItemDecoration(SearchUser.this, DividerItemDecoration.VERTICAL));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        recyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>
                (User.class, R.layout.user_row, UserViewHolder.class, databaseReferenceUsers) {

            @Override
            protected void populateViewHolder(UserViewHolder userViewHolder, final User user, final int position) {
                userViewHolder.setImage(SearchUser.this, user.getProfilePic());
                userViewHolder.setName(user.getFirstName() + " " + user.getLastName());


                userViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String USER_KEY = user.getID();

                        Intent intent = new Intent(SearchUser.this, UserProfile.class);
                        intent.putExtra("user_id", USER_KEY);
                        startActivity(intent);
                    }
                });
            }

        };

        recyclerViewUsers.setAdapter(recyclerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search_user, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.textSearch));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query search = databaseReferenceUsers.orderByChild("UserName").startAt(newText);
                final FirebaseRecyclerAdapter<User, UserViewHolder> adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
                        User.class, R.layout.user_row, UserViewHolder.class, search) {
                    @Override
                    protected void populateViewHolder(final UserViewHolder viewHolder, final User user, final int position) {
                        viewHolder.setImage(SearchUser.this, user.getProfilePic());
                        viewHolder.setName(user.getUserName());
                    }
                };

                recyclerViewUsers.setAdapter(adapter);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (android.R.id.home == item.getItemId()) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUI(FirebaseUser user) {
        //TODO
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
