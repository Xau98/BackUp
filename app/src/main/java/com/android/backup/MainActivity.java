package com.android.backup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import at.favre.lib.crypto.bcrypt.BCrypt;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends Activity {
    CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;
    private static final String EMAIL = "email";
    private static final String USER_POSTS = "user_posts";
    private static final String AUTH_TYPE = "rerequest";
    public static final int RC_SIGN_IN = 1;
    public static final int CODE_PERMISSION = 2405;
    public static final String TAG = "TienNVh";
    public static final String SHARED_PRE_TOKEN = "com.android.backup.token";
    public static final String SHAREPREFENCE = "SHAREPREFENCE_ACCOUNT";
    public static final String ID_CLIENT_GG = "797362158064-h1cjks1abj7vqphiu6rc0429jsd3puet.apps.googleusercontent.com";
    public static final String TOKENT_CLIENT = "tokent_client";
    public static final int MSG_LOGIN = 1;
    Callback callback;
    Handler mHandler;
    String mJsonData;
    Button bt_login;
    SignInButton loginGG;//Google
    LoginButton loginFB;//Facebook
    EditText mUsername, mPassword;
    private SharedPreferences mPreferences;
    ArrayList<FileItem> mListAllFile = new ArrayList<>();
    private boolean mAutheAC = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (savedInstanceState != null) {
            String tokent = savedInstanceState.getString(TOKENT_CLIENT);
            if (account != null) {
                mAutheAC = decryptToken(tokent, "GG" + account.getId());
            }
        }

        setContentView(R.layout.activity_main);


        bt_login = findViewById(R.id.bt_loginAC);
        loginGG = findViewById(R.id.loginGG_button);
        loginGG.setSize(SignInButton.SIZE_STANDARD);
        loginFB = (LoginButton) findViewById(R.id.loginFB_button);
        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        //========================Khoi tao============================================
        mPreferences = getSharedPreferences(SHARED_PRE_TOKEN, MODE_PRIVATE);
        // Bkav TienNVh : Cấp quyền
         Permission permission = new Permission(this,this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && permission.isReadStoragePermissionGranted() && permission.isWriteStoragePermissionGranted()) {


        }
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(ID_CLIENT_GG)
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //========================Login Account=========================================
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onLoginAcoount();
            }
        });
        //========================Login GG============================================
        loginGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginGoogle();
            }
        });
        //=======================Login FB=============================================
        loginFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginFacebook();
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case MSG_LOGIN:
                        try {
                            JSONObject Jobject = new JSONObject(mJsonData);
                            SharedPreferences sharedPref =  getSharedPreferences(SHAREPREFENCE,  MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("id", Jobject.getInt("id")+"");
                            editor.putString("name", Jobject.getString("name"));
                            editor.putString("token", Jobject.getString("token"));
                            editor.putString("email", Jobject.getString("email"));
                            editor.putString("date_create", Jobject.getString("date_create"));
                            editor.commit();
                            Intent intent = new Intent(getBaseContext(), HomePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Toast.makeText(getBaseContext(), "Đăng nhập thành công :" + Jobject.getInt("id"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };


        callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    //mJsonData = response.body().string();
                    InputStream bitmap= response.body().byteStream();
                    File file = new File(handleFile.PATH_ROOT+"/Android");
                    copyInputStreamToFile(bitmap,file);
                   // mHandler.sendEmptyMessage(MSG_LOGIN);
                }
            }
        };


    }
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    private static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        // append = false
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }

    }

    // Bkav TienNVh : callback Permisson
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


            switch (requestCode){
                case 2405:

                    if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                         
                        //resume tasks needing this permission
                        Log.d("Tiennvh", "onRequestPermissionsResult: ");
                    }else{
                        Log.d("Tiennvh", "onRequestPermissionsResult: FALSE");
                    } 
                    break;
              case 2406:
                  Log.d(TAG, "External storage1");
                  if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                      Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                      //resume tasks needing this permission

                  }else{
                      Log.d("Tiennvh", "onRequestPermissionsResult: FALSE");
                  }
                  break;


        }
    }


    // Bkav TienNVh : login Account
    public void onLoginAcoount() {
       
        String username = mUsername.getText().toString();
        String password = encryptPassword(mPassword.getText().toString());
        Log.d("Tiennvh", "onLoginAcoount: ");
        if (RequestToServer.isNetworkConnected(this)) {
      /*      Log.d(TAG, "onLoginAcoount: ");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", "x");
                jsonObject.put("password","x");
                String path ="loginaccount";
                RequestToServer.post(path, jsonObject, callback);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            File outputZipFile = new File(handleFile.PATH_ROOT+"/Android/demo.zip");
            File inputDir = new File( handleFile.PATH_ROOT+"/Android/data");
           CompressionFile.zipDirectory(inputDir, outputZipFile);

/*
            Intent intent = new Intent(getBaseContext(), HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);*/
        } else {
            Toast.makeText(getBaseContext(), "Not Connect Internet", Toast.LENGTH_SHORT).show();
        }
    }

    // Bkav TienNVh : login FB
    public void onLoginFacebook() {
        // ========================Bkav TienNVh :API FB===============================
        callbackManager = CallbackManager.Factory.create();
        loginFB.setReadPermissions(Arrays.asList(EMAIL, USER_POSTS));
        loginFB.setAuthType(AUTH_TYPE);
        // If using in a fragment
        //loginButton.setFragment(this);
        // Callback registration
        loginFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this, "Đăng nhập FB thành công", Toast.LENGTH_SHORT).show();
                getFbInfo();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, "ERROR : " + exception);
            }
        });
    }


    // Bkav TienNVh :
    public void onLoginGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    GoogleSignInAccount account;

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    public void updateUI(GoogleSignInAccount account) {
        if (account != null)
            Toast.makeText(MainActivity.this, "đã nhập thành công", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("TienNVh", "handleSignInResult: " + account.getIdToken());
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String id = account.getId();
            String name = account.getGivenName();
            String token = account.getIdToken();
            String date = dtf.format(now);
            Account addAccount = new Account(id, name, token, date);
            onCheckDB(addAccount);
            Log.i("LoginGG: " + date, "ID: " + id + "//name " + name + "token" + token);
            Toast.makeText(MainActivity.this, "Đăng nhập GG thành công ", Toast.LENGTH_SHORT).show();

        } catch (ApiException | IOException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TienNvh", "signInResult:failed code=" + e);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Bkav TienNVh : callback fb
        if (callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TienNVh", "onActivityResult: " + resultCode);
        // Bkav TienNVh : call back google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        Log.d("TienNVh", "onActivityResult: " + requestCode);
    }

    // Bkav TienNVh : getinfor Acoount FB
    private void getFbInfo() {
        if (AccessToken.getCurrentAccessToken() != null) {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onCompleted(final JSONObject me, GraphResponse response) {
                            if (me != null) {
                                // Bkav TienNVh : get token FB
                                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                LocalDateTime now = LocalDateTime.now();
                                String id = me.optString("id");
                                String name = me.optString("name");
                                String token = accessToken.getToken();
                                String date = dtf.format(now);
                                Log.i("LoginFB: " + date, "ID: " + id + "//name " + name + "token" + accessToken.getToken());
                                Account account = new Account(id, name, token, date);
                                try {
                                    onCheckDB(account);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    // Bkav TienNVh : encrypt Bcrypt password
    private String encryptPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    // Bkav TienNVh : decrypt Bcrypt token
    private boolean decryptToken(String hash, String tokent) {
        BCrypt.Result result = BCrypt.verifyer().verify(tokent.toCharArray(), hash);
        return result.verified;
    }


    // Bkav TienNVh : Check Database
    public void onCheckDB(Account account) throws IOException {
        onTransportActivity();
        //  displayAlertDialog();
    }

    // Bkav TienNVh : transport activity
    public void onTransportActivity() {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra("token", "This value one for ActivityTwo ");
        startActivityForResult(intent, 2405);
    }
}