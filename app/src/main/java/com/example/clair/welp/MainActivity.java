package com.example.clair.welp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //region firebase instance variables
    private FirebaseDatabase fFirebaseDatabase;
    private DatabaseReference fDatabaseReference;
    private ChildEventListener fChildEventListener;
    private FirebaseAuth fFirebaseAuth;
    private FirebaseAuth.AuthStateListener fAuthStateListener;

    //endregion

    //region firebase auth stuff
    public static final String ANONYMOUS = "anonymous";
    private String Username;
    public static final int RC_SIGN_IN=1;
    //endregion

    //region recycler view stuf
    RecyclerView rvNote;
    NoteAdapter nAdapter;
    RecyclerView.LayoutManager rLayoutManager;
    List<Note> myDataset;
    Note n=new Note();
    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //region auth
        Username=ANONYMOUS;
        fFirebaseAuth=FirebaseAuth.getInstance();
        fAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null){
                    //user is signed in
                }
                else{
                    //fiyahbase ui, dun need liao :(
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

        //region database
        fFirebaseDatabase=FirebaseDatabase.getInstance();
        //endregion

        rvNote=findViewById(R.id.rvNote);
        rLayoutManager=new LinearLayoutManager(this);
        rvNote.setLayoutManager(rLayoutManager);
        nAdapter=new NoteAdapter(this);
        nAdapter.addAllItems(FakeDataGenerator());
        rvNote.setAdapter(nAdapter);

    }
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

    //add sample data, yay firebase is a lie
    public static List<Note> FakeDataGenerator(){
        String[]fakeTags={"E-Math","Sec 4","Practice","E-Math Sec 4"};
        Note n=new Note("welperman@gmail.com","Welpermen","R.drawable.dummyImg","E-Math 'O-Level' Practice Paper 2018","very useful practice paper cos it's practice paper and practice paper are all useful","shdakdhksahdkshfjskdfhsdkjfkhsfhsdfignoreme","05/12/2017,12:01PM",null,fakeTags,null,100,1);
        Note nn=new Note("welperman@gmail.com","Welpermen","R.drawable.dummyImg","E-Math 'O-Level' Practice Paper 2018","very useful practice paper cos it's practice paper and practice paper are all useful","shdakdhksahdkshfjskdfhsdkjfkhsfhsdfignoreme","05/12/2017,12:01PM",null,fakeTags,null,100,1);
        Note nnn=new Note("welperman@gmail.com","Welpermen","R.drawable.dummyImg","E-Math 'O-Level' Practice Paper 2018","very useful practice paper cos it's practice paper and practice paper are all useful","shdakdhksahdkshfjskdfhsdkjfkhsfhsdfignoreme","05/12/2017,12:01PM",null,fakeTags,null,100,1);
        Note[] notes={n,nn,nnn};
        return Arrays.asList(notes);

    }

}

