package com.example.clair.welp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clair.welp.Firebase.CommentFirestore;
import com.example.clair.welp.Firebase.NoteFirestore;
import com.example.clair.welp.Objects.Comment;
import com.example.clair.welp.Objects.Notebook;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsActivity extends AppCompatActivity {
    @BindView(R.id.etComment)
    EditText etComment;
    @BindView(R.id.tvLoading)
    TextView tvLoading;
    @BindView(R.id.ibPost)
    ImageButton ibPost;
    @BindView(R.id.rvComments)
    RecyclerView rvComments;
    RecyclerView.LayoutManager mLayoutManager;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    String passedNoteID;
    CommentFirestore commentFirestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.bind(this);

        //Get extra
        passedNoteID = getIntent().getExtras().getString("passedNoteID");

        //Back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setTitle("Comments");


        //Recyclerview
        init();
        getCommentsList();



        //Post Comment
        ibPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentString = etComment.getText().toString();
                if (commentString.equals("") || commentString == null) {
                    Toast.makeText(CommentsActivity.this, "Enter a comment", Toast.LENGTH_SHORT).show();
                } else {
                    postComment(commentString, passedNoteID);
                    etComment.setText("");
                    Toast.makeText(CommentsActivity.this, "Comment posted", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void postComment(String commentString, String passedNoteID) {
        //CHANGE DATE FORMAT
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String datePosted = dateFormat.format(date);
        Comment comment = new Comment(mFirebaseUser.getEmail(), mFirebaseUser.getDisplayName(), commentString, datePosted, passedNoteID);
        commentFirestore.add(comment);
    }

    private void init() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvComments.setLayoutManager(mLayoutManager);
        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        commentFirestore = new CommentFirestore();
    }

    private void getCommentsList() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser.getEmail() != null) {
            Query query = db.collection("Comments").whereEqualTo("NoteID", passedNoteID);

            FirestoreRecyclerOptions<Comment> comment = new FirestoreRecyclerOptions.Builder<Comment>()
                    .setQuery(query, Comment.class)
                    .build();

            adapter = new FirestoreRecyclerAdapter<Comment, CommentsActivity.CommentsHolder>(comment) {
                @Override
                public void onBindViewHolder(CommentsActivity.CommentsHolder holder, int position, Comment model) {
                    tvLoading.setText("");
                    holder.tvComment.setText(model.getComment());
                    holder.tvCommentTime.setText(model.getDatePosted());
                    holder.tvUsername.setText(model.getUsername());
//                Glide.with(getApplicationContext())
//                        .load(model.getImage())
//                        .into(holder.imageView);

                    holder.tvUsername.setOnClickListener(v -> {
                        if ((mFirebaseUser.getEmail()).equals(model.getEmail())) {
                            startActivity(new Intent(CommentsActivity.this, ProfileActivity.class));
                        }else{
                            Intent intent = new Intent(CommentsActivity.this, ProfileActivity_otheruser.class);
                            intent.putExtra("Email", model.getEmail());
                            intent.putExtra("Username", model.getUsername());
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public CommentsActivity.CommentsHolder onCreateViewHolder(ViewGroup group, int i) {
                    View view = LayoutInflater.from(group.getContext()).inflate(R.layout.activity_comment_item, group, false);
//                View view = LayoutInflater.from(group.getContext())
//                        .inflate(R.layout.list_item, group, false);

                    return new CommentsActivity.CommentsHolder(view);
                }

                @Override
                public void onError(FirebaseFirestoreException e) {
                    Log.e("error", e.getMessage());
                }
            };

            adapter.notifyDataSetChanged();

            rvComments.setAdapter(adapter);
//            if(rvComments.getAdapter()!= null ){
//                rvComments.smoothScrollToPosition(rvComments.getAdapter().getItemCount() - 1);
//            }
        } else {
            Intent intent = new Intent(CommentsActivity.this, Login.class);
            startActivity(intent);
        }


    }

    public class CommentsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvComment)
        TextView tvComment;
        @BindView(R.id.tvUsername)
        TextView tvUsername;
        @BindView(R.id.tvCommentTime)
        TextView tvCommentTime;

        public CommentsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        if (adapter != null) {
            if (adapter.getItemCount() > 0) {
                tvLoading.setText("");
            } else {
                tvLoading.setText("Post a comment");
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    //UP BUTTON (BACK) AND FOLLOW
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
