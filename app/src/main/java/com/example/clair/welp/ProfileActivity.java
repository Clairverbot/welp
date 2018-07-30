package com.example.clair.welp;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.clair.welp.Firebase.MagicalNames;
import com.example.clair.welp.OtherStuff.BottomNavigationViewHelper;
import com.example.clair.welp.Objects.Note;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private TabLayout appBarLayout;
    private ViewPager viewPager;
    Boolean hasDescription = false;
    FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MagicalNames magicalNames = new MagicalNames();

    boolean isFabOpen = false;
    FloatingActionButton fab, fabPdf, fabImg, fabVid;
    private FirebaseAuth fFirebaseAuth;
    private FirebaseAuth.AuthStateListener fAuthStateListener;
    AlertDialog alert;


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
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser.getEmail() != null) {
            String userEmail = mFirebaseUser.getEmail();


            db.collection("Users").document(userEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        String username = task.getResult().getString(magicalNames.getUsers_Column_Username());
                        String desc = "";
                        try {
                            desc = task.getResult().getString(magicalNames.getUsers_Column_ProfileDescription());
                        } catch (Exception e) {

                        }

                        TextView tvDesc = (TextView) findViewById(R.id.profile_desc);
                        if (desc != null) {
                            if (!desc.equals("")) {
                                tvDesc.setText(desc);
                                hasDescription = true;
                            } else {
                                tvDesc.setText("You have no description yet");
                                hasDescription = false;
                            }

                        } else {
                            tvDesc.setText("You have no description yet");
                            hasDescription = false;
                        }

                        TextView tvUsername = (TextView) findViewById(R.id.profile_username);
                        tvUsername.setText(username);
                        collapsingToolbar.setTitle(username);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }


                }
            });


        } else {
            TextView tvUsername = (TextView) findViewById(R.id.profile_username);
            tvUsername.setText("ZhenRight");
            collapsingToolbar.setTitle("ZhenRight");
            TextView tvDesc = (TextView) findViewById(R.id.profile_desc);
            tvDesc.setText("You have no description yet");
            hasDescription = false;
        }


        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new FragmentCurrentUserPosts(), "Posts");
        adapter.AddFragment(new FragmentCurrentUserNotebooks(), "Notebooks");
        adapter.AddFragment(new FragmentCurrentUserFollowing(), "Following");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        //region auth
        fFirebaseAuth = FirebaseAuth.getInstance();
        fAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(ProfileActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        //endregion
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
                if (!isFabOpen) {
                    showFabMenu();
                } else {
                    closeFabMenu();
                }
            }
        });

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = findViewById(R.id.bottomNav);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                        finish();
                        break;
                    case R.id.action_chat:
                        break;
                    case R.id.action_upload:
                        break;
                    case R.id.action_noti:
                        startActivity(new Intent(ProfileActivity.this, NotificationsActivity.class));
                        finish();
                        break;
                    case R.id.action_profile:
                        break;
                }
                return true;
            }
        });
    }


    //region menu on top
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.editprofile_menu:
                createEditProfileDialog();
                return true;
            case R.id.about_menu:
                startActivity(new Intent(ProfileActivity.this, AboutUsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void createEditProfileDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View convertView = (View) inflater.inflate(R.layout.dialog_editprofile, null);
        alertDialog.setView(convertView);
        ImageView ivBack = (ImageView) convertView.findViewById(R.id.ivBack);
        EditText etProfileDesc = (EditText) convertView.findViewById(R.id.etProfileDescription);
        ImageView ivConfirm = (ImageView) convertView.findViewById(R.id.ivTick);
        TextView tvUsername = (TextView)  convertView.findViewById(R.id.tvUsername);
        tvUsername.setText(mFirebaseUser.getDisplayName());
        DocumentReference dr = FirebaseFirestore.getInstance().collection("Users").document(mFirebaseUser.getEmail());

        alertDialog.setCancelable(true);
        alert = alertDialog.show();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        ivConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = etProfileDesc.getText().toString();
                try {
                    desc = desc.trim();
                } catch (Exception e) {
                }

                if (desc != null && !desc.equals("")) {
                    if (hasDescription) {
                        dr.update(magicalNames.getUsers_Column_ProfileDescription(), desc).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ProfileActivity.this, "Profile Updated Successfully",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        restartActivity();
                    } else {
                        Map<String, Object> newDescription = new HashMap<>();
                        newDescription.put(magicalNames.getUsers_Column_ProfileDescription(), desc);
                        dr.set(newDescription, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ProfileActivity.this, "Profile Updated Successfully!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        restartActivity();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Please enter a description",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    //region fab
    public void gooeyFab() {
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

    private void showFabMenu() {
        isFabOpen = true;
        gooeyFab();
        fabPdf.animate().translationY(-getResources().getDimension(R.dimen.fab));
        fabPdf.animate().translationX(-getResources().getDimension(R.dimen.fabMargin));
        fabImg.animate().translationY(-getResources().getDimension(R.dimen.fabCenter));
        fabVid.animate().translationY(-getResources().getDimension(R.dimen.fab));
        fabVid.animate().translationX(getResources().getDimension(R.dimen.fabMargin));

    }

    private void closeFabMenu() {
        isFabOpen = false;
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
        int requestCode = 0;
        if (isFabOpen) {
            i = new Intent(Intent.ACTION_GET_CONTENT);
            switch (v.getId()) {
                case R.id.fabImg:
                    i.setType("image/*");
                    requestCode = 0;
                    break;
                case R.id.fabPdf:
                    i.setType("application/pdf");
                    requestCode = 1;
                    break;
                case R.id.fabVid:
                    i.setType("video/*");
                    requestCode = 2;
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
                if (resultCode == RESULT_OK) {
                    File file = new File(data.getData().toString());
                    String path = file.getAbsolutePath();
                    Log.d("PATH:", path);
                    Intent i = new Intent(ProfileActivity.this, AddPostDetail.class);
                    i.putExtra("path", path);
                    i.putExtra("email", mFirebaseAuth.getCurrentUser().getEmail());
                    startActivity(i);
                }
                break;
        }
    }
    //endregion


    @Override
    protected void onPause() {
        super.onPause();
        if (fAuthStateListener != null) {
            fFirebaseAuth.removeAuthStateListener(fAuthStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fFirebaseAuth.addAuthStateListener(fAuthStateListener);
    }
}
