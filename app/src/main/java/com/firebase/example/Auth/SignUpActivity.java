package com.firebase.example.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.example.MyApplication;
import com.firebase.example.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SignUpActivity extends AppCompatActivity {

    public static int RC_SIGN_IN = 1;

    private Unbinder unbinder;

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

    private FirebaseAuth mAuth;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    private String strFirstName, strLastName, strEmail, strMobile, strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        unbinder = ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(MyApplication.tbl_USER);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @OnClick(R.id.btnSubmit)
    public void onBtnSubmit(View v) {

        strFirstName = textFirstName.getText().toString();
        strLastName = textLastName.getText().toString();
        strEmail = textEmail.getText().toString();
        strMobile = textMobile.getText().toString();
        strPassword = textPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("createUserWithEmail:success");

                            DatabaseReference newDatabaseReference = databaseReference.push();

                            newDatabaseReference.child("FirstName").setValue(strFirstName);
                            newDatabaseReference.child("LastName").setValue(strLastName);
                            newDatabaseReference.child("Email").setValue(strEmail);
                            newDatabaseReference.child("Mobile").setValue(strMobile);
                            newDatabaseReference.child("Password").setValue(strPassword);


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

    @OnClick(R.id.imageViewGoogleSignIn)
    public void onImageViewGoogleSignIn(View v) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                System.out.println("Google sign in failed" + e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        System.out.println("firebaseAuthWithGoogle:" + acct.getId());

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("signInWithCredential:success");

                            acct.getDisplayName();
                            acct.getEmail();
                            acct.getFamilyName();

                            DatabaseReference newDatabaseReference = databaseReference.push();

                            newDatabaseReference.child("FirstName").setValue(acct.getDisplayName());
                            newDatabaseReference.child("LastName").setValue(strLastName);
                            newDatabaseReference.child("Email").setValue(strEmail);
                            newDatabaseReference.child("Mobile").setValue(strMobile);
                            newDatabaseReference.child("Password").setValue(strPassword);

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            System.out.println("signInWithCredential:failure" + task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
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
