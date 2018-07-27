package com.example.clair.welp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clair.welp.Firebase.MagicalNames;
import com.example.clair.welp.Firebase.NoteFirestore;
import com.example.clair.welp.Objects.Notebook;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentCurrentUserFollowing extends Fragment {
    View view;
    // Context
    Context mContext;

    String userEmail;
    List<String> followingUsers;

    @BindView(R.id.lvFollowingUsers)
    ListView lvfollowingUsers;

    @BindView(R.id.clNotFollowing)
    ConstraintLayout clNotFollowing;

    @BindView(R.id.clViewFollowedTags)
    ConstraintLayout clViewFollowedTags;

    @BindView(R.id.tvNotFollowing)
    TextView tvNotFollowing;

    ArrayAdapter<String> adapter;
    //    @BindView(R.id.tvLoading)
//    TextView textLoading;
    public FragmentCurrentUserFollowing() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        view = inflater.inflate(R.layout.currentuser_following_fragment, container, false);
        ButterKnife.bind(this, view);
        populateListView();
        onClickFollowingUser();

        clViewFollowedTags.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent i = new Intent(mContext, FollowingTagsActivity.class);
                startActivity(i);
            }
        });
        return view;
    }

    public void populateListView() {
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseFirestore.getInstance().collection("Users").document(userEmail).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists() && document != null) {
                                followingUsers = (List<String>) document.get("FollowingUsers");

                                clNotFollowing.setVisibility(View.VISIBLE);
                                tvNotFollowing.setText("You have not followed anyone yet");

                                if (followingUsers != null) {
                                    if (followingUsers.size() > 0) {
                                        //Populate Listview if user got following users
                                        clNotFollowing.setVisibility(View.GONE);
                                        lvfollowingUsers.setVisibility(View.VISIBLE);
                                        adapter = new ArrayAdapter<String>(mContext, R.layout.currentuser_followinguser_item, R.id.tvOtherUserName, followingUsers);

                                        adapter.notifyDataSetChanged();
                                        lvfollowingUsers.setAdapter(adapter);
                                        Utility.setListViewHeightBasedOnChildrenScrollView(lvfollowingUsers);
                                    }
                                }
                            }
                        }
                    }
                });
    }




    //Go to user's profile
    public void onClickFollowingUser() {

        lvfollowingUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                                    Intent i = new Intent(mContext, ProfileActivity_otheruser.class);
                                    i.putExtra("Email", passedEmail);
                                    i.putExtra("Username", passedUsername);
                                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(i);

                                }
                            }
                        });
            }
        });
    }

    public void resetListView(){
        if (followingUsers != null) {
            followingUsers.clear();
            adapter.notifyDataSetChanged();
            lvfollowingUsers.setAdapter(adapter);
            lvfollowingUsers.setVisibility(View.GONE);
        }
        populateListView();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetListView();
    }
}
