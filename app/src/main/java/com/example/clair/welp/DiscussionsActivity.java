package com.example.clair.welp;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.clair.welp.Firebase.ChatFirestore;
import com.example.clair.welp.Objects.Chat;
import com.example.clair.welp.OtherStuff.BottomNavigationViewHelper;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

public class DiscussionsActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView.LayoutManager mLayoutManager;
    ChatFirestore f;
    private FirebaseFirestore db;
    private FirebaseAuth mFirebaseAuth;
    DiscussionsAdapter mAdapter;

    @BindView(R.id.rvDiscussions)
    RecyclerView rvDiscussions;
    @BindView(R.id.ivNoDiscussions)
    ImageView ivNoDiscussions;
    @BindView(R.id.tvLoadingDiscussions)
    TextView tvLoadingDiscussions;
    @BindView(R.id.btnadduser)
    ImageButton btnadduser;
    @BindView(R.id.fabUpload)
    FloatingActionButton fab;
    @BindView(R.id.fabPdf)
    FloatingActionButton fabPdf;
    @BindView(R.id.fabImg)
    FloatingActionButton fabImg;
    @BindView(R.id.fabVid)
    FloatingActionButton fabVid;
    @BindView(R.id.bottomNav)
    BottomNavigationView bottomNavigationView;
    AlertDialog alert;
    String userEmail;
    List<String> followingUsers;

    ArrayAdapter<String> adapter;

    boolean isFabOpen = false;
    private FirebaseAuth fFirebaseAuth;
    private FirebaseAuth.AuthStateListener fAuthStateListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        ButterKnife.bind(this);
        ivNoDiscussions.setVisibility(View.GONE);
        if (mAdapter != null) {
            if (mAdapter.getItemCount() > 0) {
                ivNoDiscussions.setVisibility(View.GONE);
            } else {
                ivNoDiscussions.setVisibility(View.VISIBLE);
            }
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //region auth
        fFirebaseAuth = FirebaseAuth.getInstance();
        fAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(DiscussionsActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        //endregion


        //region bottom nav
        fabPdf.setOnClickListener((View.OnClickListener) this);
        fabImg.setOnClickListener((View.OnClickListener) this);
        fabVid.setOnClickListener((View.OnClickListener) this);

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


        bottomNavigationView = findViewById(R.id.bottomNav);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.action_chat);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        startActivity(new Intent(DiscussionsActivity.this, MainActivity.class));
                        finish();
                        break;
                    case R.id.action_chat:
                        break;
                    case R.id.action_upload:
                        break;
                    case R.id.action_noti:
                        startActivity(new Intent(DiscussionsActivity.this, NotificationsActivity.class));
                        finish();
                        break;
                    case R.id.action_profile:
                        startActivity(new Intent(DiscussionsActivity.this, ProfileActivity.class));
                        finish();
                        break;
                }
                return true;
            }
        });
        //endregion


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        rvDiscussions.setLayoutManager(mLayoutManager);
        rvDiscussions.setItemAnimator(new DefaultItemAnimator());
        Log.d(TAG, "ERROR " + "Adapter Problem");
        mAdapter = new DiscussionsAdapter(this);
        rvDiscussions.setAdapter(mAdapter);


        btnadduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddUsersDialog();
            }
        });
    }

    private void showAddUsersDialog() {
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore.getInstance().collection("Users").document(userEmail).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document != null) {
                                followingUsers = (List<String>) document.get("FollowingUsers");

                                if (followingUsers != null) {
                                    if (followingUsers.size() > 0) { //there IS users followed
                                        createUsersDialog(followingUsers);

                                    } else { //No users
                                        createNoUsersDialog();
                                    }
                                } else{
                                    createNoUsersDialog();
                                }
                            }
                        }
                    }
                });
    }

    public void createNoUsersDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        LayoutInflater inflater = LayoutInflater.from(this);
        View convertView = (View) inflater.inflate(R.layout.dialog_nousers, null);
        alertDialog.setView(convertView)
                .setTitle("No users followed")
                .setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        alertDialog.setCancelable(true);
        AlertDialog createAlert = alertDialog.show();
        int textViewId = createAlert.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
        TextView tv = (TextView) createAlert.findViewById(textViewId);
        tv.setTextColor(this.getResources().getColor(R.color.Dark_Grey));
    }

    public void createUsersDialog(List<String> followingUsers) {
        //SHOW Add to/Create Notebook Dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View convertView = (View) inflater.inflate(R.layout.dialog_chatwithfollowed_discussion, null);
        alertDialog.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.lvFollowingUsers);

        //Populate Listview if user got notebooks
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.currentuser_followinguser_item, R.id.tvOtherUserName,
                followingUsers);
        adapter.notifyDataSetChanged();
        lv.setAdapter(adapter);
        Utility.setListViewHeightBasedOnChildren(lv); //SET MAX HEIGHT OF LISTVIEW TO 6 LV ITEMS
        alertDialog.setCancelable(true);
        alert = alertDialog.show();

        onClickFollowingUser(lv, followingUsers);
    }




    //Go to user's profile
    public void onClickFollowingUser(ListView lv, List<String> followingUsers) {

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String passedUsername = followingUsers.get(position);

                FirebaseFirestore.getInstance().collection("Users").whereEqualTo("Username", passedUsername).get().
                        addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    String passedEmail = "";
                                    for (DocumentSnapshot document : task.getResult()) {
                                        passedEmail = document.getId().toString();
                                    }
                                    Intent i = new Intent(DiscussionsActivity.this, MessageListActivity.class);
                                    i.putExtra("passedSendingEmail", passedEmail);
                                    i.putExtra("passedSendingUsername", passedUsername);
                                    startActivity(i);                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DiscussionsActivity r = this;
        ivNoDiscussions.setVisibility(View.GONE);
        tvLoadingDiscussions.setText("");
        f = new ChatFirestore(r);
        if (mAdapter.getItemCount() > 0) {
            tvLoadingDiscussions.setText("");
            ivNoDiscussions.setVisibility(View.GONE);
        } else {
            tvLoadingDiscussions.setText("");
            ivNoDiscussions.setVisibility(View.VISIBLE);
        }


    }

    public void UpdateList(List<Chat> n) {
        mAdapter.deleteEverything();
        mAdapter.addAllItems(n);
    }


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
        tvLoadingDiscussions.setText("");
        if (mAdapter != null) {
            if (mAdapter.getItemCount() > 0) {
                ivNoDiscussions.setVisibility(View.GONE);
            } else {
                ivNoDiscussions.setVisibility(View.VISIBLE);
            }
        }
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

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        switch (requestCode) {
            case 0:
            case 1:
            case 2:
                if (resultCode == RESULT_OK) {
                    Uri file = data.getData();
                    Intent i = new Intent(DiscussionsActivity.this, AddPostDetail.class);
                    i.putExtra("path", file);
                    Log.d("Ppath", file.toString());
                    i.putExtra("email", currentUser.getEmail());
                    i.putExtra("username", currentUser.getDisplayName());
                    startActivity(i);
                }
                break;
        }
    }
    //endregion

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
            case R.id.filter_menu:
                startActivity(new Intent(DiscussionsActivity.this, SearchFilterActivity.class));
                return true;
            case R.id.search_menu:
                startActivity(new Intent(DiscussionsActivity.this, SearchActivity.class));
                return true;
            case R.id.about_menu:
                startActivity(new Intent(DiscussionsActivity.this, AboutUsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //region add user button


}
