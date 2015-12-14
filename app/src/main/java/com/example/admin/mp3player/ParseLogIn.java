package com.example.admin.mp3player;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.parse.Parse;

public class ParseLogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_log_in);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        Intent intent = new Intent(this, MP3List.class);
        startActivity(intent);
    }

}
