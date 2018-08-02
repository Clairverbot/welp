package com.example.clair.welp;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.clair.welp.Firebase.MagicalNames;
import com.example.clair.welp.Firebase.NoteFirestore;
import com.example.clair.welp.Objects.Note;
import com.example.clair.welp.OtherStuff.*;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //region firebase databse stuf
    private FirebaseDatabase fFirebaseDatabase;
    private DatabaseReference fDatabaseReference;
    private ChildEventListener fChildEventListener;
    NoteFirestore f;
    //storage
    private StorageReference storageReference;
    //endregion

    //region firebase auth stuff
    private FirebaseAuth fFirebaseAuth;
    private FirebaseAuth.AuthStateListener fAuthStateListener;
    //endregion

    //region recycler view stuf
    RecyclerView rvNote;
    NoteAdapter nAdapter;
    RecyclerView.LayoutManager rLayoutManager;
    Note n=new Note();
    //endregion

    BottomNavigationView bottomNavigationView;
    boolean isFabOpen=false;
    FloatingActionButton fab,fabPdf,fabImg,fabVid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //make actionBar logo as welp logo, todo :find better way to do this
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //region auth
        fFirebaseAuth=FirebaseAuth.getInstance();
        fAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null){
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        //endregion

        fFirebaseDatabase=FirebaseDatabase.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();

        //region bottom nav
        fab = findViewById(R.id.fabUpload);
        fabPdf = findViewById(R.id.fabPdf);
        fabImg = findViewById(R.id.fabImg);
        fabVid = findViewById(R.id.fabVid);
        fabPdf.setOnClickListener(this);
        fabImg.setOnClickListener(this);
        fabVid.setOnClickListener(this);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo: make upload
                if(!isFabOpen){
                    showFabMenu();
                }
                else {
                    closeFabMenu();
                }
            }
        });


        bottomNavigationView=findViewById(R.id.bottomNav);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
                        break;
                    case R.id.action_chat:
                        break;
                    case R.id.action_upload:
                        break;
                    case R.id.action_noti:
                        startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
                        finish();
                        break;
                    case R.id.action_profile:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        finish();
                        break;
                }
                return true;
            }
        });
        //endregion


        //region recyclerView adapter
        rvNote=findViewById(R.id.rvNote);
        rLayoutManager=new LinearLayoutManager(this);
        rvNote.setLayoutManager(rLayoutManager);
        nAdapter=new NoteAdapter(this);
        //nAdapter.addAllItems(FakeDataGenerator());
        rvNote.setAdapter(nAdapter);
        //endregion
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity r=this;
        //ProgressBar spinner = (ProgressBar) findViewById(R.id.progressBar);
        f=new NoteFirestore(r);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(fAuthStateListener!=null){
            fFirebaseAuth.removeAuthStateListener(fAuthStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fFirebaseAuth.addAuthStateListener(fAuthStateListener);
    }


    //region menu on top
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                finish();
                return true;
            case R.id.filter_menu:
                startActivity(new Intent(MainActivity.this, SearchFilterActivity.class));
                return true;
            case R.id.search_menu:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                return true;
            case R.id.about_menu:
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }


//endregion


    public void UpdateList(List<Note> n){
        nAdapter.deleteEverything();
        nAdapter.addAllItems(n);
    }


//region fab
    public void gooeyFab(){
        ObjectAnimator anim = ObjectAnimator.ofFloat(fab, "scaleY", R.animator.gooey_path_anim);
        anim.setDuration(2000);                  // Duration in milliseconds
        anim.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return 0;
            }
        });  // E.g. Linear, Accelerate, Decelerate
        anim.start();
    }
    private void showFabMenu(){
        isFabOpen=true;
        gooeyFab();
        fabPdf.animate().translationY(-getResources().getDimension(R.dimen.fab));
        fabPdf.animate().translationX(-getResources().getDimension(R.dimen.fabMargin));
        fabImg.animate().translationY(-getResources().getDimension(R.dimen.fabCenter));
        fabVid.animate().translationY(-getResources().getDimension(R.dimen.fab));
        fabVid.animate().translationX(getResources().getDimension(R.dimen.fabMargin));

    }

    private void closeFabMenu(){
        isFabOpen=false;
        fabPdf.animate().translationY(0);
        fabPdf.animate().translationX(0);
        fabImg.animate().translationY(0);
        fabImg.animate().translationX(0);
        fabVid.animate().translationY(0);
        fabVid.animate().translationX(0);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        int requestCode=0;
        if(isFabOpen){
            i = new Intent(Intent.ACTION_GET_CONTENT);
            switch (v.getId()){
                case R.id.fabImg:
                    i.setType("image/*");
                    requestCode=0;
                    break;
                case R.id.fabPdf:
                    i.setType("application/pdf");
                    requestCode=1;
                    break;
                case R.id.fabVid:
                    i.setType("video/*");
                    requestCode=2;
                    break;
            }
            i.addCategory(Intent.CATEGORY_OPENABLE);

            try {
                startActivityForResult(
                        Intent.createChooser(i, "Select note"),
                        requestCode);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog

            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        FirebaseUser currentUser = fFirebaseAuth.getCurrentUser();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.show();

                if(resultCode==RESULT_OK){
                    Uri file=data.getData();

                    StorageReference filepath=storageReference.child("NoteResource").child(file.getLastPathSegment());

                    filepath.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(MainActivity.this,"Upload Done",Toast.LENGTH_LONG).show()
                            progressDialog.dismiss();

                            Intent i=new Intent(MainActivity.this,AddPostDetail.class);
                            switch (requestCode){
                                case 0:
                                    i.putExtra("fileType","img");
                                    break;
                                case 1:
                                    i.putExtra("fileType","pdf");
                                    break;
                                case 2:
                                    i.putExtra("fileType","vid");
                                    break;
                            }
                            i.putExtra("path",taskSnapshot.getDownloadUrl().toString());
                            i.putExtra("email", currentUser.getEmail());
                            i.putExtra("username",currentUser.getDisplayName());
                            startActivity(i);
                        }

                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Loaded " + ((int) progress) + "%...");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
                        }
                    });
        }
    }
    //endregion
    }

