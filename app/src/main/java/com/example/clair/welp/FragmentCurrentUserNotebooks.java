package com.example.clair.welp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.example.clair.welp.Objects.Notebook;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class FragmentCurrentUserNotebooks extends Fragment {
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



    public FragmentCurrentUserNotebooks(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Context
        mContext = getContext();
        view = inflater.inflate(R.layout.currentuser_notebooks_fragment, container, false);


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
        final FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser.getEmail() != null){
            String userEmail = mFirebaseUser.getEmail();
            Query query = db.collection("Notebooks").whereEqualTo("Email", userEmail);

            FirestoreRecyclerOptions<Notebook> notebook = new FirestoreRecyclerOptions.Builder<Notebook>()
                    .setQuery(query, Notebook.class)
                    .build();

            adapter = new FirestoreRecyclerAdapter<Notebook, NotebooksHolder>(notebook) {
                @Override
                public void onBindViewHolder(NotebooksHolder holder, int position, Notebook model) {
                    textLoading.setText("");
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
                        intent.putExtra("From", "currentuser");
                        intent.putExtra("Username", mFirebaseUser.getDisplayName());
                        startActivity(intent);
                        for (String noteid: model.getNotebookNotes()){
                            Log.d("NoteID Fragment", "Note ID in Fragment" + noteid);
                        }
                    });
                }

                @Override
                public NotebooksHolder onCreateViewHolder(ViewGroup group, int i) {
                    View view = LayoutInflater.from(group.getContext()).inflate(R.layout.currentuser_notebook_item, group, false);
//                View view = LayoutInflater.from(group.getContext())
//                        .inflate(R.layout.list_item, group, false);

                    return new NotebooksHolder(view);
                }

                @Override
                public void onError(FirebaseFirestoreException e) {
                    Log.e("error", e.getMessage());
                }
            };

            adapter.notifyDataSetChanged();


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
        if (adapter != null) {
            if (adapter.getItemCount() > 0){
                textLoading.setText("");
            } else{
                textLoading.setText("You have no notebooks yet");
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}

