package com.example.st;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.st.helper.custom_toast;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class online_booking extends AppCompatActivity {
    EditText phone_number;
    String number_value;
    String HttpURL="https://sandythreading.com/backend/online_book.php";

    Button check_book;

    String finalResult ;
    HashMap<String,String> hashMap = new HashMap<>();
    String ParseResult ;
    String FinalJSonObject ;
    ProgressDialog progressDialog2;
    HttpParse httpParse = new HttpParse();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_booking);
        setTitle("Login");
        phone_number = findViewById(R.id.book_online);
        check_book = findViewById(R.id.check);
        progressDialog2=new ProgressDialog(this);


        check_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_from_edit();
                check_book(number_value);


            }
        });

    }

    private void get_from_edit() {

        number_value=phone_number.getText().toString();

    }

    private void check_book( String number_value) {

        class StudentRecordUpdateClass extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog2.setTitle("loading...");
                progressDialog2.show();
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);
                progressDialog2.dismiss();
                if ((httpResponseMsg.toString()).equals("found"))
                {



                    Intent intent=new Intent(online_booking.this,All_booking.class);
                    intent.putExtra("number",number_value);
                    startActivity(intent);
                }
                else{

                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.toast_root));

                    TextView text = layout.findViewById(R.id.toast_error);
                    text.setText(httpResponseMsg.toString());

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 40);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }


            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("phone",params[0]);
                finalResult = httpParse.postRequest(hashMap, HttpURL);

                return finalResult;
            }
        }

        StudentRecordUpdateClass studentRecordUpdateClass = new StudentRecordUpdateClass();

        studentRecordUpdateClass.execute(number_value);

    }


}



