package com.example.clair.welp;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
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

import com.bumptech.glide.Glide;
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
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    String url, fileType;
    URL Url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_viewer);
        //make actionBar logo as welp logo, todo :find better way to do this
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Set this to true if selecting "home" returns up by a single level in your UI rather than back to the top level or front page.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow_white); // set a custom icon for the default home button

        url = getIntent().getStringExtra("url");
        fileType = getIntent().getStringExtra("fileType");
        pdfView = findViewById(R.id.pdfView);
        imageView=findViewById(R.id.imageView);

        if(fileType.equalsIgnoreCase("pdf")) {
            pdfView.setVisibility(View.VISIBLE);
           showPDF();
        }else if(fileType.equalsIgnoreCase("img")){
         imageView.setVisibility(View.VISIBLE);
         showIMG();

        }
    }
    public void showPDF(){
        try {
            Url = new URL(url);
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
                .load(url)
                .into(imageView);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
