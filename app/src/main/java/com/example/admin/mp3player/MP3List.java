package com.example.admin.mp3player;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

public class MP3List extends AppCompatActivity {

    private ListView listView;
    private Button uploadButton;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.musicList);
        uploadButton = (Button)findViewById(R.id.uploadButton);
        getListFromParse();
    }

    public void getListFromParse(){
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Files");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    if(objects.isEmpty()){
                        arrayAdapter.add("Upload some files");
                    }

                    for (ParseObject x : objects) {

                        String url = x.getParseFile("File").getName();
                        String fileName = url.substring(url.lastIndexOf('-') + 1);
                        arrayAdapter.add(fileName);
                    }

                } else {
                    arrayAdapter.add("Nothing present");
                }
            }
        });

        listView.setAdapter(arrayAdapter);

        arrayAdapter.notifyDataSetChanged();
    }




    public void goUpload(View v){
        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);
    }



}
