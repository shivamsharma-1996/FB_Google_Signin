package com.shivam.pgb_signup_login;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextInputLayout tilEmail, tilPassword;
    private Button btn_register;
    private String email, password;
    private LoginButton loginButton;

    //a constant for detecting the login intent result
    private static final int RC_SIGN_IN = 234;

    //Tag for the logs optional
    private static final String TAG = new MainActivity().getClass().getSimpleName();

    //creating a GoogleSignInClient object
    GoogleSignInClient mGoogleSignInClient;

    CallbackManager callbackManager;
    private static final String EMAIL = "email";


/*
    When the signIn button is clicked, the user will first be authenticated with Google and if that is successful, we’ll now authenticate the user with Firebase after saving user data to SharedPreference, so we can have access to them for display in the Header view of our Navigation Drawer Activity. So it’s a 2 stage authentication, the 1st stage is with Google and the Second with Firebase. So i guess this provides a double and secure authentication process.
*/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.shivam.pgb_signup_login",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        callbackManager = CallbackManager.Factory.create();


        initfbLogging();

        loginButton =  findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));

        callbackManager = CallbackManager.Factory.create();


//        // Callback registration
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                // App code
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//            }
//        });


        tilEmail = findViewById(R.id.tilEmail);
        tilPassword =findViewById(R.id.tilPassword);

        btn_register = findViewById(R.id.btn_register);

       /* email = tilEmail.getEditText().getText().toString().trim();
        password = tilPassword.getEditText().getText().toString().trim();*/

        mAuth = FirebaseAuth.getInstance();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestId()
                .requestEmail()
                .build();


        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Now we will attach a click listener to the sign_in_button
        //and inside onClick() method we are calling the signIn() method that will open
        //google sign in intent
        findViewById(R.id.tv_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                signIn();
            }
        });


        closeKeyboard();
    }

    private void initfbLogging()
    {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Toast.makeText(MainActivity.this, "success fb", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(MainActivity.this, "error fb", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void closeKeyboard()
    {
        View view = this.getCurrentFocus();
        if(view!=null)
        {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
            //im.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if(mAuth.getCurrentUser()!= null)
        {
            Toast.makeText(MainActivity.this, "getCurrentUser() is not null", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(MainActivity.this, "getCurrentUser() is null", Toast.LENGTH_SHORT).show();
        }

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }

    //this method is called on click
    private void signIn()
    {
        //getting the google signin intent
        /*if (mGoogleSignInClient.(Auth.GOOGLE_SIGN_IN_API))
        {
            mGoogleSignInClient.clearDefaultAccountAndReconnect();
        }*/
        mGoogleSignInClient.revokeAccess();
        //mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    // This IS the method where the result of clicking the signIn button will be handled
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //Every activity and fragment that you integrate with the FacebookSDK Login or Share should forward onActivityResult to the callbackManager
        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN)
        {
            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            }
            catch (ApiException e)
            {
                //this exception ,ight be due to internet problem or user open acccnt chooser & then cancel it
                //Toast.makeText(MainActivity.this, e.getMessage() + "you have to choose an account", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount)
    {
        String userName = "";
        String userEmail = "";
        Log.d(TAG, "firebaseAuthWithGoogle:" + googleSignInAccount.getId());

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

        if (googleSignInAccount != null)
        {
            userName = googleSignInAccount.getDisplayName();
            userEmail = googleSignInAccount.getEmail();
            //String personGivenName = googleSignInAccount.getGivenName();
            //String personId = googleSignInAccount.getId();
            //Uri personPhoto = googleSignInAccount.getPhotoUrl();
            //Log.i("accnt details", personName + personGivenName + personEmail + personId + personPhoto);
        }

        //Now using firebase we are signing in the user here so, after completion of this task "credential are stored in firebase authentication service" with provider(as Google)
        final String finalUserEmail = userEmail;
        final String finalUserName = userName;

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "User Signed In", Toast.LENGTH_SHORT).show();

                            Intent accntSetup_intent = new Intent(MainActivity.this, Account_SetupActivity.class);
                            accntSetup_intent.putExtra("email", finalUserEmail);
                            accntSetup_intent.putExtra("name", finalUserName);

                            startActivity(accntSetup_intent);
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithCredential:failure", task.getException());
                            //Toast.makeText(MainActivity.this, "Firebase Authentication failed.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "Please check you internet.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void updateUI(FirebaseUser currentUser)
    {
        if(currentUser!= null)
        {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(MainActivity.this, "Authentication success", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Authentication failed(a/c already created", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(MainActivity.this, "Already signed in", Toast.LENGTH_SHORT).show();
        }
    }

}







//mAuth.signInWithEmailAndPassword(email, password)
//        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//@Override
//public void onComplete(@NonNull Task<AuthResult> task) {
//        if (task.isSuccessful()) {
//        // Sign in success, update UI with the signed-in user's information
//        Log.d(TAG, "signInWithEmail:success");
//        FirebaseUser user = mAuth.getCurrentUser();
//        updateUI(user);
//        } else {
//        // If sign in fails, display a message to the user.
//        Log.w(TAG, "signInWithEmail:failure", task.getException());
//        Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//        Toast.LENGTH_SHORT).show();
//        updateUI(null);
//        }
//
//        // ...
//        }
//        });
