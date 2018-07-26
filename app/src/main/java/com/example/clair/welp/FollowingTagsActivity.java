package com.example.clair.welp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FollowingTagsActivity extends AppCompatActivity{
    String userEmail;
    List<String> followingTags;

    @BindView(R.id.lvTags)
    ListView lvTags;


    @BindView(R.id.tvNoTags)
    TextView tvNoTags;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followingtags);
        ButterKnife.bind(this);

        //Title and backbutton
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Tags You Follow");

        populateListView();
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
                                followingTags = (List<String>) document.get("Subjects");
                                if (followingTags == null) {
                                    tvNoTags.setText("You have not followed any tags yet");
                                } else {
                                    if (followingTags.size() == 0) {
                                        tvNoTags.setText("You have not followed any tags yet");
                                    } else {
                                        //Populate Listview if user got following tags
                                        tvNoTags.setText("");
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FollowingTagsActivity.this, R.layout.currentuser_followingtag_item, R.id.tvTagName, followingTags);

                                        adapter.notifyDataSetChanged();
                                        lvTags.setAdapter(adapter);
                                    }
                                }
                            }
                        }
                    }
                });
    }
    //UP BUTTON (BACK)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //GOTO : Profile Activity with Notebook Tab Selected
        if(id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

    //BACK BUTTON (BOTTOM)
    @Override
    public void onBackPressed() {
        this.finish();
    }
}
