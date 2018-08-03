package com.example.clair.welp.Firebase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.clair.welp.Firebase.MagicalNames;
import com.example.clair.welp.FragmentCurrentUserPosts;
import com.example.clair.welp.FragmentOtherUserPosts;
import com.example.clair.welp.MainActivity;
import com.example.clair.welp.NotebookActivity;
import com.example.clair.welp.Objects.Note;
import com.example.clair.welp.R;
import com.example.clair.welp.SearchResultsActivity;
import com.firebase.client.annotations.NotNull;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class NoteFirestore {
    public NoteFirestore() {
    }

    CollectionReference collectionref = FirebaseFirestore.getInstance().collection("Notes");
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Note> notes;
    Map<String, Object> notas = new HashMap<>();
    MagicalNames magicalNames = new MagicalNames();
    String email, username, userIMG, noteTitle, noteDescription, resourceURL,fileType, documentID;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String datePosted, deleted;
    HashMap<String, Boolean> tags, upvote, downvote;
    int comments;

    private List<DocumentSnapshot> reverseListOrder(List<DocumentSnapshot> task)
    {
        Iterator<DocumentSnapshot> it = task.iterator();
        List<DocumentSnapshot> document = new ArrayList<>();
        while (it.hasNext()) {
            document.add(0, it.next());
            it.remove();
        }
        return document;
    }



    public NoteFirestore(MainActivity r) {
        final MainActivity reference = r;
        //final ProgressBar pbSpinner = spinner;
        collectionref.orderBy("DatePosted", Query.Direction.DESCENDING).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        notes = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (final DocumentSnapshot document : task.getResult()) {
                                notes.add(getNoteFromDocumentSnapshot(document));
                                reference.UpdateList(notes);
                                //pbSpinner.setVisibility(View.GONE);
                            }
//                                });
//
//                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    public NoteFirestore(FragmentCurrentUserPosts r, String strEmail, TextView tvLoading) {
        final FragmentCurrentUserPosts reference = r;
        final TextView tvError = tvLoading;
        collectionref.whereEqualTo("Email", strEmail).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        notes = new ArrayList<>();

                        if (task.isSuccessful()) {
                            //Reverse list so sorted by most recent
                            List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                            for (final DocumentSnapshot document : reverseListOrder(myListOfDocuments)) {
                                notes.add(getNoteFromDocumentSnapshot(document));
                                reference.UpdateList(notes);

                                if (notes != null) {
                                    if (notes.size() == 0) {
                                        tvError.setText("You have not posted anything yet");
                                    } else {
                                        tvError.setText("");
                                    }
                                }
                                Log.d(TAG, "Reached Here: ");
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            tvError.setText("You have not posted anything yet");

                        }
                    }
                });


    }



    public NoteFirestore(FragmentOtherUserPosts r, String strEmail, TextView error) {
        final FragmentOtherUserPosts reference = r;
        final TextView tvError = error;
        String Email = strEmail;
        collectionref.whereEqualTo("Email", Email).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        notes = new ArrayList<>();

                        if (task.isSuccessful()) {
                            //Reverse list so sorted by most recent
                            List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                            for (DocumentSnapshot document : reverseListOrder(myListOfDocuments)) {
                                notes.add(getNoteFromDocumentSnapshot(document));
                                reference.UpdateList(notes);

                                if (notes != null) {
                                    if (notes.size() == 0) {
                                        tvError.setText(username + " has no posts yet");
                                    } else {
                                        tvError.setText("");
                                    }
                                } else {
                                    tvError.setText(username + " has no posts yet");
                                }

                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            tvError.setText(username + " has no posts yet");
                        }
                    }
                });
    }

    public NoteFirestore(NotebookActivity r, ArrayList<String> passedList) {
        final NotebookActivity reference = r;
        final ArrayList<String> listOfNoteIDs = passedList;
        ArrayList<Note> notes2 = new ArrayList<>();
        for (String noteID : listOfNoteIDs) {
            Log.d(TAG, "Note ID1:  " + noteID);
            db.collection("Notes").document(noteID).get().
                    addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                            if (task.isSuccessful()) {

                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    notes2.add(getNoteFromDocumentSnapshot(document));
                                    reference.UpdateList(notes2);
                                }

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    public NoteFirestore(SearchResultsActivity r, ArrayList<String> passedList) {
        final SearchResultsActivity reference = r;

        Query colRef = getQueryForSearchResults(passedList);

        notes = new ArrayList<>();
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    //Reverse list so sorted by most recent
                    List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                    for (DocumentSnapshot document : reverseListOrder(myListOfDocuments)) {
                        notes.add(getNoteFromDocumentSnapshot(document));
                        reference.UpdateList(notes);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    reference.UpdateList(notes);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
                reference.UpdateList(notes);
            }
        });

    }

    public Query getQueryForSearchResults(ArrayList<String> passedList){
        Query colRef = FirebaseFirestore.getInstance().collection("Notes");

        switch (passedList.size()) {
            case 1:
                colRef = FirebaseFirestore.getInstance().collection("Notes")
                        .whereEqualTo("Tags." + (passedList.get(0)).toString(), true);
                break;
            case 2:
                colRef = FirebaseFirestore.getInstance().collection("Notes")
                        .whereEqualTo("Tags." + (passedList.get(0)).toString(), true)  //e.g. "Tags.Math + Secondary 4" == true
                        .whereEqualTo("Tags." + (passedList.get(1)).toString(), true); //e.g. "Tags.Notes" == true
                break;
        }
        return colRef;
    }

    public Note getNoteFromDocumentSnapshot(DocumentSnapshot document) {

        email = document.getString(magicalNames.getNotes_Column_Email());
        username = document.getString(magicalNames.getNotes_Column_Username());
        noteTitle = document.getString(magicalNames.getNotes_Column_NoteTitle());
        noteDescription = document.getString(magicalNames.getNotes_Column_NoteDescription());
        resourceURL = document.getString(magicalNames.getNotes_Column_ResourceURL());

        datePosted = document.getString(magicalNames.getNotes_Column_DatePosted());
        Date Deleted = document.getDate(magicalNames.getNotes_Column_Deleted());
        deleted = Deleted != null ? sdf.format(Deleted) : null;
        Long Comments = document.getLong(magicalNames.getNotes_Column_Comment());
        comments = null == Comments ? 0 : Comments.intValue();
        tags = (HashMap<String, Boolean>) document.getData().get(magicalNames.getNotes_Column_Tags());
        //String[][] comments = {{document.getString(magicalNames.getNotes_Column_CommentUsername())}, {document.getString(magicalNames.getNotes_Column_Comment())}};

        upvote = (HashMap<String, Boolean>) document.getData().get(magicalNames.getNotes_Column_Upvote());
        downvote = (HashMap<String, Boolean>) document.getData().get(magicalNames.getNotes_Column_Downvote());
//        upvote = null == Upvote ? 0 : Upvote.intValue();
//        Long Downvote = document.getLong(magicalNames.getNotes_Column_Downvote());
//        downvote = null == Downvote ? 0 : Downvote.intValue();
        fileType=document.getString(magicalNames.getNotes_Column_FileType());
        documentID = document.getId();


        Note n = new Note(email, username, userIMG, noteTitle, noteDescription, resourceURL, datePosted, deleted, tags, upvote, downvote, comments,fileType, documentID);

        return n;
    }

