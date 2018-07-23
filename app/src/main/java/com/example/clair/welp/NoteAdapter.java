package com.example.clair.welp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.clair.welp.Firebase.MagicalNames;
import com.example.clair.welp.Objects.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

//import static com.example.clair.welp.MainActivity.FakeDataGenerator;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{
    public List<Note> mDataset;
    Context context;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    final FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    String userEmail = mFirebaseUser.getEmail();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MagicalNames magicalNames = new MagicalNames();
    public NoteAdapter(){}


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvUsername,tvPostTimeDetail,tvNoteTitle,tvNoteDesc,tv_Upvote,tv_Downvote;
        Button btnSubject,btnYear,btnCategory,btnSummary;
        LinearLayout llUsernameTime;
        ImageButton ib_Upvote, ib_Downvote;
        CircleImageView profile_image;

        public ViewHolder(View v) {
            super(v);
            tvUsername = v.findViewById(R.id.tvUsername);
            tvPostTimeDetail = v.findViewById(R.id.tvPostTimeDetail);
            profile_image = v.findViewById(R.id.profile_image);
            tvNoteTitle = v.findViewById(R.id.tvNoteTitle);
            tvNoteDesc = v.findViewById(R.id.tvNoteDesc);
            tv_Upvote = v.findViewById(R.id.tv_Upvote);
            tv_Downvote = v.findViewById(R.id.tv_Downvote);
            btnSubject=v.findViewById(R.id.btn_subject);
            btnCategory=v.findViewById(R.id.btn_category);
            btnYear=v.findViewById(R.id.btn_year);
            btnSummary=v.findViewById(R.id.btn_summarry);
            ib_Upvote = v.findViewById(R.id.ib_Upvote);
            ib_Downvote = v.findViewById(R.id.ib_Downvote);
            llUsernameTime = v.findViewById(R.id.llUsernameTime);

            //SET onClickListeners for views in note_template here
            llUsernameTime.setOnClickListener((View.OnClickListener) this);
            profile_image.setOnClickListener((View.OnClickListener) this);
        }

        //DO something when view is clicked on, based on view's id
        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            switch (v.getId()) {

                case R.id.llUsernameTime:
                    if(pos != RecyclerView.NO_POSITION){
                        Note clickedNote = mDataset.get(pos);
                        checkAndGoToProfile(clickedNote);
                    }
                    break;

                case R.id.profile_image:
                    if(pos != RecyclerView.NO_POSITION){
                        Note clickedNote = mDataset.get(pos);
                        checkAndGoToProfile(clickedNote);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    public NoteAdapter(Context context){
        this.context=context;
        this.mDataset=new ArrayList<>();
    }
    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.note_template,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {

        final Note n=mDataset.get(position);
        holder.tvUsername.setText(n.getUsername());
        holder.tvPostTimeDetail.setText("Posted: "+n.getDatePosted());
        holder.tvNoteTitle.setText(n.getNoteTitle());
        holder.tvNoteDesc.setText(n.getNoteDescription());
        String upvote= n.getUpvote()+"";
        String downvote=n.getDownvote()+"";
        holder.tv_Upvote.setText((upvote));
        holder.tv_Downvote.setText(downvote);
        holder.btnSubject.setText(n.getTags().get(0));
        holder.btnYear.setText(n.getTags().get(1));
        holder.btnCategory.setText(n.getTags().get(2));
        holder.btnSummary.setText(n.getTags().get(3));

    }

    //CHECK whether user is current or not, then go to profile page based on that
    public void checkAndGoToProfile(Note clickedNote){
        Note note = clickedNote;
        if(userEmail == note.getEmail()){
            goToPage(note, "ProfileActivity");
            Log.d(TAG, "checkAndGoToProfile: is current "+ note.getUsername() + " " + note.getEmail() + "" );
        } else{
            goToPage(note,"ProfileActivity");
            Log.d(TAG, "checkAndGoToProfile: is not current "+ note.getUsername() + " " + note.getEmail() + "" );
        }
    }


    //GOTO page based on activityname passed in
    public void goToPage(Note passedNote, String activityName){
        String ActivityName = activityName;
        Note PassedNote = passedNote;
        switch (ActivityName) {

            case "ProfileActivity":
                Intent i = new Intent(context,ProfileActivity.class);
                i.putExtra("frgToLoad", 0);
                context.startActivity(i);
                break;

            case "ProfileActivity_otheruser":
                break;

            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(mDataset==null) return 0;
        else return mDataset.size();
    }
    public void addItem (Note t) {
        mDataset.add(t);
        notifyItemChanged(mDataset.size() - 1);
    }

    public void addAllItems(List<Note> notesList) {
        for (Note note : notesList) {
            addItem(note);
        }
    }
    public void deleteEverything(){
        if(mDataset!=null)  mDataset.clear();
    }
}

