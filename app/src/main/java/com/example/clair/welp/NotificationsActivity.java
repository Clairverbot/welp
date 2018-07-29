package com.example.clair.welp;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clair.welp.Firebase.NoteFirestore;
import com.example.clair.welp.Objects.Notebook;
import com.example.clair.welp.Objects.Notification;
import com.example.clair.welp.OtherStuff.BottomNavigationViewHelper;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsActivity extends AppCompatActivity implements View.OnClickListener{
    RecyclerView.LayoutManager mLayoutManager;
    NoteFirestore f;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseAuth mFirebaseAuth;
    @BindView(R.id.rvNotifications)
    RecyclerView rvNotifications;

    @BindView(R.id.ivNoNotifs)
    ImageView ivNoNotifs;
    @BindView(R.id.tvLoadingNotifs)
    TextView tvLoading;
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
    boolean isFabOpen=false;
    private FirebaseAuth fFirebaseAuth;
    private FirebaseAuth.AuthStateListener fAuthStateListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //region auth
        fFirebaseAuth=FirebaseAuth.getInstance();
        fAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user==null){
                    Intent intent = new Intent(NotificationsActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        //endregion


        //region bottom nav
        fabPdf.setOnClickListener((View.OnClickListener)this);
        fabImg.setOnClickListener((View.OnClickListener)this);
        fabVid.setOnClickListener((View.OnClickListener)this);


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
        bottomNavigationView.setSelectedItemId(R.id.action_noti);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
                        startActivity(new Intent(NotificationsActivity.this, MainActivity.class));
                        finish();
                        break;
                    case R.id.action_chat:
                        break;
                    case R.id.action_upload:
                        break;
                    case R.id.action_noti:
                        break;
                    case R.id.action_profile:
                        startActivity(new Intent(NotificationsActivity.this, ProfileActivity.class));
                        finish();
                        break;
                }
                return true;
            }
        });
        //endregion

        init();

        getNotificationsList();
    }


    //region firestore rv
    private void init(){
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvNotifications.setLayoutManager(mLayoutManager);
        db = FirebaseFirestore.getInstance();
        Log.d("TAG","Notifications init");
    }

    private void getNotificationsList() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (userEmail != null) {
            Log.d("TAG", "Notifications " + userEmail);
            Query query = db.collection("Notifications").whereEqualTo("ReceivingEmail", userEmail).orderBy("DateSent", com.google.firebase.firestore.Query.Direction.DESCENDING);

            FirestoreRecyclerOptions<Notification> notification = new FirestoreRecyclerOptions.Builder<Notification>()
                    .setQuery(query, Notification.class)
                    .build();

            adapter = new FirestoreRecyclerAdapter<Notification, NotificationsHolder>(notification) {
                @Override
                public void onBindViewHolder(NotificationsHolder holder, int position, Notification model) {
                    holder.tvNotification.setText(model.getNotificationString());
                    Log.d("TAG", "Notifications ADAPTER");

                    ivNoNotifs.setVisibility(View.GONE);
                    tvLoading.setText("");

                    long time = Long.valueOf(TimeUtility.getDateFromDateTime(model.getDateSent()));//2016-09-01 15:57:20 pass your date here
                    String timeStr = TimeUtility.timeAgo(time / 1000);
                    holder.tvNotificationTime.setText(timeStr);
//                Glide.with(getApplicationContext())
//                        .load(model.getImage())
//                        .into(holder.imageView);

                    holder.itemView.setOnClickListener(v -> {
                                Intent intent = new Intent(NotificationsActivity.this, ProfileActivity_otheruser.class);
                                intent.putExtra("Email", model.getSendingEmail()); // getText() SHOULD NOT be static!!!
                                intent.putExtra("Username", model.getSendingUsername());
                                startActivity(intent);
                            }
                    );
                }

                @Override
                public NotificationsHolder onCreateViewHolder(ViewGroup group, int i) {
                    View view = LayoutInflater.from(group.getContext()).inflate(R.layout.activity_notification_item, group, false);
//                View view = LayoutInflater.from(group.getContext())
//                        .inflate(R.layout.list_item, group, false);

                    return new NotificationsHolder(view);
                }

                @Override
                public void onError(FirebaseFirestoreException e) {
                    Log.e("error", e.getMessage());
                }
            };

            adapter.notifyDataSetChanged();
            rvNotifications.setAdapter(adapter);

        } else {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }

    }


    public class NotificationsHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvNotification)
        TextView tvNotification;
        @BindView(R.id.tvNotificationTime)
        TextView tvNotificationTime;
        public NotificationsHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        tvLoading.setText("");
        if (adapter != null) {
            if (adapter.getItemCount() > 0) {
                ivNoNotifs.setVisibility(View.GONE);
            }else{
                ivNoNotifs.setVisibility(View.VISIBLE);
                 }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    //endregion

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
        tvLoading.setText("");
        if (adapter != null) {
            if (adapter.getItemCount() > 0) {
                ivNoNotifs.setVisibility(View.GONE);
            }else{
                ivNoNotifs.setVisibility(View.VISIBLE);
            }
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

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        switch (requestCode) {
            case 0:
            case 1:
            case 2:
                if(resultCode==RESULT_OK){
                    Uri file=data.getData();
                    Intent i=new Intent(NotificationsActivity.this,AddPostDetail.class);
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
            case R.id.about_menu:
                //TODO: create credits page one dayyy
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
