package com.example.clair.welp.Firebase;

import android.util.Log;

import com.example.clair.welp.Objects.User;
import com.firebase.client.annotations.NotNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class UserFirestore {
    CollectionReference collectionReference= FirebaseFirestore.getInstance().collection("Users");
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    List<User> users;
    Map<String,Object> userMap=new HashMap<>();
    MagicalNames magicalNames = new MagicalNames();

    public void add(User user){
        userMap.put(magicalNames.getUsers_Column_Subjects(),user.getSubjects());
        userMap.put(magicalNames.getUsers_Column_Username(),user.getUsername());
        userMap.put(magicalNames.getUsers_Column_YearOfStudy(),user.getYearOfStudy());

        collectionReference.document(user.getEmail()).set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + collectionReference.document().getId());
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