// REMOVED CUS IT MAKES CALLING THE CORRECT NOTE WITH THE POSITION WRONG
//    public void getUserInfo(final String Email,final String noteTitle,final String noteDescription,final String resourceURL,final String datePosted,final String deleted,final List<String> tags,final int upvote,final int downvote,MainActivity reference){
//        final MainActivity ref=reference;
//       // final ProgressBar pbSpinner = spinner;
//
//    }


//    public String storage(Note n){
//        FirebaseStorage storage=FirebaseStorage.getInstance();
//        StorageReference storageRef=storage.getReference();
//        Uri file = Uri.fromFile(new File(n.getResourceURL()));
//        StorageReference riversRef = storageRef.child(n.getResourceURL());
//        UploadTask uploadTask = riversRef.putFile(Uri.parse("com.android.providers.downloads.documents/document/23.pdf"));
//
//        Uri file = Uri.fromFile(new File(n.getResourceURL()));
//        final Uri[] downloadUri = new Uri[1];
//        StorageReference fileRef = storageRef.child(n.getResourceURL());
//
//        UploadTask uploadTask = fileRef.putFile(file);
//
//        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                // ...
//                url[0] =taskSnapshot.getDownloadUrl().toString();
//            }
//        });
//        return url[0];
//    }

    public void add(Note n) {
//                Date datePosted= Calendar.getInstance().getTime();
        notas.put(magicalNames.getNotes_Column_Email(), n.getEmail());
        notas.put(magicalNames.getNotes_Column_NoteTitle(), n.getNoteTitle());
        notas.put(magicalNames.getNotes_Column_NoteDescription(), n.getNoteDescription());
        //notas.put(magicalNames.getNotes_Column_ResourceURL(),storage(n));
        notas.put(magicalNames.getNotes_Column_DatePosted(), n.getDatePosted());
        notas.put(magicalNames.getNotes_Column_Tags(), n.getTags());
        notas.put(magicalNames.getUsers_Column_Username(), n.getUsername());
        notas.put(magicalNames.getNotes_Column_Upvote(), n.getUpvote());
        notas.put(magicalNames.getNotes_Column_Downvote(), n.getDownvote());
        notas.put(magicalNames.getNotes_Column_ResourceURL(),n.getResourceURL());
        notas.put(magicalNames.getNotes_Column_FileType(),n.getFileType());

        DocumentReference dr = collectionref.document();

        String date = (n.getDatePosted()).replace("/", "");
        date = date.replace(" ", "");
        String docID = date + dr.getId();



        collectionref.document(docID).set(notas).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + collectionref.document().getId());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NotNull Exception e) {
                        Log.w(TAG, "Eroor adding document", e);
                    }
                });


    }


}
