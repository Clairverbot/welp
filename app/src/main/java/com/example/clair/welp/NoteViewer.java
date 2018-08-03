package com.example.clair.welp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.clair.welp.Objects.Note;
import com.example.clair.welp.Objects.Notebook;
import com.firebase.client.annotations.NotNull;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class NoteViewer extends AppCompatActivity implements View.OnClickListener{


    PDFView pdfView;
    ImageView imageView;
    VideoView videoView;
    ImageButton ib_Upvote, ib_Downvote,ib_Comment;
    TextView tv_Upvote,tv_Downvote,tv_Comment;
    LinearLayout llBookmark;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
//    String url, fileType;
    URL Url;
    Note n;
    String userEmail,notebookName, notebookDocumentID;
    List<String> notebookNotes;
    FirebaseAuth fFirebaseAuth;
    FirebaseUser mFirebaseUser;
    ArrayList<Notebook> notebooks;
    AlertDialog alert;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_viewer);


        fFirebaseAuth=FirebaseAuth.getInstance();
        mFirebaseUser = fFirebaseAuth.getCurrentUser();
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
        ib_Upvote = findViewById(R.id.ib_Upvote);
        ib_Downvote = findViewById(R.id.ib_Downvote);
        ib_Comment = findViewById(R.id.ib_Comment);
        llBookmark = findViewById(R.id.llBookmark);
        tv_Upvote=findViewById(R.id.tv_Upvote);
        tv_Downvote=findViewById(R.id.tv_Downvote);
        tv_Comment=findViewById(R.id.tv_Comment);
        ib_Comment.setOnClickListener(this);
        ib_Downvote.setOnClickListener(this);
        ib_Upvote.setOnClickListener(this);
        llBookmark.setOnClickListener(this);

        tv_Comment.setText(n.getComments() + "");

        //Set colours of upvote/downvote
        String upvote, downvote;
        Activity main = new MainActivity();
        ib_Upvote.setColorFilter(ContextCompat.getColor(NoteViewer.this, R.color.post_Grey));
        if (n.getUpvote() == null) {
            upvote = 0 + "";
        } else {
            upvote = (n.getUpvote()).size() + "";
            if ((n.getUpvote()).containsKey(mFirebaseUser.getEmail())) {
                ib_Upvote.setColorFilter(ContextCompat.getColor(NoteViewer.this, R.color.profile_LightBlue));
            }
        }

        ib_Downvote.setColorFilter(ContextCompat.getColor(NoteViewer.this, R.color.post_Grey));
        if (n.getDownvote() == null) {
            downvote = 0 + "";
        } else {
            downvote = (n.getDownvote()).size() + "";
            if ((n.getDownvote()).containsKey(mFirebaseUser.getEmail())) {
                ib_Downvote.setColorFilter(ContextCompat.getColor(NoteViewer.this, R.color.profile_LightBlue));
            }
        }

        //Set text of upvote/downvote
        tv_Upvote.setText((upvote));
        tv_Downvote.setText(downvote);



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

    @Override
    public void onClick(View v) {
        String docID=n.getDocumentID();
        switch (v.getId()){
            case R.id.llBookmark:
                openAddToOrCreateDialog(docID);
                break;
            case R.id.ib_Upvote:
                checkUpvotesOrDownvotes("Upvotes", n, ib_Upvote, tv_Upvote, ib_Downvote, tv_Downvote);
                break;
            case R.id.ib_Downvote:
                checkUpvotesOrDownvotes("Downvotes", n, ib_Upvote, tv_Upvote, ib_Downvote, tv_Downvote);
                break;
            case R.id.ib_Comment:
                goToComments(docID, Integer.parseInt(tv_Comment.getText().toString()));
                break;
                default:
                    break;

        }
    }
    //region createNotebook
    //VERY LONG METHOD TO CREATE NOTEBOOK/ADD TO EXISTING NOTEBOOK DIALOG
    private void openAddToOrCreateDialog(String noteDocumentID) {
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.d("TAG", "NOTEBOOK: email " + userEmail);
        notebooks = new ArrayList<Notebook>();
        List<String> notebookNames = new ArrayList<String>();

        FirebaseFirestore.getInstance().collection("Notebooks").whereEqualTo("Email", userEmail).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            Log.d("TAG", "NOTEBOOK: task works " + userEmail);
                            for (DocumentSnapshot document : task.getResult()) {
                                notebookName = document.getString("NotebookName");
                                notebookNotes = (List<String>) document.get("NotebookNotes");
                                notebookDocumentID = document.getId();

                                Notebook n = new Notebook(userEmail, notebookName, notebookNotes, notebookDocumentID);
                                notebooks.add(n);

                                notebookNames.add(notebookName);

                                Log.d("TAG", "NOTEBOOK: document " + notebookName);
                            }

                            //SHOW Add to/Create Notebook Dialog
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(NoteViewer.this);
                            LayoutInflater inflater = LayoutInflater.from(NoteViewer.this);
                            View convertView = (View) inflater.inflate(R.layout.dialog_create_addto_notebook, null);
                            alertDialog.setView(convertView);
                            ConstraintLayout cl = (ConstraintLayout) convertView.findViewById(R.id.clCreateNotebook);
                            ListView lv = (ListView) convertView.findViewById(R.id.lvNotebook);
                            if (notebookNames == null) {
                                lv.setVisibility(View.GONE);

                            } else {
                                if (notebookNames.size() == 0) {
                                    lv.setVisibility(View.GONE);
                                } else {
                                    //Populate Listview if user got notebooks
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(NoteViewer.this, R.layout.currentuser_notebook_item, R.id.tvNotebookName, notebookNames);
                                    adapter.notifyDataSetChanged();
                                    lv.setAdapter(adapter);
                                    Utility.setListViewHeightBasedOnChildren(lv); //SET MAX HEIGHT OF LISTVIEW TO 6 LV ITEMS
                                }
                            }
                            alertDialog.setCancelable(true);
                            alert = alertDialog.show();


                            //CLICK Add to Existing Notebook
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Notebook notebook = notebooks.get(position);
                                    String notebookName = notebook.getNotebookName();
                                    String notebookDocID = notebook.getNotebookDocumentID();
                                    List<String> notesList = notebook.getNotebookNotes();
                                    notesList.add(noteDocumentID);
                                    FirebaseFirestore.getInstance()
                                            .collection("Notebooks")
                                            .document(notebookDocID)
                                            .update("NotebookNotes", notesList);
                                    Toast.makeText(NoteViewer.this, "Added to " + notebookName + "!",
                                            Toast.LENGTH_SHORT).show();
                                    alert.dismiss();
                                }
                            });

                            //CLICK Create Notebook
                            cl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.dismiss();
                                    //OPEN create notebook dialog
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(NoteViewer.this, R.style.AppCompatAlertDialogStyle);
                                    LayoutInflater inflater = LayoutInflater.from(NoteViewer.this);
                                    View convertView = (View) inflater.inflate(R.layout.dialog_create_notebook, null);
                                    EditText etNotebookName = (EditText) convertView.findViewById(R.id.et_NotebookName);
                                    alertDialog.setView(convertView)
                                            .setTitle("Create Notebook")
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String notebookName = "";
                                                    notebookName = etNotebookName.getText().toString();

                                                    if (notebookName.equals("")) {
                                                        Toast.makeText(NoteViewer.this, "Please enter a notebook name",
                                                                Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        Boolean notebookNameExists = false;
                                                        for (String nbName : notebookNames) {
                                                            if (nbName.equalsIgnoreCase(notebookName)) {
                                                                notebookNameExists = true;
                                                            }
                                                        }
                                                        if (notebookNameExists) {
                                                            Toast.makeText(NoteViewer.this, "You already have a notebook named " + notebookName,
                                                                    Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            List<String> Notes = new ArrayList<String>();
                                                            Notes.add(noteDocumentID);
                                                            Map<String, Object> notebookas = new HashMap<>();
                                                            notebookas.put("Email", userEmail);
                                                            notebookas.put("NotebookName", notebookName);
                                                            notebookas.put("NotebookNotes", Notes);
                                                            DocumentReference dr = FirebaseFirestore.getInstance().collection("Notebooks").document();
                                                            dr.set(notebookas).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d(TAG, "DocumentSnapshot added with ID: " + dr.getId());
                                                                    Toast.makeText(NoteViewer.this, "Notebook Created!",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NotNull Exception e) {
                                                                    Log.w(TAG, "Eroor adding document", e);
                                                                    Toast.makeText(NoteViewer.this, "Something went wrong when creating your notebook",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                        }

                                                    }
                                                }
                                            });


                                    alertDialog.setCancelable(true);
                                    AlertDialog createAlert = alertDialog.show();
                                    int textViewId = createAlert.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
                                    TextView tv = (TextView) createAlert.findViewById(textViewId);
                                    tv.setTextColor(NoteViewer.this.getResources().getColor(R.color.Dark_Grey));
                                }
                            });


                        } else {
                            //Show create notebook button only
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    //endregion

    //region upvote/downvote
    public void checkUpvotesOrDownvotes(String type, Note clickedNote, ImageButton buttonUpvote, TextView countUpvote, ImageButton buttonDownvote, TextView countDownvote) {
        String email = mFirebaseUser.getEmail();
        HashMap<String, Boolean> votes, otherVotes;
        String docId = clickedNote.getDocumentID();
        String downvote = (clickedNote.getDownvote()).size() + "";
        String upvote = (clickedNote.getUpvote()).size() + "";
        switch (type) {
            case "Upvotes":
                votes = clickedNote.getUpvote();
                otherVotes = clickedNote.getDownvote();
                if (votes == null) {
                    if (otherVotes.containsKey(email)) {
                        downvote = (otherVotes.size() - 1) + "";
                        buttonDownvote.setColorFilter(ContextCompat.getColor(NoteViewer.this, R.color.post_Grey));
                        otherVotes.remove(email);
                        updateUpvotesOrDownvotes("Downvotes", otherVotes, docId);
                    }
                    upvote = 1 + "";
                    buttonUpvote.setColorFilter(ContextCompat.getColor(NoteViewer.this, R.color.profile_LightBlue));
                    votes.put(email, true);
                } else {
                    if (votes.containsKey(email)) {
                        upvote = (votes.size() - 1) + "";
                        buttonUpvote.setColorFilter(ContextCompat.getColor(NoteViewer.this, R.color.post_Grey));
                        votes.remove(email);
                    } else {
                        if (otherVotes.containsKey(email)) {
                            downvote = (otherVotes.size() - 1) + "";
                            buttonDownvote.setColorFilter(ContextCompat.getColor(NoteViewer.this, R.color.post_Grey));
                            otherVotes.remove(email);
                            updateUpvotesOrDownvotes("Downvotes", otherVotes, docId);
                        }
                        upvote = (votes.size() + 1) + "";
                        buttonUpvote.setColorFilter(ContextCompat.getColor(NoteViewer.this, R.color.profile_LightBlue));
                        votes.put(email, true);
                    }
                }
                countDownvote.setText(downvote);
                countUpvote.setText(upvote);
                updateUpvotesOrDownvotes(type, votes, docId);
                break;

            case "Downvotes":
                votes = clickedNote.getDownvote();
                otherVotes = clickedNote.getUpvote();
                if (votes == null) {
                    if (otherVotes.containsKey(email)) {
                        upvote = (otherVotes.size() - 1) + "";
                        buttonUpvote.setColorFilter(ContextCompat.getColor(NoteViewer.this, R.color.post_Grey));
                        otherVotes.remove(email);
                        updateUpvotesOrDownvotes("Upvotes", otherVotes, docId);
                    }
                    downvote = 1 + "";
                    buttonDownvote.setColorFilter(ContextCompat.getColor(NoteViewer.this, R.color.profile_LightBlue));
                    votes.put(email, true);
                } else {
                    if (votes.containsKey(email)) {
                        downvote = (votes.size() - 1) + "";
                        buttonDownvote.setColorFilter(ContextCompat.getColor(NoteViewer.this, R.color.post_Grey));
                        votes.remove(email);
                    } else {
                        if (otherVotes.containsKey(email)) {
                            upvote = (otherVotes.size() - 1) + "";
                            buttonUpvote.setColorFilter(ContextCompat.getColor(NoteViewer.this, R.color.post_Grey));
                            otherVotes.remove(email);
                            updateUpvotesOrDownvotes("Upvotes", otherVotes, docId);
                        }
                        downvote = (votes.size() + 1) + "";
                        buttonDownvote.setColorFilter(ContextCompat.getColor(NoteViewer.this, R.color.profile_LightBlue));
                        votes.put(email, true);
                    }
                }
                countDownvote.setText(downvote);
                countUpvote.setText(upvote);
                updateUpvotesOrDownvotes(type, votes, docId);
                break;
        }
    }

    public void updateUpvotesOrDownvotes(String type, HashMap<String, Boolean> votes, String docId) {
        FirebaseFirestore.getInstance().collection("Notes").document(docId).update(type, votes)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "UPVOTED SUCCESS");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NotNull Exception e) {
                        Log.d(TAG, "UPVOTED FAILED");
                    }
                });
    }
    //endregion

    //region comments
    private void goToComments(String clickedNoteDocumentID, int num) {
        Intent i = new Intent(NoteViewer.this, CommentsActivity.class);
        i.putExtra("passedNoteID", clickedNoteDocumentID);
        i.putExtra("passedCommentsCount", num);
        startActivity(i);
    }
    //endregion
}
