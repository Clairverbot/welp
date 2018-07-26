package com.example.clair.welp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clair.welp.Firebase.NoteFirestore;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;

public class FragmentCurrentUserFollowing extends Fragment{
    View view;
    // Context
    Context mContext;

    // RecyclerView
    RecyclerView.LayoutManager mLayoutManager;
    NoteFirestore f;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseAuth mFirebaseAuth;

    @BindView(R.id.rvNotebook)
    RecyclerView notebookList;

    @BindView(R.id.tvLoading)
    TextView textLoading;
    public FragmentCurrentUserFollowing(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.currentuser_following_fragment, container, false);
        return view;
    }
}
