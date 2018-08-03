package com.example.clair.welp;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.clair.welp.Objects.Note;
import com.github.barteksc.pdfviewer.PDFView;
import com.squareup.okhttp.ResponseBody;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class NoteViewer extends AppCompatActivity {


    PDFView pdfView;
    ImageView imageView;
    VideoView videoView;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
//    String url, fileType;
    URL Url;
    Note n;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_viewer);

        n=(Note)getIntent().getSerializableExtra("note");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(n.getNoteTitle());
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Set this to true if selecting "home" returns up by a single level in your UI rather than back to the top level or front page.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_white); // set a custom icon for the default home button

//        url = getIntent().getStringExtra("url");
//        fileType = getIntent().getStringExtra("fileType");
        pdfView = findViewById(R.id.pdfView);
        imageView=findViewById(R.id.imageView);
        videoView=findViewById(R.id.videoView);

        if(n.getFileType().equalsIgnoreCase("pdf")) {
            pdfView.setVisibility(View.VISIBLE);
           showPDF();
        }else if(n.getFileType().equalsIgnoreCase("img")){
         imageView.setVisibility(View.VISIBLE);
         showIMG();
        }
        else if (n.getFileType().equalsIgnoreCase("vid")){
            videoView.setVisibility(View.VISIBLE);
            showVideo();
        }
    }
    public void showPDF(){
        try {
            Url = new URL(n.getResourceURL());
            URLConnection conexion = Url.openConnection();
            conexion.connect();
            InputStream input = new BufferedInputStream(Url.openStream());
            pdfView.fromStream(input).load();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showIMG(){
        Glide.with(imageView.getContext())
                .load(n.getResourceURL())
                .into(imageView);
    }
    public void showVideo(){
        int position = 0;
        MediaController mediaControls;
        mediaControls = new MediaController(NoteViewer.this);
        try
        {

            // set the media controller in the VideoView
            videoView.setMediaController(mediaControls);

            // set the uri of the video to be played
            videoView.setVideoURI(Uri.parse(n.getResourceURL()));

        } catch (Exception e)
        {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoView.requestFocus();

        // we also set an setOnPreparedListener in order to know when the video
        // file is ready for playback

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){

            public void onPrepared(MediaPlayer mediaPlayer)
            {
                // if we have a position on savedInstanceState, the video
                // playback should start from here
                videoView.seekTo(position);


                if (position == 0)
                {
                    videoView.start();
                } else
                {
                    // if we come from a resumed activity, video playback will
                    // be paused
                    videoView.pause();
                }
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (videoView.isPlaying()){
            videoView.stopPlayback();
        }
        onBackPressed();
        return true;
    }
}
