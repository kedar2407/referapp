package com.example.referandearn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class splashscreen extends AppCompatActivity {
    TextView welcome;



    Handler handler = new Handler ();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
          startActivity(new Intent(splashscreen.this, MainActivity.class));
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        handler.postDelayed(runnable,000);



    }
}
//scrcpy mirr