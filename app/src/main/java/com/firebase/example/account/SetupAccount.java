package com.firebase.example.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.firebase.example.MyApplication;
import com.firebase.example.PostActivity;
import com.firebase.example.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class SetupAccount extends AppCompatActivity {

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
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    private Uri imageUri;
    private String strFirstName, strLastName, strEmail, strMobile, strGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setup);


        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(MyApplication.tbl_USERS);
        databaseReference.keepSynced(true);

        storageReference = FirebaseStorage.getInstance().getReference(MyApplication.FOLDER_PROFILE_IMAGE);

        savedInstanceState = getIntent().getExtras();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("firstName")) {
                textFirstName.setText(savedInstanceState.getString("firstName"));
            }
            if (savedInstanceState.containsKey("lastName")) {
                textLastName.setText(savedInstanceState.getString("lastName"));
            }
            if (savedInstanceState.containsKey("email")) {
                textEmail.setText(savedInstanceState.getString("email"));
            }
            if (savedInstanceState.containsKey("mobile")) {
                textMobile.setText(savedInstanceState.getString("mobile"));
            }
            //     if (savedInstanceState.containsKey("profile_pic_uri")) {
            //         imageUri = Uri.parse(savedInstanceState.getString("profile_pic_uri"));
            //         imgUserProfilePic.setImageURI(imageUri);
            //     }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    @OnClick(R.id.imgUserProfilePic)
    public void onImgUserProfilePic() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @OnClick(R.id.btnSubmit)
    public void onBtnSubmit(View v) {

        strFirstName = textFirstName.getText().toString();
        strLastName = textLastName.getText().toString();
        strEmail = textEmail.getText().toString();
        strMobile = textMobile.getText().toString();
        strGender = ((RadioButton) findViewById(rdoGrpGender.getCheckedRadioButtonId())).getText().toString();

        if (imageUri == null) {
            Toast.makeText(SetupAccount.this, "Please profile image. ", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(strFirstName)
                || TextUtils.isEmpty(strLastName)
                || TextUtils.isEmpty(strEmail)
                || TextUtils.isEmpty(strMobile)
                || TextUtils.isEmpty(strGender)) {

            Toast.makeText(getApplicationContext(), "PLEASE FILL PROPER DETAILS", Toast.LENGTH_LONG).show();

        } else {

            progressDialog.setMessage("Updating ...");
            progressDialog.show();
            StorageReference newStorageReference = storageReference.child(imageUri.getLastPathSegment());

            newStorageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final FirebaseUser user = mAuth.getCurrentUser();

                    DatabaseReference newDatabaseReference = databaseReference.child(user.getUid());

                    newDatabaseReference.child("UID").setValue(user.getUid());

                    newDatabaseReference.child("ID").setValue(user.getUid());
                    newDatabaseReference.child("FirstName").setValue(strFirstName);
                    newDatabaseReference.child("LastName").setValue(strLastName);
                    newDatabaseReference.child("Email").setValue(strEmail);
                    newDatabaseReference.child("Mobile").setValue(strMobile);
                    newDatabaseReference.child("Gender").setValue(strGender);
                    newDatabaseReference.child("ProfilePic").setValue(downloadUrl.toString());
                    newDatabaseReference.child("UserName").setValue(strFirstName + " " + strLastName)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Intent intent = new Intent(SetupAccount.this, PostActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();

                                }
                            });

                    progressDialog.dismiss();
                }
            });

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            imgUserProfilePic.setImageURI(imageUri);
        }

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
