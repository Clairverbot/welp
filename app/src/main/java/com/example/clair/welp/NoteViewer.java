package com.example.clair.welp;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;

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
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    String url, fileType;
    URL Url;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_viewer);
        //make actionBar logo as welp logo, todo :find better way to do this
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        url=getIntent().getStringExtra("url");
        fileType=getIntent().getStringExtra("fileType");
        pdfView=findViewById(R.id.pdfView);

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

//        try {
//            openRenderer(this);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


//    @TargetApi(21)
//    private void openRenderer(Context context) throws IOException {
//
//        // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
//        // the cache directory.
//        URL Url=new URL(url);
//        URLConnection conexion = Url.openConnection();
//        conexion.connect();
//        InputStream input = new BufferedInputStream(Url.openStream());
//        OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath());
//        final byte[] data = new byte[1024];
//        long total = 0;
//        int count;
//        while ((count = input.read(data)) != -1) {
//            total += count;
//            output.write(data, 0, count);
//        }
//
//        output.flush();
//        output.close();
//        input.close();
//
//        File file=new File(Environment.getExternalStorageDirectory().getPath());
//        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
//        // This is the PdfRenderer we use to render the PDF.
//        if (mFileDescriptor != null) {
//            mPdfRenderer = new PdfRenderer(mFileDescriptor);
//        }
//    }
}
