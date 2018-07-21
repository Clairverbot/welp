package com.example.clair.welp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clair.welp.Firebase.NoteFirestore;
import com.example.clair.welp.Objects.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class FragmentCurrentUserPosts extends Fragment {
    View view;
    // Context
    Context mContext;

    // RecyclerView
    RecyclerView mRecyclerView;
    NoteAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    NoteFirestore f;
    private FirebaseAuth mFirebaseAuth;

    public FragmentCurrentUserPosts(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Context
        mContext = getContext();
        view = inflater.inflate(R.layout.currentuser_posts_fragment, container, false);


        // Views
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvNote);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

//        mName = "Android";
//        mEmail = "android@google.com";

        // specify an adapter
//        mAdapter = new RecyclerView.Adapter(getActivity(), mNavTitles, mIcons, mName, mEmail, R.drawable.profile_icon);
        mAdapter=new NoteAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentCurrentUserPosts r=this;
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        TextView tvLoading = (TextView) view.findViewById(R.id.tvLoading);
        f=new NoteFirestore(r, mFirebaseUser.getEmail(), tvLoading);
    }

    public void UpdateList(List<Note> n){
        mAdapter.deleteEverything();
        mAdapter.addAllItems(n);
    }
}
