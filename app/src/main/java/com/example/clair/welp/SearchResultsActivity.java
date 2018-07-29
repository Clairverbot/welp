package com.example.clair.welp;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.clair.welp.Firebase.NoteFirestore;
import com.example.clair.welp.Objects.Note;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

public class SearchResultsActivity extends AppCompatActivity {
    @BindView(R.id.rvSearchResults)
    RecyclerView rvSearchResults;
    @BindView(R.id.tvLoading)
    TextView tvLoading;
    @BindView(R.id.ivErrorMeme)
    ImageView ivErrorMeme;

    RecyclerView.LayoutManager mLayoutManager;
    NoteAdapter mAdapter;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    ArrayList<String> passedSearchQuery;
    NoteFirestore f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresults);
        ButterKnife.bind(this);


        //Title and backbutton
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Search Results");
        //TODO: get concatenated Search Result Title

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        rvSearchResults.setLayoutManager(mLayoutManager);
        rvSearchResults.setItemAnimator(new DefaultItemAnimator());
        Log.d("TAGSR", "ERROR " + "Adapter Problem");
        mAdapter = new NoteAdapter(this);
        rvSearchResults.setAdapter(mAdapter);

    }


    @Override
    protected void onStart() {
        super.onStart();
        ivErrorMeme.setVisibility(View.GONE);
        SearchResultsActivity r = this;
        passedSearchQuery = getIntent().getExtras().getStringArrayList("passedSearchQuery");//getExtras
        f = new NoteFirestore(r, passedSearchQuery);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAdapter.getItemCount() > 0){
                    ivErrorMeme.setVisibility(View.GONE);
                    tvLoading.setText("");
                } else{
                    ivErrorMeme.setVisibility(View.VISIBLE);
                    tvLoading.setText("No posts matching your search query were found");
                }

            }
        }, 3000); //set timer for error text to appear, 3s

    }

    public void UpdateList(List<Note> n) {
        mAdapter.deleteEverything();
        mAdapter.addAllItems(n);
        if (n.size() > 0) {
            ivErrorMeme.setVisibility(View.GONE);
            tvLoading.setText("");
        } else if ((n.size() == 0)) {
            ivErrorMeme.setVisibility(View.VISIBLE);
            tvLoading.setText("No posts matching your search query were found");
        }
    }

    //UP BUTTON (BACK)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //GOTO : Profile Activity with Notebook Tab Selected
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }
}
