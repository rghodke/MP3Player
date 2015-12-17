package com.example.admin.mp3player;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.Normalizer;

public class UploadActivity extends AppCompatActivity {

    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        showFileChooser();
    }


    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("Result code is" + " " + resultCode);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    uploadData(uri);
                }
                else if (resultCode == RESULT_CANCELED) {
                    Intent intent = new Intent(this, MP3List.class);
                    startActivity(intent);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    public void uploadData(Uri uri) {

        /*
        Progress Dialog Setup
         */
        progress = new ProgressDialog(this);
        progress.setMessage("Uploading to Server...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.show();

        final Context context = this;

        if (uri != null) {
            try {
                InputStream iStream = getContentResolver().openInputStream(uri);
                try {
                    final byte[] inputData = getBytes(iStream);

                    /*
                    NORMALIZE FILE NAME FOR PARSEFILE UPLOAD
                    */
                    String temp = uri.getLastPathSegment();
                    String normalized = Normalizer.normalize(temp, Normalizer.Form.NFD);
                    String result = normalized.replaceAll("[^A-Za-z0-9]", "");

                    /*
                    Get content type
                     */
                    ContentResolver cR = context.getContentResolver();
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String type = mime.getExtensionFromMimeType(cR.getType(uri));
                    System.out.println(type);

                    final ParseFile file = new ParseFile(result, inputData, "." + type);
                    System.out.println(file);
                    ParseObject fileUpload = new ParseObject("Files");
                    fileUpload.put("File", file);
                        file.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                progress.dismiss();
                                progress = null;
                                Intent intent = new Intent(context, MP3List.class);
                                startActivity(intent);
                            }
                        });

                        fileUpload.saveInBackground();


                } catch (Exception e) {}

            } catch (Exception e) {
                System.out.println(e);
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
}