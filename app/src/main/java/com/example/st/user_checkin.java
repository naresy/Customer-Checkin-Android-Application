package com.example.st;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.st.helper.DatabaseHandler;
import com.example.st.helper.Functions;
import com.example.st.helper.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class user_checkin extends AppCompatActivity {
    private static final String TAG = user_checkin.class.getSimpleName();
    EditText fullname,email,Password;
    TextView Already_have_account,Sinup;
    ProgressDialog pDialog;


    // check internet
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    RelativeLayout relativeLayout;
    private boolean internetConnected=true;

    private Activity activity = user_checkin.this;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_checkin);

        setTitle("Register");
        fullname = findViewById(R.id.register_user_name);
        email = findViewById(R.id.register_user_email);
        Password = findViewById(R.id.user_register_password);
        Already_have_account = findViewById(R.id.login_email_cancel);
        Sinup = findViewById(R.id.login_email);
        relativeLayout = findViewById(R.id.login_form);
        pDialog = new ProgressDialog(this);




        pDialog.setCancelable(false);
        setTitle("Register");
        Already_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(user_checkin.this, checkin_login.class));
            }
        });


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
        String email_value = getemail(this);
        if (email_value != null) {
            email.setText(email_value);
        }


    }






    static String getemail(Context context)
    {
        AccountManager accountManager=AccountManager.get(context);
        Account account=getaccount(accountManager);
        if (account==null)
        {
            return  null;
        }else {
            return account.name;
        }
    }
    private static Account getaccount(AccountManager accountManager)
    {
        Account[]accounts=accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length>0)
        {
            account=accounts[0];
        }
        else {
            account=null;
        }
        return  account;
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
        Sinup.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Hide Keyboard
                Functions.hideSoftKeyboard(user_checkin.this);

                String name = fullname.getText().toString().trim();
                String emailvalue = email.getText().toString().trim();
                String passwordvalue = Password.getText().toString().trim();

                // Check for empty data in the form

                if (!name.isEmpty() && !emailvalue.isEmpty() && !passwordvalue.isEmpty()) {
                    if (Functions.isValidEmailAddress(emailvalue)) {
                        if (passwordvalue.length()<10)
                        {
                            Password.setError("Enter 10 digit Number");

                        }

                        else {


                            registerUser(name, emailvalue, passwordvalue);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Email is not valid!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter your details!", Toast.LENGTH_LONG).show();
                }
            }

        });

        // Link to Register Screen

    }

    private void registerUser(final String name, final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.REGISTER_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Functions logout = new Functions();
                        logout.logoutUser(getApplicationContext());

                        Bundle b = new Bundle();
                        b.putString("email", email);
                        Intent i = new Intent(user_checkin.this, checkin_login.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtras(b);
                        startActivity(i);
                        pDialog.dismiss();
                        finish();

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("message");

                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage(), error);
                Toast.makeText(getApplicationContext(), "please check your status"+error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("phone", password);

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
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}

