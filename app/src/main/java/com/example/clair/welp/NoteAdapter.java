package com.example.clair.welp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
        TextView tvUsername, tvPostTimeDetail, tvNoteTitle, tvNoteDesc, tv_Upvote, tv_Downvote;
        Button btnSubject, btnYear, btnCategory, btnSummary;
        LinearLayout llUsernameTime, llBookmark;
        ConstraintLayout llNoteTopBar;
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
            llNoteTopBar = v.findViewById(R.id.llNoteTopBar);
            //SET onClickListeners for views in note_template here
            llNoteTopBar.setOnClickListener((View.OnClickListener) this);
            //llUsernameTime.setOnClickListener((View.OnClickListener) this);
            //profile_image.setOnClickListener((View.OnClickListener) this);
            llBookmark.setOnClickListener((View.OnClickListener) this);
        }

        //DO something when view is clicked on, based on view's id
        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            switch (v.getId()) {

                case R.id.llNoteTopBar:
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
//                        String bookmarkEmail = clickedNote.getEmail();
                        String clickedNoteDocumentID = clickedNote.getDocumentID();
                        openAddToOrCreateDialog(clickedNoteDocumentID);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    //VERY LONG METHOD TO CREATE NOTEBOOK
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

                            //Show Add to/Create Notebook Dialog
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
                                    //Open create notebook dialog
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
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

        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

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

