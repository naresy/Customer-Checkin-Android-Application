package com.example.st;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

public class dashboard extends AppCompatActivity {
    String FinalHttpData = "";
    BufferedWriter bufferedWriter ;
    WebCallParse webCallParse = new WebCallParse();
    BufferedReader bufferedReader ;
    OutputStream outputStream ;
    StringBuilder stringBuilder = new StringBuilder();
    String Result ;
    GridView Subcategory_list;
    public static String ParseResult ;

    ProgressDialog pDialog;
    String HttpURL = "https://sandythreading.com/backend/all_queue.php";
    HashMap<String,String> ResultHash = new HashMap<>();
    URL url;
    ArrayAdapter<String> arrayAdapter ;
    String FinalJSonObject ;


    // check connection
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
    private Snackbar snackbar;
    LinearLayout relativeLayout;
    private boolean internetConnected=true;
    ProgressDialog progressDialog;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Subcategory_list = (GridView) findViewById(R.id.my_view);
        relativeLayout=findViewById(R.id.relative);
        progressDialog=new ProgressDialog(this);




        //Receiving the ListView Clicked item value send by previous activity.




        pDialog=new ProgressDialog(this);

        HttpWebCall();













    }
    // check connection
    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    @Override
    protected void onResume() {

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);

                HttpWebCall();
            }
        }, delay);
        super.onResume();
        registerInternetCheckReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();

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




    public void HttpWebCall(){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();


            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();


                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String.
                new dashboard.GetHttpResponse(dashboard.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {




                ParseResult = webCallParse.postRequest(ResultHash);





                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute();
    }

    public class WebCallParse {

        public String postRequest(HashMap<String, String> Data) {

            try {
                url = new URL(HttpURL);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(12000);

                httpURLConnection.setConnectTimeout(12000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, "UTF-8"));

                bufferedWriter.write(FinalDataParse(Data));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                if (httpURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(
                            new InputStreamReader(
                                    httpURLConnection.getInputStream()
                            )
                    );
                    FinalHttpData = bufferedReader.readLine();
                }
                else {
                    FinalHttpData = "Something Went Wrong";
                }
            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }

            return FinalHttpData;
        }

        public String FinalDataParse(HashMap<String, String> hashMap2) throws UnsupportedEncodingException {

            for(Map.Entry<String, String> map_entry : hashMap2.entrySet()){

                stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(map_entry.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(map_entry.getValue(), "UTF-8"));

            }

            Result = stringBuilder.toString();

            return Result ;
        }
    }

    private class GetHttpResponse extends AsyncTask<Void, Void, Void>
    {
        public Context context;
        List<product_data> subcategory_data;

        public GetHttpResponse(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... arg0)
        {
            try
            {
                if(FinalJSonObject != null)
                {
                    JSONArray jsonArray = null;

                    try {
                        jsonArray = new JSONArray(FinalJSonObject);

                        JSONObject jsonObject;
                        product_data subcategoryData ;
                        subcategory_data = new ArrayList<product_data>();

                        for(int i=0; i<jsonArray.length(); i++)
                        {
                            subcategoryData = new product_data();
                            jsonObject = jsonArray.getJSONObject(i);

                            subcategoryData.client_name=jsonObject.getString("c_name");
                            subcategoryData.service=jsonObject.getString("service");
                            subcategoryData.artist=jsonObject.getString("staff");
                            subcategoryData.time=jsonObject.getString("time");
                            subcategoryData.id=jsonObject.getString("id");


                            subcategory_data.add(subcategoryData);


                        }
                    }
                    catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                        progressDialog.dismiss();

                    }
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();

                progressDialog.dismiss();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)

        {

            int item=0;
            try {


                for (int i = 0; i<subcategory_data.size(); i++)
                {
                    item++;


                }
            } catch (NullPointerException ex)
            {


            }





            progressDialog.dismiss();


            Subcategory_list.setVisibility(View.VISIBLE);

            product_adapter adapter = new product_adapter(subcategory_data, context);
            Subcategory_list.setAdapter(adapter);

        }
    }



}
