package com.example.clair.welp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.clair.welp.Firebase.MagicalNames;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

public class ProfileActivity_otheruser  extends AppCompatActivity {
    private TabLayout tabLayout;
    private TabLayout appBarLayout;
    private ViewPager viewPager;


    private FirebaseAuth mFirebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MagicalNames magicalNames = new MagicalNames();
    public static String passedEmail, passedUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_otheruser);

        //Toolbar settings
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Backbutton
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitleEnabled(true);
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.color_YouCantSeeME)); // transperent color = #00000000
        collapsingToolbar.setCollapsedTitleTextColor(Color.rgb(255, 255, 255)); //Color of your title

        passedEmail = getIntent().getExtras().getString("Email");
        passedUsername = getIntent().getExtras().getString("Username");

        db.collection("Users").document(passedEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String desc= "";
                    try {
                        desc = task.getResult().getString(magicalNames.getUsers_Column_ProfileDescription());

                    }catch (Exception e){
                        Log.d(TAG, "User has no description: ", task.getException());
                    }

                    TextView tvDesc = (TextView) findViewById(R.id.profile_desc);
                    if (desc != "" && desc != null) {
                        tvDesc.setText(desc);
                    } else {
                        tvDesc.setText(passedUsername + " has no description yet");
                    }

                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        TextView tvUsername = (TextView) findViewById(R.id.profile_username);
        tvUsername.setText(passedUsername);
        collapsingToolbar.setTitle(passedUsername);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new FragmentOtherUserPosts(), "Posts");
        adapter.AddFragment(new FragmentOtherUserFollowing(), "Following");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


    }


//    //region menu on top
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.sign_out_menu:
//                AuthUI.getInstance().signOut(this);
//                return true;
//            case R.id.credits_menu:
//                //TODO: create credits page one dayyy
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//UP BUTTON (BACK)
@Override
public boolean onOptionsItemSelected(MenuItem item) {
//    startActivity(new Intent(ProfileActivity_otheruser.this, MainActivity.class));
    this.finish();
    return super.onOptionsItemSelected(item);

}

    //BACK BUTTON (BOTTOM)
    @Override
    public void onBackPressed() {
//        startActivity(new Intent(ProfileActivity_otheruser.this, MainActivity.class));
        this.finish();
    }

}
