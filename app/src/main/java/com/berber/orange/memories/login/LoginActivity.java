package com.berber.orange.memories.login;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = "LoginActivity";


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button registerButton;
    private Button signInWIthEmailButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        registerButton = (Button) findViewById(R.id.email_register_button);
        signInWIthEmailButton = (Button) findViewById(R.id.email_sign_in_email_button);

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_register_button:
                Log.d(TAG,"creare Account");
                createAccount();
                break;
            case R.id.email_sign_in_email_button:
                Log.d(TAG,"getCurrentUSer");

                getCurrentUser();
                break;
        }
    }

    private void createAccount() {
        final String password = mPasswordView.getText().toString();
        final String validateEmail = validate(mEmailView.getText().toString());
        Log.d(TAG, "createUserWithEmail:onComplete:" + validateEmail+ password);
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
                }
                else { Toast.makeText(LoginActivity.this, "Create User Account succeeds",
                        Toast.LENGTH_SHORT).show();
                    signInWithEmailAndPassword(validateEmail, password);}

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


}






