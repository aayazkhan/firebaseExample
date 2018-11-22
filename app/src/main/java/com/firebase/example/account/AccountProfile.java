package com.firebase.example.account;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.example.MyApplication;
import com.firebase.example.R;
import com.firebase.example.SinglePostActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;

public class AccountProfile extends AppCompatActivity {

    public static int GALLERY_REQUEST = 1;

    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.imgUserProfilePic)
    ImageView imgUserProfilePic;

    @BindView(R.id.linearLayoutFollower)
    LinearLayout linearLayoutFollower;

    @BindView(R.id.textFollowerCount)
    TextView textFollowerCount;

    @BindView(R.id.linearLayoutFollowing)
    LinearLayout linearLayoutFollowing;

    @BindView(R.id.textFollowingCount)
    TextView textFollowingCount;

    @BindView(R.id.textFirstName)
    EditText textFirstName;

    @BindView(R.id.textLastName)
    EditText textLastName;

    @BindView(R.id.textEmail)
    EditText textEmail;

    @BindView(R.id.textMobile)
    EditText textMobile;

    @BindView(R.id.textGender)
    EditText textGender;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReferenceUsers;
    private DatabaseReference currentUserDatabaseReference;

    private DatabaseReference databaseReferencePosts;
    private DatabaseReference databaseReferenceFollowings;
    private DatabaseReference userDatabaseReferenceFollowings;
    private DatabaseReference userDatabaseReferenceFollowers;

    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    private Uri imageUri;

    private String strFirstName, strLastName, strEmail, strMobile, strGender, strImageUrl;


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


        databaseReferencePosts = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_POSTS);
        databaseReferencePosts.keepSynced(true);

        databaseReferenceFollowings = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_FOLLOWING);
        databaseReferenceFollowings.keepSynced(true);

        Query queryFollowings = databaseReferenceFollowings.orderByChild("UID").equalTo(user.getUid());

        queryFollowings.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //TODO
                textFollowingCount.setText("" + ((int) dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query queryFollowers = databaseReferenceFollowings.orderByChild("FollowUID").equalTo(user.getUid());

        queryFollowers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //TODO
                textFollowerCount.setText("" + ((int) dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        currentUserDatabaseReference = databaseReferenceUsers.child(user.getUid());

        storageReference = FirebaseStorage.getInstance().getReference(MyApplication.FOLDER_POST_IMAGE);

        currentUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    strFirstName = dataSnapshot.child("FirstName").getValue().toString();
                    strLastName = dataSnapshot.child("LastName").getValue().toString();
                    strEmail = dataSnapshot.child("Email").getValue().toString();
                    strMobile = dataSnapshot.child("Mobile").getValue().toString();
                    strGender = dataSnapshot.child("Gender").getValue().toString();
                    strImageUrl = dataSnapshot.child("ProfilePic").getValue().toString();

                    textFirstName.setText(strFirstName);
                    textLastName.setText(strLastName);
                    textEmail.setText(strEmail);
                    textMobile.setText(strMobile);
                    textGender.setText(strGender);

                    Picasso.with(AccountProfile.this).load(strImageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(imgUserProfilePic, new Callback() {

                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            Picasso.with(AccountProfile.this).load(strImageUrl).into(imgUserProfilePic);
                        }

                    });


//                text.setText(dataSnapshot.child("").getValue().toString());

                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @OnLongClick({R.id.textFirstName, R.id.textLastName, R.id.textEmail, R.id.textMobile, R.id.textGender})
    public boolean onTextLongClick(View v) {
        switch (v.getId()) {
            case R.id.textFirstName:
                showDialogToUpdate("First Name", strFirstName, "FirstName");
                break;
            case R.id.textLastName:
                showDialogToUpdate("Last Name", strLastName, "LastName");
                break;
            case R.id.textEmail:
//                showDialogToUpdate("Email", strEmail, "Email");
                Toast.makeText(getApplicationContext(), " cannot change email ! ", Toast.LENGTH_LONG).show();
                break;
            case R.id.textMobile:
                showDialogToUpdate("Mobile", strMobile, "Mobile");
                break;
            case R.id.textGender:
                showRadioDialogToUpdate("Gender", strGender, "Gender");
                break;
        }

        return true;
    }

    @OnClick(R.id.linearLayoutFollowing)
    public void onLinearLayoutFollowing() {
        Intent intent = new Intent(AccountProfile.this, FollowingListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.linearLayoutFollower)
    public void onLinearLayoutFollower() {
        Intent intent = new Intent(AccountProfile.this, FollowerListActivity.class);
        startActivity(intent);
    }

    private void showDialogToUpdate(String title, String value, final String serverVeriableName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update");

        final EditText editText = new EditText(AccountProfile.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50, 0, 50, 0);
        editText.setLines(1);
        editText.setText(value);

        TextInputLayout textInputLayout = new TextInputLayout(AccountProfile.this);
        textInputLayout.setHint(title);
        textInputLayout.addView(editText, layoutParams);

        builder.setView(textInputLayout);

        builder.setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (serverVeriableName.equalsIgnoreCase("firstname")) {

                    currentUserDatabaseReference.child(serverVeriableName).setValue(editText.getText().toString());
                    currentUserDatabaseReference.child("UserName").setValue(editText.getText().toString() + " " + strLastName);

                } else if (serverVeriableName.equalsIgnoreCase("lastname")) {

                    currentUserDatabaseReference.child(serverVeriableName).setValue(editText.getText().toString());
                    currentUserDatabaseReference.child("UserName").setValue(strFirstName + " " + editText.getText().toString());

                } else {
                    currentUserDatabaseReference.child(serverVeriableName).setValue(editText.getText().toString());
                }
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void showRadioDialogToUpdate(String title, String value, final String serverVeriableName) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50, 0, 50, 0);

        final RadioGroup radioGroup = new RadioGroup(AccountProfile.this);

        RadioButton radioButtonMale = new RadioButton(AccountProfile.this);
        radioButtonMale.setId(View.generateViewId());
        radioButtonMale.setText("Male");
        radioGroup.addView(radioButtonMale, layoutParams);

        RadioButton radioButtonFemale = new RadioButton(AccountProfile.this);
        radioButtonFemale.setId(View.generateViewId());
        radioButtonFemale.setText("Female");
        radioGroup.addView(radioButtonFemale, layoutParams);

        if (value.equalsIgnoreCase("male")) {
            radioButtonMale.setChecked(true);
        } else if (value.equalsIgnoreCase("female")) {
            radioButtonFemale.setChecked(true);
        }

        final LinearLayout linearLayout = new LinearLayout(AccountProfile.this);

        linearLayout.addView(radioGroup, layoutParams);

        builder.setView(linearLayout);

        builder.setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                RadioButton radioButton = (RadioButton) linearLayout.findViewById(radioGroup.getCheckedRadioButtonId());
                currentUserDatabaseReference.child(serverVeriableName).setValue(radioButton.getText().toString());

            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    @OnClick(R.id.imgUserProfilePic)
    public void onImageBtnPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
//            imgUserProfilePic.setImageURI(imageUri);
            uploadProfilePic();

        }

    }

    private void uploadProfilePic() {
        progressDialog.setMessage("uploading ...");
        progressDialog.show();

        StorageReference newStorageReference = storageReference.child(imageUri.getLastPathSegment());

        newStorageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                currentUserDatabaseReference = databaseReferenceUsers.child(user.getUid());

                currentUserDatabaseReference.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentUserDatabaseReference.child("ProfilePic").setValue(downloadUrl.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

                progressDialog.dismiss();

            }
        });
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
