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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.musicList);
        uploadButton = (Button)findViewById(R.id.uploadButton);
        getListFromParse();
        uploadData();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject x : objects) {
                        if(x.get("applicantResumeFile") != null){


                            try {
                                System.out.println(new String(x.getBytes("applicantResumeFile"), "UTF-8"));
                            }
                            catch (Exception e2){}
                            /*
                            applicantResume.getDataInBackground(new GetDataCallback() {
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        String doc2 = "a";

                                        try{
                                            doc2 = new String(data, "UTF-8");
                                        }
                                        catch (Exception e2){}

                                        System.out.println(doc2);
                                    } else {
                                        // something went wrong
                                    }

                                }
                            });
                            */
                        }



                    }
                    ;
                } else {
                }
            }
        });




    }

    public void uploadData() {
        Intent intent = getIntent();
        Bundle temp = intent.getExtras();
        Uri uri = null;
        byte[] inputData = null;
        if (temp != null) {
            uri = (Uri) temp.get("PATH");
        }
        if (uri != null) {
            try {
                InputStream iStream = getContentResolver().openInputStream(uri);
                try {
                    inputData = getBytes(iStream);
                    final ParseFile file = new ParseFile(uri.getPath(), "HELLO IS THIS BEING TRANSMITTED".getBytes());
                    file.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {



                            ParseObject gameScore = new ParseObject("GameScore");
                            gameScore.put("score", 1337);
                            gameScore.put("playerName", "Sean Plott");
                            gameScore.put("playerName22", "Sean Plott");
                            gameScore.put("cheatMode", false);
                            gameScore.put("applicantResumeFile", "HELLO IS THIS BEING TRANSMITTED".getBytes());
                            gameScore.saveInBackground();
                        }
                    }, new ProgressCallback() {
                        public void done(Integer percentDone) {
                            System.out.println("percentDone");
                            System.out.println("percentDone");
                            System.out.println("percentDone");
                            System.out.println("percentDone");
                            System.out.println("percentDone");
                            System.out.println("percentDone");
                            System.out.println("percentDone");System.out.println("percentDone");
                            System.out.println("percentDone");


                        }
                    });


                } catch (Exception e) {
                }

            }
            catch(Exception e){
                System.out.println("Bug");
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public void getListFromParse(){
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("TestObject");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject x : objects) {
                        arrayAdapter.add(x.getString("foo"));
                    }
                    ;
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
