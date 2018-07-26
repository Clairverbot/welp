package com.example.clair.welp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clair.welp.Firebase.NoteFirestore;
import com.example.clair.welp.Objects.Notebook;
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

public class FragmentOtherUserNotebooks extends Fragment {
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

    ProfileActivity_otheruser otheruser;


    public FragmentOtherUserNotebooks(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Context
        mContext = getContext();
        view = inflater.inflate(R.layout.otheruser_notebooks_fragment, container, false);


        ButterKnife.bind(this, view);
        // notebookList = (RecyclerView) view.findViewById(R.id.rvNotebook);
        init();
        getNotebookList();

        return view;
    }

    private void init(){
        //linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        //friendList.setLayoutManager(linearLayoutManager);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        notebookList.setLayoutManager(mLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    private void getNotebookList(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        String otheruserEmail = otheruser.passedEmail;
        String otheruserUsername = otheruser.passedUsername;
        Log.d("TAG", "otheruser " +otheruserEmail + " " + otheruserUsername);
        if (otheruserEmail != null){
            Query query = db.collection("Notebooks").whereEqualTo("Email", otheruserEmail);

            FirestoreRecyclerOptions<Notebook> notebook = new FirestoreRecyclerOptions.Builder<Notebook>()
                    .setQuery(query, Notebook.class)
                    .build();

            adapter = new FirestoreRecyclerAdapter<Notebook, FragmentOtherUserNotebooks.NotebooksHolder>(notebook) {
                @Override
                public void onBindViewHolder(FragmentOtherUserNotebooks.NotebooksHolder holder, int position, Notebook model) {
                    //progressBar.setVisibility(View.GONE);
                    holder.textName.setText(model.getNotebookName());
//                holder.textTitle.setText(model.getTitle());
//                holder.textCompany.setText(model.getCompany());
//                Glide.with(getApplicationContext())
//                        .load(model.getImage())
//                        .into(holder.imageView);

                    holder.itemView.setOnClickListener(v -> {
//                        Snackbar.make(notebookList, model.getNotebookName()+", "+model.getEmail(), Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();

                        Intent intent = new Intent(getActivity(), NotebookActivity.class);
                        intent.putExtra("NotebookName", model.getNotebookName()); // getText() SHOULD NOT be static!!!
                        intent.putExtra("NotebookNotes", (ArrayList<String>) model.getNotebookNotes());
                        intent.putExtra("Email", model.getEmail());
                        startActivity(intent);
                        for (String noteid: model.getNotebookNotes()){
                            Log.d("NoteID Fragment", "Note ID in Fragment" + noteid);
                        }
                    });
                }

                @Override
                public FragmentOtherUserNotebooks.NotebooksHolder onCreateViewHolder(ViewGroup group, int i) {
                    View view = LayoutInflater.from(group.getContext()).inflate(R.layout.currentuser_notebook_item, group, false);
//                View view = LayoutInflater.from(group.getContext())
//                        .inflate(R.layout.list_item, group, false);

                    return new FragmentOtherUserNotebooks.NotebooksHolder(view);
                }

                @Override
                public void onError(FirebaseFirestoreException e) {
                    Log.e("error", e.getMessage());
                }
            };

            adapter.notifyDataSetChanged();
            if (adapter == null) {
                textLoading.setText("User has no notebooks yet");
            } else {
                textLoading.setText("");
            }

            notebookList.setAdapter(adapter);

        }else{
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
        }


    }

    public class NotebooksHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvNotebookName)
        TextView textName;

        public NotebooksHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
