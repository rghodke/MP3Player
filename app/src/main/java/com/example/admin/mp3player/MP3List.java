package com.example.admin.mp3player;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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


    private boolean playPause = false;
    private MediaPlayer mediaPlayer;
    private boolean intialStage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.musicList);
        uploadButton = (Button)findViewById(R.id.uploadButton);
        getListFromParse();

        //Init Mp3Player
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    }

    public void getListFromParse(){
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Files");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    if(objects.isEmpty()){
                        arrayAdapter.add("Upload some files");
                    }

                    else{
                    for (ParseObject x : objects) {

                        String url = x.getParseFile("File").getName();
                        String fileName = url.substring(url.lastIndexOf('-') + 1);
                        arrayAdapter.add(fileName);
                    }
                        updateListView(arrayAdapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                startMP3(position, objects);
                            }
                        });
                }
                }
                else {
                    arrayAdapter.add("Nothing present");
                }
            }
        });

        updateListView(arrayAdapter);
    }

    private void updateListView(ArrayAdapter<String> arrayAdapter) {
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    private void startMP3(int position, List<ParseObject> objects) {
        if (!playPause) {
            if (intialStage)
                new Player()
                        .execute(objects.get(position).getParseFile("File").getUrl());
            else {
                if (!mediaPlayer.isPlaying())
                    mediaPlayer.start();
            }
            playPause = true;
        } else {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
            playPause = false;
        }
    }

    public void goUpload(View v){
        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);
    }


    class Player extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progress;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean prepared;
            try {

                mediaPlayer.setDataSource(params[0]);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        intialStage = true;
                        playPause=false;
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            } catch (Exception e){
                prepared = false;
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (progress.isShowing()) {
                progress.cancel();
            }
            mediaPlayer.start();
            intialStage = false;
        }

        public Player() {
            progress = new ProgressDialog(MP3List.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progress.setMessage("Buffering...");
            this.progress.show();

        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


}
