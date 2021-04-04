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
import java.io.IOException;
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
    public  static final String TAG="TienNVh";
    public  static final String SHARED_PRE_TOKEN="com.android.backup.token";
    public  static String PATH_ROOT = Environment.getExternalStorageDirectory().toString();
    public static final String ID_CLIENT_GG="797362158064-h1cjks1abj7vqphiu6rc0429jsd3puet.apps.googleusercontent.com";
    public static final  String TOKENT_CLIENT = "tokent_client";
    public static final int MSG_LOGIN=1;
    Callback callback;
    Handler mHandler;
    String mJsonData;
    Button bt_login;
    SignInButton loginGG;//Google
    LoginButton loginFB;//Facebook
    EditText mUsername , mPassword;
    private SharedPreferences mPreferences;
    ArrayList<FileItem> mListAllFile= new ArrayList<>();
    private boolean mAutheAC = false;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (savedInstanceState != null) {
            String tokent = savedInstanceState.getString(TOKENT_CLIENT);
            if(account!=null){
               mAutheAC = decryptToken(tokent,"GG"+account.getId());
            }
        }

             setContentView(R.layout.activity_main);


        bt_login = findViewById(R.id.bt_loginAC);
        loginGG = findViewById(R.id.loginGG_button);
        loginGG.setSize(SignInButton.SIZE_STANDARD);
        loginFB = (LoginButton) findViewById(R.id.loginFB_button);
        mUsername= findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
    //========================Khoi tao============================================
        mPreferences = getSharedPreferences(SHARED_PRE_TOKEN, MODE_PRIVATE);
        // Bkav TienNVh : Cấp quyền
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    CODE_PERMISSION);
            return;
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
    //========================Login FB============================================
        loginGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginGoogle();
            }
        });
    //=======================Login GG=============================================
        loginFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginFacebook();
            }
        });

        mHandler =  new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case MSG_LOGIN:
                        try {
                            JSONObject Jobject = new JSONObject(mJsonData);
                            Intent intent = new Intent(getBaseContext(), HomePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);



                            startActivity(intent);
                            Toast.makeText(getBaseContext(), "Đăng nhập thành công :"+Jobject.getInt("id"),Toast.LENGTH_SHORT).show();
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
                if (response.isSuccessful()){
                    mJsonData = response.body().string();
                    mHandler.sendEmptyMessage(MSG_LOGIN);
                }
            }
        };


    }

    // Bkav TienNVh : callback Permisson
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CODE_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                Log.d("TienNVh", "onRequestPermissionsResult: OK");
                mListAllFile = loadFile(PATH_ROOT);
            } else {
                // User refused to grant permission.
                Log.d("TienNVh", "onRequestPermissionsResult: NOT");
                // Bkav TienNVh : Thoat app
                finish();
            }
        }
    }

    // Bkav TienNVh :Dialog
    public void displayAlertDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.choose_flie_backup, null);
        RecyclerView recyclerView = alertLayout.findViewById(R.id.recyleview_list_file);
        mListAllFile = loadFile(PATH_ROOT);
        /*AdapterListFile adapterListFile=new AdapterListFile(this, mListAllFile);
        recyclerView.setAdapter(adapterListFile);*/
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("< Choose File >");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Backup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // code for matching password

            }
        });
        ViewTreeObserver vto = alertLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    alertLayout.getViewTreeObserver()
                            .removeOnGlobalLayoutListener(this);
                } else {
                    alertLayout.getViewTreeObserver()
                            .removeGlobalOnLayoutListener(this);
                }
                handleCheckbox(alertLayout);
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }

    Button mSetTimeUpdate;
    CheckBox mCheckBox;
    // Bkav TienNVh : `TODO: sửa lại cách hiện thị
    private void handleCheckbox(View view){
        mCheckBox= view.findViewById(R.id.checkbox);
        mSetTimeUpdate = view.findViewById(R.id.set_time_update);
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetTimeUpdate.setVisibility(View.VISIBLE);
            }
        });
        if(mCheckBox.isSelected()){
            mSetTimeUpdate.setVisibility(View.VISIBLE);

        }else {
            mSetTimeUpdate.setVisibility(View.GONE);
        }
    }

    // Bkav TienNVh :Load File
    //TODO: Lọc các thư mục cần thiết để backup (ko up lên cả)
    private ArrayList<FileItem> loadFile(String path){
        ArrayList<FileItem> list= new ArrayList<>();
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files!=null) {
            for (int i = 0; i < files.length; i++) {
                list.add(new FileItem(path+"/"+files[i].getName(), 0));
                //listAllFile.add(new FileItem(list.get(i).getPath(), 3));
            }
            return  list;
        }
        else {
           // listAllFile.add(new FileItem(path, 0));
            return null;
        }
    }

    // Bkav TienNVh : login Account
    public  void  onLoginAcoount(){
        Toast.makeText(MainActivity.this, "Đăng nhập Account", Toast.LENGTH_SHORT).show();
        String username = mUsername.getText().toString();
        String password = encryptPassword(mPassword.getText().toString());
        if (RequestToServer.isNetworkConnected(this)){
            Log.d(TAG, "onLoginAcoount: ");
           /* JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", mUsername.getText().toString());
                jsonObject.put("password",mPassword.getText().toString());
                String path ="loginaccount";
                RequestToServer.post(path, jsonObject, callback);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SharedPreferences sharedPref =  getContext().getSharedPreferences(SHAREPREFENCE,  MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("id", ID);
            editor.putString("name", name);
            editor.putString("token", token);
            editor.putString("email", email);
            editor.putString("date_create", dateCreate);
            editor.commit();
            */
            Intent intent = new Intent(getBaseContext(), HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(), "Not Connect Internet", Toast.LENGTH_SHORT).show();
        }

        //new Lichterkette().execute(username,password);
    }

    // Bkav TienNVh : login FB
    public void onLoginFacebook(){
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
                Log.e(TAG,"ERROR : "+ exception);
            }
        });
    }

    // Bkav TienNVh :TODO xem nhu cau dung server nao
    //TODO: check kết nối lâu không được thì trả về no internet
    public void postRequest(String user , String pass ) throws IOException {
        String urlStr= "https://tiennvh.000webhostapp.com/authenticationAccount.php";
        String jsonBodyStr="username"+ URLEncoder.encode(user, "UTF-8")
                +"&password"+ URLEncoder.encode( pass,"UTF-8");
        URL url = new URL(urlStr);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
        dataOutputStream.writeBytes(jsonBodyStr);
        dataOutputStream.flush();
        dataOutputStream.close();
        Log.d("TienNVh", "postRequest: "+httpURLConnection.getResponseCode());
        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if(line.equals("401")){
                        Log.d("TienNVh", "postRequest: ERR ");
                    }
                    else{
                        Log.d("TienNVh", "Connect : "+ line);
                        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
                        preferencesEditor.putString(TOKENT_CLIENT, line);
                        preferencesEditor.apply();
                    }

                }
            }
        } else {
            Log.d("TienNVh", "No Internet");
            // ... do something with unsuccessful response
        }
    }

    // Bkav TienNVh :
    public void onLoginGoogle(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    //=========================
    class Lichterkette extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            try {
                postRequest(username, password);
            } catch (IOException e) {
                e.printStackTrace();
                return "loi ket noi";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //All your UI operation can be performed here
            System.out.println(s);
        }
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
            Log.d("TienNVh", "handleSignInResult: "+ account.getIdToken());
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String id =  account.getId();
            String name =account.getGivenName();
            String token =  account.getIdToken();
            String date = dtf.format(now);
            Account addAccount= new Account(id,name, token,date);
            onCheckDB(addAccount);
            Log.i("LoginGG: "+date, "ID: "+ id+"//name "+name+ "token"+token);
            Toast.makeText(MainActivity.this, "Đăng nhập GG thành công ", Toast.LENGTH_SHORT).show();

        } catch (ApiException | IOException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TienNvh", "signInResult:failed code=" + e );

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Bkav TienNVh : callback fb
        if(callbackManager!=null)
             callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TienNVh", "onActivityResult: "+ resultCode);
        // Bkav TienNVh : call back google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        Log.d("TienNVh", "onActivityResult: "+ requestCode);
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
                                String name =me.optString("name");
                                String token =  accessToken.getToken();
                                String date = dtf.format(now);
                                Log.i("LoginFB: "+date, "ID: "+ id+"//name "+name+ "token"+accessToken.getToken());
                                Account account= new Account(id,name, token,date);
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
    private  String encryptPassword(String password) {
        return  BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    // Bkav TienNVh : decrypt Bcrypt token
    private boolean decryptToken(String hash, String tokent){
        BCrypt.Result result= BCrypt.verifyer().verify(tokent.toCharArray(),hash);
        return result.verified;
    }



    // Bkav TienNVh : Check Database
    public void onCheckDB(Account account) throws IOException {
             onTransportActivity();
      //  displayAlertDialog();
    }

    // Bkav TienNVh : transport activity
    public void onTransportActivity(){
        Intent intent= new Intent(this,HomePage.class);
        intent.putExtra("token", "This value one for ActivityTwo ");
        startActivityForResult(intent,2405);
    }

/*    //==========API Google ==============================
    // Bkav TienNVh : dang xuat google
    public void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    }
                });
    }*/
     /*   StringBuilder sb=null;
        BufferedReader reader=null;
        String serverResponse=null;
        try {

            URL url = new URL("https://tiennvh.000webhostapp.com/test.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("uploaded_file", "");
            connection.connect();
            int statusCode = connection.getResponseCode();
            //Log.e("statusCode", "" + statusCode);
            if (statusCode == 200) {
                sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            }

            connection.disconnect();
            if (sb!=null)
                serverResponse=sb.toString();
            Log.d("TienNVh", "doInBackground: "+serverResponse);
        } catch (Exception e) {
            Log.d("TienNVh", "doInBackground2: "+e);
            e.printStackTrace();
        } finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("TienNVh", "onCheckDB: "+serverResponse);*/
    /*URL url =new URL("https://tiennvh.000webhostapp.com/test.php");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    String urlprocess="55";
    connection.setRequestMethod("POST");

    connection.setRequestProperty("test", "tiennvh");

    connection.setDoInput(true);
    DataOutputStream outputStream=new DataOutputStream(connection.getOutputStream());
    outputStream.writeBytes(urlprocess);
    outputStream.flush();
    outputStream.close();

    int reposecode = connection.getResponseCode();

        Log.d("TienNVh", "onCheckDB: "+ reposecode);*/



}
// Bkav TienNVh : get Key hash
      /*   findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



             // Bkav TienNVh : ĐĂng nhập bằng Database MYSQL
              String username = mUsername.getText().toString();
               String password = mPassword.getText().toString();
                new PostToServer( username.trim(),password.trim()).execute("https://tiennvh.000webhostapp.com/request.php");
            }
        });
    }*/
    /*    try {
            PackageInfo info = null;
            try {
                info = getPackageManager().getPackageInfo(
                        "com.android.backup",                  //Insert your own package name.
                        PackageManager.GET_SIGNATURES);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NoSuchAlgorithmException e) {

        }

    }
        */