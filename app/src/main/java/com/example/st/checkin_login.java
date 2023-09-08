package com.example.st;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.st.helper.DatabaseHandler;
import com.example.st.helper.Functions;
import com.example.st.helper.SessionManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class checkin_login extends AppCompatActivity {

    TextView Create_account_user;


    EditText input_email,input_password;
    Button sinIn ;
    private static final String TAG = checkin_login.class.getSimpleName();
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private ProgressDialog pDialog;
    private SessionManager session;
    private DatabaseHandler db;

    // check connection
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    private boolean internetConnected=true;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_login);

        Create_account_user=findViewById(R.id.Register_here);
        setTitle("Login");
        input_email=findViewById(R.id.register_user_email);

        sinIn=findViewById(R.id.login_email);
        relativeLayout=findViewById(R.id.login_form);


        Create_account_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(checkin_login.this,user_checkin.class));
            }
        });
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // create sqlite database
        db = new DatabaseHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        // check user is already logged in
        if (session.isLoggedIn()) {

            if (session.isroller()==1) {
                Intent i = new Intent(checkin_login.this, dashboard.class);
                startActivity(i);
                finish();
            }
            else {
                Intent i = new Intent(checkin_login.this, user_booking.class);
                startActivity(i);
                finish();
            }


        }



        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerInternetCheckReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
    /**
     *  Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, internetFilter);
    }

    /**
     *  Runtime Broadcast receiver inner class to capture internet connectivity events
     */
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = getConnectivityStatusString(context);
            setSnackbarMessage(status,false);
        }
    };

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {
        int conn = getConnectivityStatus(context);
        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled";
        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled";
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }
    private void setSnackbarMessage(String status,boolean showBar) {
        String internetStatus = "";
        if (status.equalsIgnoreCase("Wifi enabled") || status.equalsIgnoreCase("Mobile data enabled")) {
            internetStatus = "Internet Connected";
        } else {
            internetStatus = "Lost Internet Connection";
        }
        snackbar = Snackbar
                .make(relativeLayout, internetStatus, Snackbar.LENGTH_LONG)
                .setAction("X", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
        // Changing message text color
        snackbar.setActionTextColor(Color.WHITE);
        // Changing action button text color
        View sbView = snackbar.getView();

        if (internetStatus.equalsIgnoreCase("Lost Internet Connection")) {
            if (internetConnected) {
                snackbar.show();
                internetConnected = false;
            }
        } else {
            if (!internetConnected) {
                internetConnected = true;
                snackbar.show();
            }
        }
    }





    private void init() {
        // Login button Click Event
        sinIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Hide Keyboard
                Functions.hideSoftKeyboard(checkin_login.this);

                String email = input_email.getText().toString().trim();


                // Check for empty data in the form
                if (!email.isEmpty()) {

                        // login user
                        loginProcess(email);

                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(), "Please enter the credentials!", Toast.LENGTH_LONG).show();
                }
            }

        });



        // Forgot Password Dialog

    }


    private void loginProcess(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.LOGIN_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        JSONObject json_user = jObj.getJSONObject("user");

                        Functions logout = new Functions();
                        logout.logoutUser(getApplicationContext());

                        if (Integer.parseInt(json_user.getString("role"))==1)

                        {
                            db.addUser(json_user.getString(KEY_UID), json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_CREATED_AT));
                            Intent upanel = new Intent(checkin_login.this, dashboard.class);
                            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(upanel);
                            session.setLogin(true,1);
                            finish();
                        }
                        else {
                            db.addUser(json_user.getString(KEY_UID), json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json_user.getString(KEY_CREATED_AT));
                            Intent upanel = new Intent(checkin_login.this, user_booking.class);
                            upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(upanel);
                            session.setLogin(true,0);
                            finish();

                        }







                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);


                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}



