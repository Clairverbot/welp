package com.example.clair.welp;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
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

import com.example.clair.welp.Firebase.NoteFirestore;
import com.example.clair.welp.Objects.Note;
import com.example.clair.welp.OtherStuff.*;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //region firebase databse stuf
    private FirebaseDatabase fFirebaseDatabase;
    private DatabaseReference fDatabaseReference;
    private ChildEventListener fChildEventListener;
    NoteFirestore f;
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
                //TODO: create credits page one dayyy
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
        switch (requestCode) {
            case 0:
            case 1:
                case 2:
                if(resultCode==RESULT_OK){
                    Uri file=data.getData();
                    Intent i=new Intent(MainActivity.this,AddPostDetail.class);
                    i.putExtra("path",file);
                    Log.d("Ppath",file.toString());
                    i.putExtra("email", currentUser.getEmail());
                    i.putExtra("username",currentUser.getDisplayName());
                    startActivity(i);
                }
                break;
        }
    }
    //endregion
    }

