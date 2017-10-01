package com.berber.orange.memories.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.net.Uri;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.berber.orange.memories.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private static final String WEB_ID = "1007045828483-limqn92o7logo67ddrqejj3hn7fcurnr.apps.googleusercontent.com";


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button registerButton;
    private Button signInWIthEmailButton;
    private Button signOutButton;
    private Button signInWithGoogleButton;

    private Button signInWithFacebookButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        registerButton = (Button) findViewById(R.id.email_register_button);
        signInWIthEmailButton = (Button) findViewById(R.id.email_sign_in_email_button);

        signInWithGoogleButton = findViewById(R.id.sign_in_google_button);
        signOutButton = findViewById(R.id.sign_out_button);
        signInWithFacebookButton = findViewById(R.id.sign_in_facebook_button);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    // USer is signed in
                    Log.d(TAG, "onAuthStateCHanged:signed in: " + currentUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        registerButton.setOnClickListener(this);
        signInWIthEmailButton.setOnClickListener(this);
        signInWithGoogleButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
        signInWithFacebookButton.setOnClickListener(this);

        //prepare google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(WEB_ID)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);

        mGoogleApiClient.disconnect();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_register_button:
                Log.d(TAG, "creare Account");
                createAccount();
                break;
            case R.id.email_sign_in_email_button:
                Log.d(TAG, "getCurrentUSer");

                getCurrentUser();
                break;

            case R.id.sign_in_google_button:
                signInWithGoogle();
                break;
            case R.id.sign_in_facebook_button:
                signInWithFacebook();
                break;
            case R.id.sign_out_button:
                //FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.d(TAG, "status " + status);
                    }
                });
                break;
        }
    }

    private void signInWithFacebook() {

    }


    //sign in with google account
    private void signInWithGoogle() {
        Intent googleSignInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(googleSignInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(googleSignInResult);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult googleSignInResult) {
        Log.d(TAG, "handleGoogleSignInResult:" + googleSignInResult.isSuccess() + " " + googleSignInResult.getStatus());
        if (googleSignInResult.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = googleSignInResult.getSignInAccount();
            Log.d(TAG, "handleGoogleSignInResult:" + acct.getDisplayName() + " " + acct.getPhotoUrl());
            firebaseAuthWithGoogle(acct);
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            // updateUI(false);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "signInWithCredential:success: " + user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }

    private void createAccount() {
        final String password = mPasswordView.getText().toString();
        final String validateEmail = validate(mEmailView.getText().toString());
        Log.d(TAG, "createUserWithEmail:onComplete:" + validateEmail + password);
        mAuth.createUserWithEmailAndPassword(validateEmail, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Failed=" + task.getException().getMessage());
                    Toast.makeText(LoginActivity.this, "Create User Account failed",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Create User Account succeeds",
                            Toast.LENGTH_SHORT).show();
                    signInWithEmailAndPassword(validateEmail, password);
                }

            }
        });
    }

    private void signInWithEmailAndPassword(String validateEmail, String password) {
        mAuth.signInWithEmailAndPassword(validateEmail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Create User Account failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private String validate(String email) {
        //todo regular expression to validate the email format
        return email;

    }

    private void getCurrentUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            MyUser myUser = new MyUser();
            // Name, email address, and profile photo Url
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            Uri photoUrl = currentUser.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = currentUser.getUid();

            myUser.setName(name);
            myUser.setEmail(email);
            myUser.setPhotoUrl(photoUrl);
            myUser.setUid(uid);
            System.out.println(myUser);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "google sign in onConnectionFailed: " + connectionResult.getErrorMessage());
    }
}






