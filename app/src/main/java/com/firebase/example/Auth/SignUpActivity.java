package com.firebase.example.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.example.PostActivity;
import com.firebase.example.MyApplication;
import com.firebase.example.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SignUpActivity extends AppCompatActivity {


    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.textFirstName)
    EditText textFirstName;

    @BindView(R.id.textLastName)
    EditText textLastName;

    @BindView(R.id.textEmail)
    EditText textEmail;

    @BindView(R.id.textMobile)
    EditText textMobile;

    @BindView(R.id.textPassword)
    EditText textPassword;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    @BindView(R.id.btnLogin)
    Button btnLogin;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    private String strFirstName, strLastName, strEmail, strMobile, strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(MyApplication.tbl_USERS);
        databaseReference.keepSynced(true);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(SignUpActivity.this, SetupActivity.class);

                    intent.putExtra("firstName", strFirstName);
                    intent.putExtra("lastName", strLastName);
                    intent.putExtra("email", strEmail);
                    intent.putExtra("mobile", strMobile);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        };

    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }

    @OnClick(R.id.btnSubmit)
    public void onBtnSubmit(View v) {

        strFirstName = textFirstName.getText().toString();
        strLastName = textLastName.getText().toString();
        strEmail = textEmail.getText().toString();
        strMobile = textMobile.getText().toString();
        strPassword = textPassword.getText().toString();

        progressDialog.setMessage("Starting Sign In ...");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("createUserWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();


                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            System.err.println("createUserWithEmail:failure" + task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    @OnClick(R.id.btnLogin)
    public void onBtnLogin() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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