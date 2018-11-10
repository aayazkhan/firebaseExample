package com.firebase.example.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.example.PostActivity;
import com.firebase.example.MyApplication;
import com.firebase.example.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginActivity extends AppCompatActivity {

    public static int RC_SIGN_IN = 1;

    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.textEmail)
    EditText textEmail;

    @BindView(R.id.textPassword)
    EditText textPassword;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    @BindView(R.id.btnGoogleSignIn)
    SignInButton btnGoogleSignIn;

    @BindView(R.id.btnFacebookSignIn)
    LoginButton btnFacebookSignIn;

    @BindView(R.id.btnRegister)
    Button btnRegister;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReferenceUsers;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;

    private CallbackManager mCallbackManager;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference(MyApplication.tbl_USERS);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(LoginActivity.this, PostActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        };

        mCallbackManager = CallbackManager.Factory.create();

        btnFacebookSignIn.setReadPermissions("email", "public_profile");
        btnFacebookSignIn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                System.out.println("facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("facebook:onError" + error);
                // ...
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @OnClick(R.id.btnSubmit)
    public void onBtnSubmit() {
        String strEmail = textEmail.getText().toString();
        String strPassword = textPassword.getText().toString();

        if (!TextUtils.isEmpty(strEmail) && !TextUtils.isEmpty(strPassword)) {

            progressDialog.setMessage("Starting Google Sign In ...");
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(strEmail, strPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                System.out.println("signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                System.out.println("signInWithEmail:failure" + task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }

    @OnClick(R.id.btnGoogleSignIn)
    public void onImageViewGoogleSignIn(View v) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @OnClick(R.id.btnRegister)
    public void onBtnRegister() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            progressDialog.setMessage("Starting Google Sign In ...");
            progressDialog.show();

            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                progressDialog.dismiss();
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void handleFacebookAccessToken(AccessToken token) {
        System.out.println("handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            System.out.println("signInWithCredential:failure" + task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        System.out.println("firebaseAuthWithGoogle:" + acct.getId());

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            DatabaseReference newDatabaseReference = databaseReferenceUsers.child(user.getUid());

                            newDatabaseReference.child("UID").setValue(user.getUid());

                            newDatabaseReference.child("ID").setValue(acct.getId());
                            newDatabaseReference.child("FirstName").setValue(acct.getGivenName());
                            newDatabaseReference.child("LastName").setValue(acct.getFamilyName());
                            newDatabaseReference.child("Email").setValue(acct.getEmail());
                            newDatabaseReference.child("ProfilePic").setValue(user.getPhotoUrl());

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
