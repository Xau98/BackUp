package com.android.backup.account;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class Login {
    GoogleSignInClient mGoogleSignInClient;
    public static final String ID_CLIENT_GG="797362158064-h1cjks1abj7vqphiu6rc0429jsd3puet.apps.googleusercontent.com";
    Context mContext;
    public  void  Login(Context context){
        mContext = context;
    }
    public  Intent loginGG(){
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(ID_CLIENT_GG)
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
       return  signInIntent;
    }




}
