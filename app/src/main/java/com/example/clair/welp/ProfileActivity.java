package com.example.clair.welp;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.clair.welp.Firebase.MagicalNames;
import com.example.clair.welp.OtherStuff.BottomNavigationViewHelper;
import com.example.clair.welp.Objects.Note;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private TabLayout tabLayout;
    private TabLayout appBarLayout;
    private ViewPager viewPager;


    private FirebaseAuth mFirebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MagicalNames magicalNames = new MagicalNames();

    boolean isFabOpen=false;
    FloatingActionButton fab,fabPdf,fabImg,fabVid;
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Toolbar settings
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitleEnabled(true);
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.color_YouCantSeeME)); // transperent color = #00000000
        collapsingToolbar.setCollapsedTitleTextColor(Color.rgb(255, 255, 255)); //Color of your title



        //region auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser.getEmail() != null){
            String userEmail = mFirebaseUser.getEmail();


            db.collection("Users").document(userEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String username = task.getResult().getString(magicalNames.getUsers_Column_Username());
                        String desc = "";
                        try{
                            desc = task.getResult().getString(magicalNames.getUsers_Column_ProfileDescription());
                        }catch (Exception e){

                        }

                        TextView tvDesc = (TextView)findViewById(R.id.profile_desc);
                        if(desc != null ){
                            if (!desc.equals("")){
                                tvDesc.setText(desc);
                            } else{
                                tvDesc.setText("You have no description yet");
                            }

                        } else{
                            tvDesc.setText("You have no description yet");
                        }

                        TextView tvUsername = (TextView)findViewById(R.id.profile_username);
                        tvUsername.setText(username);
                        collapsingToolbar.setTitle(username);
                    }
                    else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }


                }
            });



        } else{
            TextView tvUsername = (TextView)findViewById(R.id.profile_username);
            tvUsername.setText("ZhenRight");
            collapsingToolbar.setTitle("ZhenRight");
            TextView tvDesc = (TextView)findViewById(R.id.profile_desc);
            tvDesc.setText("You have no description yet");
        }



        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new FragmentCurrentUserPosts(), "Posts");
        adapter.AddFragment(new FragmentCurrentUserNotebooks(), "Notebooks");
        adapter.AddFragment(new FragmentCurrentUserFollowing(), "Following");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        //set current tab
        int intentFragment = getIntent().getExtras().getInt("frgToLoad");

        switch (intentFragment) {
            case 1:
                viewPager.setCurrentItem(1); //notebooks tab
                break;
            case 2:
                viewPager.setCurrentItem(2); //following tab
                break;

                default: //post tab


        }


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

        BottomNavigationView bottomNavigationView;
        bottomNavigationView=findViewById(R.id.bottomNav);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
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

//
//        loadBackdrop();
    }

//    private void loadBackdrop() {
//        final ImageView imageView = findViewById(R.id.backdrop);
//


//    }




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

        switch (requestCode) {
            case 0:
            case 1:
            case 2:
                if(resultCode==RESULT_OK){
                    File file=new File(data.getData().toString());
                    String path=file.getAbsolutePath();
                    Log.d("PATH:",path);
                    Intent i=new Intent(ProfileActivity.this,AddPostDetail.class);
                    i.putExtra("path",path);
                    i.putExtra("email",mFirebaseAuth.getCurrentUser().getEmail());
                    startActivity(i);
                }
                break;
        }
    }
    //endregion
}
