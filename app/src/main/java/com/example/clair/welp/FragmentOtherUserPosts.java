package com.example.clair.welp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clair.welp.Firebase.NoteFirestore;
import com.example.clair.welp.Objects.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class FragmentOtherUserPosts extends Fragment {

    String email,username;
    View view;
    // Context
    Context mContext;

    // RecyclerView
    RecyclerView mRecyclerView;
    NoteAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    NoteFirestore f;
    TextView tvLoading;
    @Override
    public void onStart() {
        super.onStart();
        email = ProfileActivity_otheruser.passedEmail;
        username = ProfileActivity_otheruser.passedUsername;

        FragmentOtherUserPosts r=this;
        tvLoading = (TextView) view.findViewById(R.id.tvLoading);
        f=new NoteFirestore(r, email, tvLoading);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAdapter.getItemCount() > 0){
                    tvLoading.setText("");
                } else{
                    tvLoading.setText(username +" has not posted anything yet");
                }

            }
        }, 2000); //set timer for error text to appear, 3s
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Context
        mContext = getContext();
        view = inflater.inflate(R.layout.otheruser_posts_fragment, container, false);

        // Views
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvNote);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter=new NoteAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public void UpdateList(List<Note> n){
        mAdapter.deleteEverything();
        mAdapter.addAllItems(n);
        if (n.size() > 0) {
            tvLoading.setText("");
            Log.d("TAG", "No posts");
        } else {
            tvLoading.setText(username +" has not posted anything yet");
            Log.d("TAG", "Yes posts");
        }
    }
}
