package com.example.st;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.st.helper.DatabaseHandler;
import com.example.st.helper.Functions;
import com.example.st.helper.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class user_booking extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    Spinner service_list, staff_list;
    Button check_in, cancle_check_in;
    CheckBox staff_check_box;
    private SessionManager session;

    ArrayList<String> servicelist = new ArrayList<>();
    private HashMap<String,String> user = new HashMap<>();
    ArrayList<String> stafflist = new ArrayList<>();
    ArrayAdapter<String> serviceadapter;
    ArrayAdapter<String> staffadapter;
    RequestQueue requestQueue;


    AlertDialog.Builder builder;
    LinearLayout relativeLayout;
    private boolean internetConnected=true;
    ProgressDialog progressDialog;
   public static    String service_value,counter;
    public static    String staff_value,user_id,user_name,FinalJSonObject,ParseResult,fetching_url="https://sandythreading.com/backend/rewards.php";
    private DatabaseHandler db;
    ProgressDialog pDialog;
    HashMap<String,String> ResultHash = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    TextView rewards;


    // insert check


    private static final String URL_PRODUCTS = "https://sandythreading.com/backend/all_service.php", staff_url = "https://sandythreading.com/backend/all_staff.php",postURL="https://sandythreading.com/backend/checkin.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_booking);
        service_list = findViewById(R.id.service_menu);
        staff_list = findViewById(R.id.staff);
        check_in = findViewById(R.id.check_user);
        cancle_check_in = findViewById(R.id.cancel);
        staff_check_box = findViewById(R.id.staff_check);
        requestQueue = Volley.newRequestQueue(this);
        service_list.setOnItemSelectedListener(this);
        service_list.setOnItemSelectedListener(this);
        rewards=findViewById(R.id.counter);

        db=new DatabaseHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        user=db.getUserDetails();
        user_id=user.get("uid");
        user_name=user.get("name");

        pDialog=new ProgressDialog(this);


        if (staff_check_box.isChecked()) {
             String staff = "Who free first";
        }

        builder=new AlertDialog.Builder(this);
        relativeLayout=findViewById(R.id.relative);
        progressDialog=new ProgressDialog(this);


        load_staff();
        loadProducts();
        HttpWebCall(user_id);

        cancle_check_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Do you want to cancel ?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                session.setLogin(false,0);
                                // Launching the login activity
                                Functions logout = new Functions();
                                logout.logoutUser(getApplicationContext());
                                Intent intent = new Intent(user_booking.this, MainActivity.class);
                                startActivity(intent);
                                finish();

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

        check_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_data();
            }


        });

    }



    private void load_staff() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, staff_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray jsonArray = response.getJSONArray("staff");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String countryname = jsonObject.optString("fullname");
                        stafflist.add(countryname);
                        staffadapter = new ArrayAdapter<>(user_booking.this, android.R.layout.simple_list_item_1, stafflist);
                        staffadapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                        staff_list.setAdapter(staffadapter);
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);


    }

    private void loadProducts() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_PRODUCTS, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("naresh", response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("service");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String countryname = jsonObject.optString("ServiceName");
                        servicelist.add(countryname);
                        serviceadapter = new ArrayAdapter<>(user_booking.this, android.R.layout.simple_list_item_1, servicelist);
                        serviceadapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                        service_list.setAdapter(serviceadapter);
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        try {
            service_value=servicelist.get(position);
            staff_value=stafflist.get(position);



        } catch (IndexOutOfBoundsException ex)
        {
            ex.printStackTrace();
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    private void upload_data() {

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
                                Toast.makeText(user_booking.this, " Congratulation you are on the queue ,Please follow the display queue \n", Toast.LENGTH_LONG).show();
                                session.setLogin(false,0);
                                // Launching the login activity
                                Functions logout = new Functions();
                                logout.logoutUser(getApplicationContext());
                                Intent intent = new Intent(user_booking.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(user_booking.this, "you have some problem please ask for help to the staff", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(user_booking.this, "Error!" + e, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(user_booking.this, "Error!" + error, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("service", service_value);
                params.put("staff", staff_value);
                params.put("name", user_name);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

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
                    new user_booking.GetHttpResponse(user_booking.this).execute();

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

    @Override
    public void onBackPressed() {

    }
}

