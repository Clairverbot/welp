package com.example.clair.welp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class ProfileActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private TabLayout appBarLayout;
    private ViewPager viewPager;


    public static final String EXTRA_NAME = "cheese_name";

    private FirebaseAuth mFirebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth.AuthStateListener fAuthStateListener;
    MagicalNames magicalNames = new MagicalNames();

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitleEnabled(true);
        collapsingToolbar.setTitle("ZhenRight");
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
                        String desc = task.getResult().getString(magicalNames.getUsers_Column_ProfileDescription());
                        if(desc != null){
                            TextView tvDesc = (TextView)findViewById(R.id.profile_desc);
                            tvDesc.setText(desc);
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

// User ID
//        mFirebaseUser.getUid();
// Email-ID
//        mFirebaseUser.getEmail();
//// User-Profile (if available)
//        mFirebaseUser.getPhotoUrl();







        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new FragmentCurrentUserPosts(), "Posts");
        adapter.AddFragment(new FragmentCurrentUserNotebooks(), "Notebooks");
        adapter.AddFragment(new FragmentCurrentUserFollowing(), "Following");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);




        //region bottom nav
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


}
