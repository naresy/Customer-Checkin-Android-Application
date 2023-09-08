package com.example.st;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.st.helper.Functions;
import com.example.st.helper.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class detail_booing extends AppCompatActivity {
    TextView service,staff,rewards;
    Button check,cancel;

    ProgressDialog pDialog;
    String appointment,user,ser,st,id,counter,f_name,L_name,common_name,FinalJSonObject,ParseResult,fetching_url="https://sandythreading.com/backend/rewards.php";
    AlertDialog.Builder builder;

    // insert check
    HashMap<String,String> ResultHash = new HashMap<>();
    HttpParse httpParse = new HttpParse();



    public String result1,postURL="https://sandythreading.com/backend/checkin.php",booking_id;
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    LinearLayout relativeLayout;
    private boolean internetConnected=true;
    ProgressDialog progressDialog;
    private SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_booing);
        service=findViewById(R.id.ordername);
        staff=findViewById(R.id.address);
        check=findViewById(R.id.checkin);
        cancel=findViewById(R.id.cancel);
        builder=new AlertDialog.Builder(this);
        ser= getIntent().getStringExtra("Servicename");
        st=getIntent().getStringExtra("staff");
        appointment=getIntent().getStringExtra("aptnumber");
        user=getIntent().getStringExtra("user_id");
        f_name=getIntent().getStringExtra("fname");
        L_name=getIntent().getStringExtra("lname");
        booking_id=getIntent().getStringExtra("ID");
        Toast.makeText(detail_booing.this,booking_id,Toast.LENGTH_LONG).show();
        rewards=findViewById(R.id.counter);
        common_name=f_name+L_name;
        service.setText(ser);
        staff.setText(st);
        relativeLayout=findViewById(R.id.relative);
        progressDialog=new ProgressDialog(this);

        pDialog=new ProgressDialog(this);
        session=new SessionManager((getApplicationContext()));

        HttpWebCall(user);


        //add data to database

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Do you want to cancel ?")
                .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(detail_booing.this,online_booking.class));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
 dialogInterface.dismiss();
                            }
                        }).show();


            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_check();
            }
        });



    }

    private void upload_check() {
        progressDialog.setTitle("Loading...");
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, postURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Success = jsonObject.getString("success");
                            if (Success.equals("1")) {
                                Toast.makeText(detail_booing.this, " Congratulation you are on the queue ,Please follow the display queue \n", Toast.LENGTH_LONG).show();
                                session.setLogin(false,0);
                                update_book();
                                Functions logout = new Functions();
                                logout.logoutUser(getApplicationContext());
                                Intent intent = new Intent(detail_booing.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(detail_booing.this, "you have some problem please ask for help to the staff", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(detail_booing.this, "Error!" + e, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(detail_booing.this, "Error!" + error, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user);
                params.put("service", ser);
                params.put("staff", st);
                params.put("aptnumber", appointment);
                params.put("name",common_name);
                params.put("book_id",booking_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
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
    // counter check



    private void HttpWebCall(final String PreviousListViewClickedItem){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                pDialog.setTitle("loading...");
                pDialog.show();
                pDialog.setCanceledOnTouchOutside(false);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {


                super.onPostExecute(httpResponseMsg);


                pDialog.dismiss();

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String to GetHttpResponse Method.
                new detail_booing.GetHttpResponse(detail_booing.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {
                ResultHash.put("user_id",params[0]);
                ParseResult = httpParse.postRequest(ResultHash, fetching_url);

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(PreviousListViewClickedItem);
    }


    // Parsing Complete JSON Object.
    @SuppressLint("StaticFieldLeak")
    private class GetHttpResponse extends AsyncTask<Void, Void, Void> {
        public Context context;

        public GetHttpResponse(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                if (FinalJSonObject != null) {
                    JSONArray jsonArray = null;

                    try {
                        jsonArray = new JSONArray(FinalJSonObject);

                        JSONObject jsonObject;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);

                            counter = jsonObject.getString("counter");


                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

rewards.setText(counter);




            }





        }

        private  void update_book()
        {

        }



    }

