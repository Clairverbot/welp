package com.example.clair.welp.Firebase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.example.clair.welp.Firebase.MagicalNames;
import com.example.clair.welp.FragmentCurrentUserPosts;
import com.example.clair.welp.MainActivity;
import com.example.clair.welp.Objects.Note;
import com.example.clair.welp.R;
import com.firebase.client.annotations.NotNull;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class NoteFirestore {
    public NoteFirestore() {
    }

    CollectionReference collectionref = FirebaseFirestore.getInstance().collection("Notes");
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Note> notes;
    Map<String, Object> notas = new HashMap<>();
    MagicalNames magicalNames = new MagicalNames();
    String email, username,userIMG,noteTitle,noteDescription,resourceURL;
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    String datePosted,deleted;
    List<String>tags;
    int upvote,downvote;

    public NoteFirestore(MainActivity r) {
        final MainActivity reference = r;

        collectionref.orderBy("DatePosted").get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        notes = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (final DocumentSnapshot document : task.getResult()) {
                                email = document.getString(magicalNames.getNotes_Column_Email());
                                noteTitle = document.getString(magicalNames.getNotes_Column_NoteTitle());
                                noteDescription = document.getString(magicalNames.getNotes_Column_NoteDescription());
                                resourceURL = document.getString(magicalNames.getNotes_Column_ResourceURL());
                                Date DatePosted=document.getDate(magicalNames.getNotes_Column_DatePosted());
                                datePosted=DatePosted!=null?sdf.format(DatePosted):null;
                                Date Deleted=document.getDate(magicalNames.getNotes_Column_Deleted());
                                deleted =Deleted!=null?sdf.format(Deleted):null;

                                tags = (List<String>)document.get(magicalNames.getNotes_Column_Tags());
                                //String[][] comments = {{document.getString(magicalNames.getNotes_Column_CommentUsername())}, {document.getString(magicalNames.getNotes_Column_Comment())}};
                                upvote = Integer.parseInt(document.getLong(magicalNames.getNotes_Column_Upvote()).toString());
                                downvote = Integer.parseInt(document.getLong(magicalNames.getNotes_Column_Downvote()).toString());

                                getUserInfo(email,noteTitle,noteDescription,resourceURL,datePosted,deleted,tags,upvote,downvote,reference);


                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public NoteFirestore(FragmentCurrentUserPosts r, String email, TextView error) {
        final FragmentCurrentUserPosts reference = r;
        final TextView tvError = error;
        CollectionReference collectionrefcurrentuser = FirebaseFirestore.getInstance().collection("Notes");

        collectionrefcurrentuser.whereEqualTo("Email", email).get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        notes = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String email = document.getString(magicalNames.getNotes_Column_Email());
                                String username = document.getString(magicalNames.getNotes_Column_Username());
                                String userIMG = document.getString(magicalNames.getNotes_Column_UserIMG());
                                String noteTitle = document.getString(magicalNames.getNotes_Column_NoteTitle());
                                String noteDescription = document.getString(magicalNames.getNotes_Column_NoteDescription());
                                String resourceURL = document.getString(magicalNames.getNotes_Column_ResourceURL());
                                Date datePosted = document.getDate(magicalNames.getNotes_Column_DatePosted());
                                Date deleted = document.getDate(magicalNames.getNotes_Column_Deleted());
                                //String[] tags = {document.getString(magicalNames.getNotes_Column_Tags())};
                                //String[][] comments = {{document.getString(magicalNames.getNotes_Column_CommentUsername())}, {document.getString(magicalNames.getNotes_Column_Comment())}};
                                int upvote = Integer.parseInt(document.getLong(magicalNames.getNotes_Column_Upvote()).toString());
                                int downvote = Integer.parseInt(document.getLong(magicalNames.getNotes_Column_Downvote()).toString());

                                Note n=new Note(email,username,userIMG,noteTitle,noteDescription,resourceURL,datePosted,deleted,null,null,upvote,downvote);
                                notes.add(n);
                            }
                            reference.UpdateList(notes);
                            if(notes.size() == 0){

                                tvError.setText("You have not posted anything yet.");
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
    public void getUserInfo(final String Email,final String noteTitle,final String noteDescription,final String resourceURL,final String datePosted,final String deleted,final List<String> tags,final int upvote,final int downvote,MainActivity reference){
        final MainActivity ref=reference;
        db.collection("Users").document(Email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    username = task.getResult().getString(magicalNames.getNotes_Column_Username());
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

                Note n=new Note(email,username,userIMG,noteTitle,noteDescription,resourceURL,datePosted,deleted,tags,upvote,downvote,null);
                notes.add(n);
                ref.UpdateList(notes);
            }
        });
    }

    public void add(Note n) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        Uri file=Uri.fromFile(new File(n.getResourceURL()));
        final Uri[] downloadUri = new Uri[1];
        StorageReference fileRef=storageRef.child(n.getResourceURL());

        UploadTask uploadTask=fileRef.putFile(file);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        final StorageReference ref = storageRef.child(n.getResourceURL());
        uploadTask = ref.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadUri[0] = task.getResult();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
        notas.put(magicalNames.getNotes_Column_Email(),n.getEmail());
        notas.put(magicalNames.getNotes_Column_NoteTitle(),n.getNoteTitle());
        notas.put(magicalNames.getNotes_Column_NoteDescription(),n.getNoteDescription());
        notas.put(magicalNames.getNotes_Column_ResourceURL(),downloadUri[0]);
        notas.put(magicalNames.getNotes_Column_DatePosted(),n.getDatePosted());
        notas.put(magicalNames.getNotes_Column_Tags(),n.getTags());


        collectionref.document().set(notas).addOnSuccessListener(new OnSuccessListener<Void>() {
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
