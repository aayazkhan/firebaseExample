package com.firebase.example.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.firebase.example.MyApplication;
import com.firebase.example.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AccountProfile extends AppCompatActivity {

    public static int GALLERY_REQUEST = 1;

    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.imgUserProfilePic)
    ImageView imgUserProfilePic;

    @BindView(R.id.textFirstName)
    EditText textFirstName;

    @BindView(R.id.textLastName)
    EditText textLastName;

    @BindView(R.id.textEmail)
    EditText textEmail;

    @BindView(R.id.textMobile)
    EditText textMobile;

    @BindView(R.id.rdoGrpGender)
    RadioGroup rdoGrpGender;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReferenceUsers;
    private DatabaseReference currentUserDatabaseReference;

    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_USERS);
        databaseReferenceUsers.keepSynced(true);

        currentUserDatabaseReference = databaseReferenceUsers.child(user.getUid());

        storageReference = FirebaseStorage.getInstance().getReference(MyApplication.FOLDER_POST_IMAGE);
    }

    @OnClick(R.id.imgUserProfilePic)
    public void onImageBtnPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @OnClick(R.id.btnSubmit)
    public void onBtnSubmit() {
        //TODO
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            imgUserProfilePic.setImageURI(imageUri);
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
