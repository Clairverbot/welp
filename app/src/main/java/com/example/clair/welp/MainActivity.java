package com.example.clair.welp;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

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
    Firestore f;
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
                    //fiyahbase ui, for ref.
                    /*startActivityForResult(
                            AuthUI.getInstance()
                            .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                            .setProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .build(),
                                    RC_SIGN_IN);*/
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
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
                        break;
                    case R.id.action_profile:
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
        f=new Firestore(r);
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
                return true;
            case R.id.credits_menu:
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
        if(isFabOpen){
            switch (v.getId()){
                case R.id.fabImg:
                    //todo: image selector
                    i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.setType("image/*");
                    i.addCategory(Intent.CATEGORY_OPENABLE);

                    try {
                        startActivityForResult(
                                Intent.createChooser(i, "Select note"),
                                0);
                    } catch (android.content.ActivityNotFoundException ex) {
                        // Potentially direct the user to the Market with a Dialog

                    }
                    break;
                case R.id.fabPdf:
                    //todo: pdf selector
                    i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.setType("application/pdf");
                    i.addCategory(Intent.CATEGORY_OPENABLE);

                    try {
                        startActivityForResult(
                                Intent.createChooser(i, "Select note"),
                                0);
                    } catch (android.content.ActivityNotFoundException ex) {
                        // Potentially direct the user to the Market with a Dialog

                    }
                    break;
                case R.id.fabVid:
                    //todo: video selector;
                    i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.setType("video/*");
                    i.addCategory(Intent.CATEGORY_OPENABLE);

                    try {
                        startActivityForResult(
                                Intent.createChooser(i, "Select note"),
                                0);
                    } catch (android.content.ActivityNotFoundException ex) {
                        // Potentially direct the user to the Market with a Dialog

                    }
                    break;
            }
        }
    }
}

