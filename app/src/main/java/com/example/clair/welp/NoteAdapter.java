package com.example.clair.welp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.clair.welp.Firebase.MagicalNames;
import com.example.clair.welp.Objects.Note;
import com.example.clair.welp.Objects.Notebook;
import com.example.clair.welp.Objects.Tag;
import com.firebase.client.annotations.NotNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    AlertDialog alert;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvUsername, tvPostTimeDetail, tvNoteTitle, tvNoteDesc, tv_Upvote, tv_Downvote, tv_Comment;
        Button btnSubject, btnYear, btnCategory, btnSummary;
        LinearLayout llUsernameTime, llBookmark, llDownvote, llUpvote, llComment;
        ConstraintLayout llNoteTopBar;
        ImageButton ib_Upvote, ib_Downvote, ib_Comment;
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
            llNoteTopBar = v.findViewById(R.id.llNoteTopBar);
            llDownvote = v.findViewById(R.id.llDownvote);
            llUpvote = v.findViewById(R.id.llUpvote);
            tv_Comment = v.findViewById(R.id.tv_Comment);
            llComment = v.findViewById(R.id.llComment);
            ib_Comment = v.findViewById(R.id.ib_Comment);

            //SET onClickListeners for views in note_template here
            llNoteTopBar.setOnClickListener((View.OnClickListener) this);
            //llUsernameTime.setOnClickListener((View.OnClickListener) this);
            //profile_image.setOnClickListener((View.OnClickListener) this);
            llBookmark.setOnClickListener((View.OnClickListener) this);
            btnSubject.setOnClickListener((View.OnClickListener) this);
            btnCategory.setOnClickListener((View.OnClickListener) this);
            btnYear.setOnClickListener((View.OnClickListener) this);
            btnSummary.setOnClickListener((View.OnClickListener) this);
            llDownvote.setOnClickListener((View.OnClickListener) this);
            llUpvote.setOnClickListener((View.OnClickListener) this);
            ib_Upvote.setOnClickListener((View.OnClickListener) this);
            ib_Downvote.setOnClickListener((View.OnClickListener) this);
            ib_Comment.setOnClickListener((View.OnClickListener) this);
            llComment.setOnClickListener((View.OnClickListener) this);
        }

        //DO something when view is clicked on, based on view's id
        @Override
        public void onClick(View v) {
            String clickedBtnText;

            int downvotes, upvotes;
            Note clickedNote = new Note();
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                clickedNote = mDataset.get(pos);
            }
            String clickedNoteDocumentID = clickedNote.getDocumentID();
            switch (v.getId()) {

                case R.id.llNoteTopBar:
                    checkAndGoToProfile(clickedNote);
                    break;

                case R.id.profile_image:
                    checkAndGoToProfile(clickedNote);
                    break;

                case R.id.llBookmark:
                    openAddToOrCreateDialog(clickedNoteDocumentID);
                    break;

                case R.id.btn_subject:
                    clickedBtnText = btnSubject.getText().toString();
                    goToSearchResultsByTag(clickedBtnText);
                    break;

                case R.id.btn_category:
                    clickedBtnText = btnCategory.getText().toString();
                    goToSearchResultsByTag(clickedBtnText);
                    break;

                case R.id.btn_year:
                    clickedBtnText = btnYear.getText().toString();
                    goToSearchResultsByTag(clickedBtnText);
                    break;
                case R.id.btn_summarry:
                    clickedBtnText = btnSummary.getText().toString();
                    goToSearchResultsByTag(clickedBtnText);
                    break;

                case R.id.llUpvote:
                    checkUpvotesOrDownvotes("Upvotes", clickedNote, ib_Upvote, tv_Upvote, ib_Downvote, tv_Downvote);
                    break;

                case R.id.ib_Upvote:
                    checkUpvotesOrDownvotes("Upvotes", clickedNote, ib_Upvote, tv_Upvote, ib_Downvote, tv_Downvote);
                    break;

                case R.id.llDownvote:
                    checkUpvotesOrDownvotes("Downvotes", clickedNote, ib_Upvote, tv_Upvote, ib_Downvote, tv_Downvote);
                    break;

                case R.id.ib_Downvote:
                    checkUpvotesOrDownvotes("Downvotes", clickedNote, ib_Upvote, tv_Upvote, ib_Downvote, tv_Downvote);
                    break;
                case R.id.ib_Comment:
                    goToComments(clickedNoteDocumentID, Integer.parseInt(tv_Comment.getText().toString()));
                    break;
                case R.id.llComment:
                    goToComments(clickedNoteDocumentID, Integer.parseInt(tv_Comment.getText().toString()));
                    break;
                default:
                    break;
            }
        }
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
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        Note n = mDataset.get(position);
        //Set date format to timeAgo
        Log.d(TAG, "ERROR " + n.getDatePosted());
        long time = Long.valueOf(TimeUtility.getDateFromDateTime(n.getDatePosted()));//2016-09-01 15:57:20 pass your date here
        String timeStr = TimeUtility.timeAgo(time / 1000);

        holder.tvUsername.setText(n.getUsername());
        holder.tvPostTimeDetail.setText("Posted " + timeStr);
        holder.tvNoteTitle.setText(n.getNoteTitle());
        holder.tvNoteDesc.setText(n.getNoteDescription());
        holder.tv_Comment.setText(n.getComments() + "");

        //Set colours of upvote/downvote
        String upvote, downvote;
        Activity main = new MainActivity();
        holder.ib_Upvote.setColorFilter(ContextCompat.getColor(context, R.color.post_Grey));
        if (n.getUpvote() == null) {
            upvote = 0 + "";
        } else {
            upvote = (n.getUpvote()).size() + "";
            if ((n.getUpvote()).containsKey(mFirebaseUser.getEmail())) {
                holder.ib_Upvote.setColorFilter(ContextCompat.getColor(context, R.color.profile_LightBlue));
            }
        }

        holder.ib_Downvote.setColorFilter(ContextCompat.getColor(context, R.color.post_Grey));
        if (n.getDownvote() == null) {
            downvote = 0 + "";
        } else {
            downvote = (n.getDownvote()).size() + "";
            if ((n.getDownvote()).containsKey(mFirebaseUser.getEmail())) {
                holder.ib_Downvote.setColorFilter(ContextCompat.getColor(context, R.color.profile_LightBlue));
            }
        }

        //Set text of upvote/downvote
        holder.tv_Upvote.setText((upvote));
        holder.tv_Downvote.setText(downvote);


        //Set text of tags
        for (int i = 0; i < 4; i++) {
            String tagToCheck = (String) (n.getTags().keySet().toArray()[i]); //get string from key array of tags hashmap

            if ((Arrays.asList((Tag.Subjects)).contains(tagToCheck))) { //Get list of all subjects from tag class
                holder.btnSubject.setText(tagToCheck); //Math
            } else if ((Arrays.asList((Tag.YearsOfStudy)).contains(tagToCheck))) {
                holder.btnYear.setText(tagToCheck); //Secondary 1
            } else if ((Arrays.asList((Tag.MaterialTypes)).contains(tagToCheck))) {
                holder.btnCategory.setText(tagToCheck); //Notes
            } else {
                holder.btnSummary.setText(tagToCheck); //Math + Secondary 1
            }
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

    //region comments
    private void goToComments(String clickedNoteDocumentID, int num) {
        Intent i = new Intent(context, CommentsActivity.class);
        i.putExtra("passedNoteID", clickedNoteDocumentID);
        i.putExtra("passedCommentsCount", num);
        context.startActivity(i);
    }
    //endregion

    //region upvote/downvote
    public void checkUpvotesOrDownvotes(String type, Note clickedNote, ImageButton buttonUpvote, TextView countUpvote, ImageButton buttonDownvote, TextView countDownvote) {
        String email = mFirebaseUser.getEmail();
        HashMap<String, Boolean> votes, otherVotes;
        String docId = clickedNote.getDocumentID();
        String downvote = (clickedNote.getDownvote()).size() + "";
        String upvote = (clickedNote.getUpvote()).size() + "";
        switch (type) {
            case "Upvotes":
                votes = clickedNote.getUpvote();
                otherVotes = clickedNote.getDownvote();
                if (votes == null) {
                    if (otherVotes.containsKey(email)) {
                        downvote = (otherVotes.size() - 1) + "";
                        buttonDownvote.setColorFilter(ContextCompat.getColor(context, R.color.post_Grey));
                        otherVotes.remove(email);
                        updateUpvotesOrDownvotes("Downvotes", otherVotes, docId);
                    }
                    upvote = 1 + "";
                    buttonUpvote.setColorFilter(ContextCompat.getColor(context, R.color.profile_LightBlue));
                    votes.put(email, true);
                } else {
                    if (votes.containsKey(email)) {
                        upvote = (votes.size() - 1) + "";
                        buttonUpvote.setColorFilter(ContextCompat.getColor(context, R.color.post_Grey));
                        votes.remove(email);
                    } else {
                        if (otherVotes.containsKey(email)) {
                            downvote = (otherVotes.size() - 1) + "";
                            buttonDownvote.setColorFilter(ContextCompat.getColor(context, R.color.post_Grey));
                            otherVotes.remove(email);
                            updateUpvotesOrDownvotes("Downvotes", otherVotes, docId);
                        }
                        upvote = (votes.size() + 1) + "";
                        buttonUpvote.setColorFilter(ContextCompat.getColor(context, R.color.profile_LightBlue));
                        votes.put(email, true);
                    }
                }
                countDownvote.setText(downvote);
                countUpvote.setText(upvote);
                updateUpvotesOrDownvotes(type, votes, docId);
                break;

            case "Downvotes":
                votes = clickedNote.getDownvote();
                otherVotes = clickedNote.getUpvote();
                if (votes == null) {
                    if (otherVotes.containsKey(email)) {
                        upvote = (otherVotes.size() - 1) + "";
                        buttonUpvote.setColorFilter(ContextCompat.getColor(context, R.color.post_Grey));
                        otherVotes.remove(email);
                        updateUpvotesOrDownvotes("Upvotes", otherVotes, docId);
                    }
                    downvote = 1 + "";
                    buttonDownvote.setColorFilter(ContextCompat.getColor(context, R.color.profile_LightBlue));
                    votes.put(email, true);
                } else {
                    if (votes.containsKey(email)) {
                        downvote = (votes.size() - 1) + "";
                        buttonDownvote.setColorFilter(ContextCompat.getColor(context, R.color.post_Grey));
                        votes.remove(email);
                    } else {
                        if (otherVotes.containsKey(email)) {
                            upvote = (otherVotes.size() - 1) + "";
                            buttonUpvote.setColorFilter(ContextCompat.getColor(context, R.color.post_Grey));
                            otherVotes.remove(email);
                            updateUpvotesOrDownvotes("Upvotes", otherVotes, docId);
                        }
                        downvote = (votes.size() + 1) + "";
                        buttonDownvote.setColorFilter(ContextCompat.getColor(context, R.color.profile_LightBlue));
                        votes.put(email, true);
                    }
                }
                countDownvote.setText(downvote);
                countUpvote.setText(upvote);
                updateUpvotesOrDownvotes(type, votes, docId);
                break;
        }
    }

    public void updateUpvotesOrDownvotes(String type, HashMap<String, Boolean> votes, String docId) {
        FirebaseFirestore.getInstance().collection("Notes").document(docId).update(type, votes)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "UPVOTED SUCCESS");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NotNull Exception e) {
                        Log.d(TAG, "UPVOTED FAILED");
                    }
                });
    }
    //endregion

    //region goToSearchResultsByTag
    //GOTO Search Results Page when click on any Tag Button
    public void goToSearchResultsByTag(String clickedButtonText) {
        ArrayList<String> passedSearchQuery = new ArrayList<>();
        passedSearchQuery.add(clickedButtonText);
        Intent i = new Intent(context, SearchResultsActivity.class);
        i.putExtra("passedSearchQuery", passedSearchQuery);
        context.startActivity(i);
    }
    //endregion

    //region profile
    //CHECK whether user is current or not, then go to profile page based on that
    public void checkAndGoToProfile(Note clickedNote) {
        Note note = clickedNote;
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userEmail.equals(note.getEmail())) {
            goToPage(note, "ProfileActivity");
            Log.d(TAG, "checkAndGoToProfile: is current " + note.getUsername() + " " + note.getEmail() + " " + userEmail);
        } else {
            goToPage(note, "ProfileActivity_otheruser");
            Log.d(TAG, "checkAndGoToProfile: is not current " + note.getUsername() + " " + note.getEmail() + " " + userEmail);
        }
    }

    //GOTO current/other user's profile page based on activityname passed in (ONCLICK Username/Img)
    public void goToPage(Note passedNote, String activityName) {
        String ActivityName = activityName;
        Note PassedNote = passedNote;
        switch (ActivityName) {

            case "ProfileActivity":
                context.startActivity(new Intent(context, ProfileActivity.class));
                break;

            case "ProfileActivity_otheruser":
                Intent i = new Intent(context, ProfileActivity_otheruser.class);
                i.putExtra("Email", PassedNote.getEmail());
                i.putExtra("Username", PassedNote.getUsername());
                context.startActivity(i);
                break;

            default:
                break;
        }
    }
    //endregion

    //region createNotebook
    //VERY LONG METHOD TO CREATE NOTEBOOK/ADD TO EXISTING NOTEBOOK DIALOG
    private void openAddToOrCreateDialog(String noteDocumentID) {
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.d("TAG", "NOTEBOOK: email " + userEmail);
        notebooks = new ArrayList<Notebook>();
        List<String> notebookNames = new ArrayList<String>();

        FirebaseFirestore.getInstance().collection("Notebooks").whereEqualTo("Email", userEmail).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            Log.d("TAG", "NOTEBOOK: task works " + userEmail);
                            for (DocumentSnapshot document : task.getResult()) {
                                notebookName = document.getString("NotebookName");
                                notebookNotes = (List<String>) document.get("NotebookNotes");
                                notebookDocumentID = document.getId();

                                Notebook n = new Notebook(userEmail, notebookName, notebookNotes, notebookDocumentID);
                                notebooks.add(n);

                                notebookNames.add(notebookName);

                                Log.d("TAG", "NOTEBOOK: document " + notebookName);
                            }

                            //SHOW Add to/Create Notebook Dialog
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            LayoutInflater inflater = LayoutInflater.from(context);
                            View convertView = (View) inflater.inflate(R.layout.dialog_create_addto_notebook, null);
                            alertDialog.setView(convertView);
                            ConstraintLayout cl = (ConstraintLayout) convertView.findViewById(R.id.clCreateNotebook);
                            ListView lv = (ListView) convertView.findViewById(R.id.lvNotebook);
                            if (notebookNames == null) {
                                lv.setVisibility(View.GONE);

                            } else {
                                if (notebookNames.size() == 0) {
                                    lv.setVisibility(View.GONE);
                                } else {
                                    //Populate Listview if user got notebooks
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.currentuser_notebook_item, R.id.tvNotebookName, notebookNames);
                                    adapter.notifyDataSetChanged();
                                    lv.setAdapter(adapter);
                                    Utility.setListViewHeightBasedOnChildren(lv); //SET MAX HEIGHT OF LISTVIEW TO 6 LV ITEMS
                                }
                            }
                            alertDialog.setCancelable(true);
                            alert = alertDialog.show();


                            //CLICK Add to Existing Notebook
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Notebook notebook = notebooks.get(position);
                                    String notebookName = notebook.getNotebookName();
                                    String notebookDocID = notebook.getNotebookDocumentID();
                                    List<String> notesList = notebook.getNotebookNotes();
                                    notesList.add(noteDocumentID);
                                    FirebaseFirestore.getInstance()
                                            .collection("Notebooks")
                                            .document(notebookDocID)
                                            .update("NotebookNotes", notesList);
                                    Toast.makeText(context, "Added to " + notebookName + "!",
                                            Toast.LENGTH_SHORT).show();
                                    alert.dismiss();
                                }
                            });

                            //CLICK Create Notebook
                            cl.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.dismiss();
                                    //OPEN create notebook dialog
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                                    LayoutInflater inflater = LayoutInflater.from(context);
                                    View convertView = (View) inflater.inflate(R.layout.dialog_create_notebook, null);
                                    EditText etNotebookName = (EditText) convertView.findViewById(R.id.et_NotebookName);
                                    alertDialog.setView(convertView)
                                            .setTitle("Create Notebook")
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String notebookName = "";
                                                    notebookName = etNotebookName.getText().toString();

                                                    if (notebookName.equals("")) {
                                                        Toast.makeText(context, "Please enter a notebook name",
                                                                Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        Boolean notebookNameExists = false;
                                                        for (String nbName : notebookNames) {
                                                            if (nbName.equalsIgnoreCase(notebookName)) {
                                                                notebookNameExists = true;
                                                            }
                                                        }
                                                        if (notebookNameExists) {
                                                            Toast.makeText(context, "You already have a notebook named " + notebookName,
                                                                    Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            List<String> Notes = new ArrayList<String>();
                                                            Notes.add(noteDocumentID);
                                                            Map<String, Object> notebookas = new HashMap<>();
                                                            notebookas.put("Email", userEmail);
                                                            notebookas.put("NotebookName", notebookName);
                                                            notebookas.put("NotebookNotes", Notes);
                                                            DocumentReference dr = FirebaseFirestore.getInstance().collection("Notebooks").document();
                                                            dr.set(notebookas).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d(TAG, "DocumentSnapshot added with ID: " + dr.getId());
                                                                    Toast.makeText(context, "Notebook Created!",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NotNull Exception e) {
                                                                    Log.w(TAG, "Eroor adding document", e);
                                                                    Toast.makeText(context, "Something went wrong when creating your notebook",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                        }

                                                    }
                                                }
                                            });


                                    alertDialog.setCancelable(true);
                                    AlertDialog createAlert = alertDialog.show();
                                    int textViewId = createAlert.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
                                    TextView tv = (TextView) createAlert.findViewById(textViewId);
                                    tv.setTextColor(context.getResources().getColor(R.color.Dark_Grey));
                                }
                            });


                        } else {
                            //Show create notebook button only
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    //endregion
}

