package com.example.jeff.tiprequest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;

public class SignInActivity extends AppCompatActivity {

    final int RC_SIGNED_IN = 101;
    final int RC_SIGNED_OUT = 102;
    GoogleSignInClient mGoogleSignInClient;
    ProgressBar spinner;
    GoogleSignInAccount account;
    SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.lightBlue2, getTheme()));
        }
        setContentView(R.layout.signin_activity);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        spinner.setVisibility(View.VISIBLE);
        String userID = sharedPref.getString("userID", "");
        account = GoogleSignIn.getLastSignedInAccount(this);
        if (!userID.equals("")) {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            UserInfo.setAccountID(account.getId());
            UserInfo.setAccountName(account.getDisplayName());
            startActivityForResult(mainIntent, RC_SIGNED_OUT);
        } else {
            spinner.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RC_SIGNED_IN) {
                if (resultCode==-1) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    GoogleSignInAccount acc = task.getResult();
                    handleSignInResult(acc);
                } else {
                    spinner.setVisibility(View.INVISIBLE);
                }
            } else if (requestCode == RC_SIGNED_OUT) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("userID", "");
                editor.commit();
            }
    }

    private void signIn() {
        spinner.setVisibility(View.VISIBLE);
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGNED_IN);
    }

    private void handleSignInResult(GoogleSignInAccount account) {
        String name;
        if (account.getDisplayName() == null) {
            name = account.getEmail();
        } else {
            name = account.getDisplayName();
        }
        UserInfo.setAccountName(name);
        UserInfo.setAccountID(account.getId());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userID", account.getId());
        editor.commit();
        System.out.println("****************************** New Sign In: " + name);
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        UserInfo.setAccountID(account.getId());
        UserInfo.setAccountName(account.getDisplayName());
        spinner.setVisibility(View.INVISIBLE);
        startActivityForResult(mainIntent, RC_SIGNED_OUT);
    }
}
