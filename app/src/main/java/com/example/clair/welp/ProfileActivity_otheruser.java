package com.example.clair.welp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clair.welp.Firebase.MagicalNames;
import com.example.clair.welp.Objects.Notification;
import com.firebase.client.annotations.NotNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ProfileActivity_otheruser extends AppCompatActivity {
    private TabLayout tabLayout;
    private TabLayout appBarLayout;
    private ViewPager viewPager;
    MenuItem itemFollow;

    //region firebase auth stuff
    private FirebaseAuth fFirebaseAuth;
    private FirebaseAuth.AuthStateListener fAuthStateListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MagicalNames magicalNames = new MagicalNames();
    public static String passedEmail, passedUsername;
    Map<String, Object> userFollowMap = new HashMap<>();
    FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_otheruser);

        //Toolbar settings
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Set title of collapsed toolbar
        final CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitleEnabled(true);
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.color_YouCantSeeME)); // transperent color = #00000000
        collapsingToolbar.setCollapsedTitleTextColor(Color.rgb(255, 255, 255)); //Color of your title

        //get Extras
        passedEmail = getIntent().getExtras().getString("Email");
        passedUsername = getIntent().getExtras().getString("Username");

        fFirebaseAuth = FirebaseAuth.getInstance();
        user = fFirebaseAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(ProfileActivity_otheruser.this, Login.class);
            startActivity(intent);
        }

        db.collection("Users").document(passedEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String desc = "";
                    try {
                        desc = task.getResult().getString(magicalNames.getUsers_Column_ProfileDescription());

                    } catch (Exception e) {
                        Log.d(TAG, "User has no description: ", task.getException());
                    }

                    TextView tvDesc = (TextView) findViewById(R.id.profile_desc);
                    if (desc != "" && desc != null) {
                        tvDesc.setText(desc);
                    } else {
                        tvDesc.setText(passedUsername + " has no description yet");
                    }

                } else {
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
        adapter.AddFragment(new FragmentOtherUserNotebooks(), "Notebooks");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


    }



    public void showInitialFollowStatus() {

        db.collection("Users").document(user.getEmail()).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                List<String> followingUsers = new ArrayList<String>();

                                //get column of following users
                                followingUsers = (List<String>) document.get(magicalNames.getUsers_Column_FollowingUsers());
                                //Log.d("TAG", "followingUsers size " + followingUsers.size());
                                DocumentReference dr = FirebaseFirestore.getInstance().collection("Users").document(user.getEmail());

                                if (followingUsers != null) { //if current user has following users
                                    if (followingUsers.contains(passedUsername)) {
                                        itemFollow.setTitle("Followed   ");
                                    } else {
                                        itemFollow.setTitle("Follow   ");
                                    }
                                } else {
                                    itemFollow.setTitle("Follow   ");
                                }

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    public void followOrUnfollow() {
        //region auth
        db.collection("Users").document(user.getEmail()).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                List<String> followingUsers = new ArrayList<String>();
                                Boolean isFollowed = false;
                                String userToUnfollow = "";

                                userFollowMap.put(magicalNames.getUsers_Column_Subjects(), document.get(magicalNames.getUsers_Column_Subjects()));
                                userFollowMap.put(magicalNames.getUsers_Column_Username(), document.getString(magicalNames.getUsers_Column_Username()));
                                userFollowMap.put(magicalNames.getUsers_Column_YearOfStudy(), document.getString(magicalNames.getUsers_Column_YearOfStudy()));

                                //get column of following users
                                followingUsers = (List<String>) document.get(magicalNames.getUsers_Column_FollowingUsers());

                                DocumentReference dr = FirebaseFirestore.getInstance().collection("Users").document(user.getEmail());

                                if (followingUsers != null) {
//
                                    Log.d("TAG", "followingUsers size " + followingUsers.size() + " " + passedUsername);

                                    if (followingUsers.contains(passedUsername)) {
                                        isFollowed = true;
                                        userToUnfollow = passedUsername;
                                        Log.d("TAG", "removed user");
                                    }


                                    if (isFollowed) {  //if already followed, update field value
                                        followingUsers.remove(userToUnfollow);
                                        final List<String> finalFollowingUsers = followingUsers;
                                        new AlertDialog.Builder(ProfileActivity_otheruser.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                                                .setTitle("Confirm Unfollow")
                                                .setMessage("Do you really want to unfollow this user?")
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        itemFollow.setTitle("Follow   ");
                                                        dr.update(magicalNames.getUsers_Column_FollowingUsers(), finalFollowingUsers).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "DocumentSnapshot added with ID: " + dr.getId());
                                                                Toast.makeText(ProfileActivity_otheruser.this, "Successful",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NotNull Exception e) {
                                                                Log.w(TAG, "Eroor adding document", e);
                                                                Toast.makeText(ProfileActivity_otheruser.this, "Something went wrong",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }).setNegativeButton(android.R.string.no, null).show();

                                    } else { //if not already followed
                                        itemFollow.setTitle("Followed   ");
                                        followingUsers.add(passedUsername);
                                        dr.update(magicalNames.getUsers_Column_FollowingUsers(), followingUsers).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot added with ID: " + dr.getId());
                                                Toast.makeText(ProfileActivity_otheruser.this, "Successful",
                                                        Toast.LENGTH_SHORT).show();
                                                createNotification();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NotNull Exception e) {
                                                Log.w(TAG, "Eroor adding document", e);
                                                Toast.makeText(ProfileActivity_otheruser.this, "Something went wrong",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }


                                } else { //if no column yet, update entire document
                                    itemFollow.setTitle("Followed   ");
                                    followingUsers = new ArrayList<>();
                                    followingUsers.add(passedUsername);
                                    userFollowMap.put(magicalNames.getUsers_Column_FollowingUsers(), followingUsers);

                                    dr.set(userFollowMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + dr.getId());
                                            Toast.makeText(ProfileActivity_otheruser.this, "Successful",
                                                    Toast.LENGTH_SHORT).show();
                                            createNotification();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NotNull Exception e) {
                                            Log.w(TAG, "Eroor adding document", e);
                                            Toast.makeText(ProfileActivity_otheruser.this, "Something went wrong",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            } else {
                                Log.d(TAG, "No such document");
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    //region create Notification on follow
    public void createNotification() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateSent = dateFormat.format(new Date());
        String currentUsername = user.getDisplayName();

        DocumentReference drNotif = FirebaseFirestore.getInstance().collection("Notifications").document();
        Map<String, Object> notification = new HashMap<>();
        notification.put("ReceivingEmail", passedEmail);
        notification.put("SendingUsername", currentUsername);
        notification.put("SendingEmail", user.getEmail());
        notification.put("NotificationType", "Notification");
        notification.put("NotificationString", currentUsername+" has started following you");
        notification.put("DateSent", dateSent);
        drNotif.set(notification);
    }
    //endregion

    //region menu on top
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_items_otheruser_profile, menu);
        itemFollow = menu.findItem(R.id.menuFollow);
        showInitialFollowStatus();
        return true;
    }

    //UP BUTTON (BACK) AND FOLLOW
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuFollow:
                Log.d("TAG", "Click Follow");
                followOrUnfollow();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    //BACK BUTTON (BOTTOM)
    @Override
    public void onBackPressed() {

//        startActivity(new Intent(ProfileActivity_otheruser.this, MainActivity.class));
        this.finish();
    }


}
