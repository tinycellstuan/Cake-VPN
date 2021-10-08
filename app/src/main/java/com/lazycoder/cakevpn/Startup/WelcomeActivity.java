package com.cakevpn.vpn.Startup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;

import com.cake.vpn.Auth.Login;

import com.cakevpn.vpn.R;



public class WelcomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(WelcomeActivity.this, Login.class);
                startActivity(i);
                finish();
            }
        }, 4800);

    }
}
