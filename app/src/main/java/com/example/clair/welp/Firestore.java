package com.example.clair.welp;

import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.client.annotations.NotNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Firestore {
    public Firestore(){}
/*
    CollectionReference collectionref = FirebaseFirestore.getInstance().collection("collection");
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    List<Note> notes;
    Map<String,Object> tasks=new HashMap<>();

    public Firestore(MainActivity r){
        final MainActivity reference = r;

        collectionref.get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        notes = new ArrayList<>();

                        if(task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String taskName = document.getString(ToDoListContract.ToDoEntry.COLUMN_NAME_TASKNAME);
                                String dueDate = document.getString(ToDoListContract.ToDoEntry.COLUMN_NAME_DUEDATE);
                                String status = document.getString(ToDoListContract.ToDoEntry.COLUMN_NAME_STATUS);

                                Note t = new ToDo(taskName, dueDate, status);
                                todo.add(t);
                            }

                            reference.UpdateList(todo);
                        }else{
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    public void add(ToDo todo){
        tasks.put(ToDoListContract.ToDoEntry.COLUMN_NAME_TASKNAME, todo.getTaskName());
        tasks.put(ToDoListContract.ToDoEntry.COLUMN_NAME_DUEDATE,todo.getTaskDueDate());
        tasks.put(ToDoListContract.ToDoEntry.COLUMN_NAME_STATUS,todo.getStatus());


        collectionref.document().set(tasks).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"DocumentSnapshot added with ID: "+ collectionref.document().getId());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NotNull Exception e) {
                        Log.w(TAG,"Eroor adding document",e);
                    }
                });


    }

*/
}
