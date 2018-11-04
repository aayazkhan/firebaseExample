package com.firebase.example;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.example.Auth.LoginActivity;
import com.firebase.example.model.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NewPostActivity extends AppCompatActivity {

    public static int GALLERY_REQUEST = 1;

    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.imageBtnPhoto)
    ImageButton imgBtnPhoto;

    @BindView(R.id.textImageTitle)
    EditText textImageTitle;

    @BindView(R.id.textImageDescription)
    EditText textImageDescription;

    @BindView(R.id.btnSubmit)
    Button btnSumit;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReferenceUsers;
    private DatabaseReference databaseReferencePosts;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_USERS);
        databaseReferenceUsers.keepSynced(true);

        databaseReferencePosts = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_POSTS);
        databaseReferencePosts.keepSynced(true);

        storageReference = FirebaseStorage.getInstance().getReference(MyApplication.FOLDER_POST_IMAGE);


    }

    @OnClick(R.id.imageBtnPhoto)
    public void onImageBtnPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }


    @OnClick(R.id.btnSubmit)
    public void onBtnSubmit() {
        startPosting();
    }

    private void startPosting() {
        final String strTitle = textImageTitle.getText().toString().trim();
        final String strDescription = textImageDescription.getText().toString().trim();

        if (TextUtils.isEmpty(strTitle) || TextUtils.isEmpty(strDescription) || imageUri == null) {

            Toast.makeText(getApplicationContext(), "PLEASE FILL PROPER DETAILS", Toast.LENGTH_LONG).show();

        } else {

            progressDialog.setMessage("Post to blog ...");
            progressDialog.show();
            StorageReference newStorageReference = storageReference.child(imageUri.getLastPathSegment());

            newStorageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    DatabaseReference newDatabaseReference = databaseReferencePosts.push();

                    newDatabaseReference.child("title").setValue(strTitle);
                    newDatabaseReference.child("description").setValue(strDescription);
                    newDatabaseReference.child("image_url").setValue(downloadUrl.toString());
                    newDatabaseReference.child("UID").setValue(user.getUid());

                    progressDialog.dismiss();

                    Post newPost = new Post();

                    newPost.setTitle(strTitle);
                    newPost.setDescription(strDescription);
                    newPost.setImage_url(downloadUrl.toString());
                    newPost.setUid(user.getUid());

                    Intent intent = new Intent();
                    intent.putExtra("new_post", newPost);

                    setResult(RESULT_OK, intent);
                    finish();

                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            imgBtnPhoto.setImageURI(imageUri);
        }

    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

}
