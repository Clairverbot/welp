package com.example.clair.welp.Firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.clair.welp.Objects.Comment;
import com.example.clair.welp.Objects.Note;
import com.firebase.client.annotations.NotNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CommentFirestore {
    CollectionReference commentCollectionRef= FirebaseFirestore.getInstance().collection("Comments");
    CollectionReference noteCollectionRef = FirebaseFirestore.getInstance().collection("Notes");
    Map<String, Object> commentas = new HashMap<>();
    MagicalNames magicalNames = new MagicalNames();
    int commentsCount;
    public CommentFirestore() {
    }

    public void add(Comment n) {
        commentas.put(magicalNames.getComments_Column_Email(), n.getEmail());
        commentas.put(magicalNames.getComments_Column_Username(), n.getUsername());
        commentas.put(magicalNames.getComments_Column_Comment(), n.getComment());
        commentas.put(magicalNames.getComments_Column_DatePosted(), n.getDatePosted());
        commentas.put(magicalNames.getComments_Column_NoteID(), n.getNoteID());

        DocumentReference dr = commentCollectionRef.document();

        String date = (n.getDatePosted()).replace("/", "");
        date = date.replace(" ", "");
        String docID = date + dr.getId();


        commentCollectionRef.document(docID).set(commentas).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + commentCollectionRef.document().getId());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NotNull Exception e) {
                        Log.w(TAG, "Eroor adding document", e);
                    }
                });

        noteCollectionRef.document(n.getNoteID()).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                Long Comments = document.getLong(magicalNames.getNotes_Column_Comment());
                                commentsCount = null == Comments ? 0 : Comments.intValue();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        noteCollectionRef.document(n.getNoteID()).update("Comments", (commentsCount + 1));
    }
}
