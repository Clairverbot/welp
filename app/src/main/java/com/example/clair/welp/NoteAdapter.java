package com.example.clair.welp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.app.AppCompatDialogFragment;


import com.example.clair.welp.Firebase.MagicalNames;
import com.example.clair.welp.Objects.Note;
import com.example.clair.welp.Objects.Notebook;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

//import static com.example.clair.welp.MainActivity.FakeDataGenerator;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    public List<Note> mDataset;
    Context context;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MagicalNames magicalNames = new MagicalNames();

    public NoteAdapter() {
    }

    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    private String userEmail;
    List<String> notebookNotes;
    String notebookName, notebookDocumentID;
    ArrayList<Notebook> notebooks;
    ArrayList<String> notebookNames;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvUsername, tvPostTimeDetail, tvNoteTitle, tvNoteDesc, tv_Upvote, tv_Downvote;
        Button btnSubject, btnYear, btnCategory, btnSummary;
        LinearLayout llUsernameTime, llBookmark;
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
            btnSubject = v.findViewById(R.id.btn_subject);
            btnCategory = v.findViewById(R.id.btn_category);
            btnYear = v.findViewById(R.id.btn_year);
            btnSummary = v.findViewById(R.id.btn_summarry);
            ib_Upvote = v.findViewById(R.id.ib_Upvote);
            ib_Downvote = v.findViewById(R.id.ib_Downvote);
            llUsernameTime = v.findViewById(R.id.llUsernameTime);
            llBookmark = v.findViewById(R.id.llBookmark);
            //SET onClickListeners for views in note_template here
            llUsernameTime.setOnClickListener((View.OnClickListener) this);
            profile_image.setOnClickListener((View.OnClickListener) this);
            llBookmark.setOnClickListener((View.OnClickListener) this);
        }

        //DO something when view is clicked on, based on view's id
        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            switch (v.getId()) {

                case R.id.llUsernameTime:
                    if (pos != RecyclerView.NO_POSITION) {
                        Note clickedNote = mDataset.get(pos);
                        checkAndGoToProfile(clickedNote);
                    }
                    break;

                case R.id.profile_image:
                    if (pos != RecyclerView.NO_POSITION) {
                        Note clickedNote = mDataset.get(pos);
                        checkAndGoToProfile(clickedNote);
                    }
                    break;

                case R.id.llBookmark:
                    if (pos != RecyclerView.NO_POSITION) {
                        Note clickedNote = mDataset.get(pos);
                        String bookmarkEmail = clickedNote.getEmail();
                        //DocumentReference ref = db.collection("Notes").document(clickedNote.getDocumentID());
                        //String myId = ref.getId();
//                        openDialog(clickedNote.getDocumentID());
//                        notebookDialog = new CreateOrAddNotebookDialog();
////                        notebookDialog.setContentView(R.layout.dialog_create_addto_notebook);
//
//                        notebookDialog.show(({AppCompatActivity}context).getSupportFragmentManager(), "hi");
//                        notebookDialog.setCanceledOnTouchOutside(true);
                        // custom dialog
//                        final Dialog dialog = new Dialog(context);
//                        dialog.setContentView(R.layout.dialog_create_addto_notebook);
//                        dialog.show();
                        openDialog(bookmarkEmail);

                    }
                    break;

                default:
                    break;
            }
        }
    }

    private void openDialog(String bookmarkEmail) {
//       String documentID = docID;
//
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseUser = mFirebaseAuth.getCurrentUser();
//        userEmail = mFirebaseUser.getEmail();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = (View) inflater.inflate(R.layout.dialog_create_addto_notebook, null);
        alertDialog.setView(convertView);
//                        alertDialog.setTitle("List");
        ListView lv = (ListView) convertView.findViewById(R.id.lvNotebook);



        notebooks = new ArrayList<Notebook>();

        notebookNames = new ArrayList<String>();

        db.collection("Notebooks").whereEqualTo("Email", bookmarkEmail).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            Log.d("TAG","NOTEBOOK: task works " + bookmarkEmail);
                            for (DocumentSnapshot document : task.getResult()) {
                                notebookName = document.getString("NotebookName");
                                notebookNotes = (List<String>) document.get("NotebookNotes");
                                notebookDocumentID = document.getId();

                                Notebook n = new Notebook(userEmail, notebookName, notebookNotes, notebookDocumentID);
                                notebooks.add(n);
                                notebookNames.add(notebookName);

                                Log.d("TAG","NOTEBOOK: document " + notebookName);
                            }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        if (notebookNames != null){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.currentuser_notebook_item,R.id.tvNotebookName, notebookNames);
            lv.setAdapter(adapter);
        }

        alertDialog.setCancelable(true);
        alertDialog.show();
    }


    public NoteAdapter(Context context) {
        this.context = context;
        this.mDataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_template, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {

        final Note n = mDataset.get(position);
        holder.tvUsername.setText(n.getUsername());
        holder.tvPostTimeDetail.setText("Posted: " + n.getDatePosted());
        holder.tvNoteTitle.setText(n.getNoteTitle());
        holder.tvNoteDesc.setText(n.getNoteDescription());
        String upvote = n.getUpvote() + "";
        String downvote = n.getDownvote() + "";
        holder.tv_Upvote.setText((upvote));
        holder.tv_Downvote.setText(downvote);
        holder.btnSubject.setText(n.getTags().get(0));
        holder.btnYear.setText(n.getTags().get(1));
        holder.btnCategory.setText(n.getTags().get(2));
        holder.btnSummary.setText(n.getTags().get(3));

    }

    //CHECK whether user is current or not, then go to profile page based on that
    public void checkAndGoToProfile(Note clickedNote) {
        Note note = clickedNote;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userEmail = mFirebaseUser.getEmail();

        if (userEmail.equals(note.getEmail())) {
            goToPage(note, "ProfileActivity");
            Log.d(TAG, "checkAndGoToProfile: is current " + note.getUsername() + " " + note.getEmail() + " " + userEmail);
        } else {
            goToPage(note, "ProfileActivity_otheruser");
            Log.d(TAG, "checkAndGoToProfile: is not current " + note.getUsername() + " " + note.getEmail() + " " + userEmail);
        }
    }


    //GOTO page based on activityname passed in
    public void goToPage(Note passedNote, String activityName) {
        String ActivityName = activityName;
        Note PassedNote = passedNote;
        switch (ActivityName) {

            case "ProfileActivity":
                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("frgToLoad", 0);
                context.startActivity(i);
                break;

            case "ProfileActivity_otheruser":
                Intent i2 = new Intent(context, ProfileActivity_otheruser.class);
                i2.putExtra("Email", PassedNote.getEmail());
                i2.putExtra("Username", PassedNote.getUsername());
                context.startActivity(i2);
                break;

            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mDataset == null) return 0;
        else return mDataset.size();
    }

    public void addItem(Note t) {
        mDataset.add(t);
        notifyItemChanged(mDataset.size() - 1);
    }

    public void addAllItems(List<Note> notesList) {
        for (Note note : notesList) {
            addItem(note);
        }
    }

    public void deleteEverything() {
        if (mDataset != null) mDataset.clear();
    }
}

