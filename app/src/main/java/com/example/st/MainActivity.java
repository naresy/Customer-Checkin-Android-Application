package com.example.st;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.st.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public Button new_checkin,direct_checkin,onlinebooking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);

  new_checkin = findViewById(R.id.new_checkin);
  direct_checkin=findViewById(R.id.already_member);
  onlinebooking=findViewById(R.id.booking);
  new_checkin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          startActivity(new Intent(MainActivity.this,user_checkin.class));
      }
  });

  direct_checkin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          startActivity(new Intent(MainActivity.this,checkin_login.class));
      }
  });
  onlinebooking.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
          startActivity(new Intent(MainActivity.this,online_booking.class));
      }
  });


    }

    @Override
    public void onBackPressed() {

    }
}