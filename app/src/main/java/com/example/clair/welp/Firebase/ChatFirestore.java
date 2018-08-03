package com.example.clair.welp.Firebase;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.example.clair.welp.DiscussionsActivity;
import com.example.clair.welp.FragmentOtherUserPosts;
import com.example.clair.welp.MainActivity;
import com.example.clair.welp.MessageListActivity;
import com.example.clair.welp.Objects.Chat;
import com.example.clair.welp.Objects.Comment;
import com.example.clair.welp.Objects.Note;
import com.firebase.client.annotations.NotNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ChatFirestore {

    private String message, receivingEmail, receivingUsername, sendingEmail, sendingUsername, dateSent;
    CollectionReference collectionRef = FirebaseFirestore.getInstance().collection("Chats");
    Map<String, Object> chatas = new HashMap<>();
    MagicalNames magicalNames = new MagicalNames();
    private FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    List<Chat> chats;

    public ChatFirestore(){}

    public ChatFirestore(DiscussionsActivity r) {
        final DiscussionsActivity reference = r;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        collectionRef
                .whereEqualTo("ReceivingEmail", mFirebaseUser.getEmail())
                .get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        chats = new ArrayList<>();
                        List<String> sendingEmails = new ArrayList<>();

                        if (task.isSuccessful()) {
                            //Reverse list so sorted by most recent
                            List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                            myListOfDocuments = reverseListOrder(myListOfDocuments);

                            for (DocumentSnapshot document : myListOfDocuments) {

                                String sendingEmail =document.getString(magicalNames.getChats_Column_SendingEmail());

                                if(!sendingEmails.contains(sendingEmail)){
                                    sendingEmails.add(sendingEmail);
                                    chats.add(getChatFromDocumentSnapshot(document));
                                }

                                reference.UpdateList(chats);
                                reference.setErrorImage();
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

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

    public ChatFirestore(MessageListActivity r, String currentUserEmail, String passedSendingEmail) {
        final MessageListActivity reference = r;
        chats = new ArrayList<>();
        collectionRef
                .whereEqualTo("SendingEmail", currentUserEmail)
                .whereEqualTo("ReceivingEmail", passedSendingEmail)
                .get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (final DocumentSnapshot document : task.getResult()) {
                                chats.add(getChatFromDocumentSnapshot(document));
                                Log.d(TAG, "CHAT SOC 1");
//                                reference.UpdateList(chats);
                                Collections.sort(chats, new Comparator<Chat>(){
                                    public int compare(Chat obj1, Chat obj2) {
                                        // ## Ascending order
                                        return obj1.getDateSent().compareToIgnoreCase(obj2.getDateSent()); // To compare string values
                                        // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values

                                        // ## Descending order
                                        // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                                        // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
                                    }

                                });

                                reference.UpdateList(chats);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        collectionRef
                .whereEqualTo("ReceivingEmail", currentUserEmail)
                .whereEqualTo("SendingEmail", passedSendingEmail)
                .get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (final DocumentSnapshot document : task.getResult()) {
                                chats.add(getChatFromDocumentSnapshot(document));
                                Log.d(TAG, "CHAT SOC 2");
                                Collections.sort(chats, new Comparator<Chat>(){
                                    public int compare(Chat obj1, Chat obj2) {
                                        // ## Ascending order
                                        return obj1.getDateSent().compareToIgnoreCase(obj2.getDateSent()); // To compare string values
                                        // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values

                                        // ## Descending order
                                        // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                                        // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
                                    }

                                });

                                reference.UpdateList(chats);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    public Chat getChatFromDocumentSnapshot(DocumentSnapshot document) {

        message = document.getString(magicalNames.getChats_Column_Message());
        receivingEmail = document.getString(magicalNames.getChats_Column_ReceivingEmail());
        receivingUsername = document.getString(magicalNames.getChats_Column_ReceivingUsername());
        sendingEmail = document.getString(magicalNames.getChats_Column_SendingEmail());
        sendingUsername = document.getString(magicalNames.getChats_Column_SendingUsername());

        dateSent = document.getString(magicalNames.getChats_Column_DateSent());

        Chat c = new Chat(message, receivingEmail, receivingUsername, sendingEmail, sendingUsername, dateSent);
        return c;
    }

    public void add(Chat n) {
        chatas.put(magicalNames.getChats_Column_Message(), n.getMessage());
        chatas.put(magicalNames.getChats_Column_ReceivingEmail(), n.getReceivingEmail());
        chatas.put(magicalNames.getChats_Column_ReceivingUsername(), n.getReceivingUsername());
        chatas.put(magicalNames.getChats_Column_SendingEmail(), n.getSendingEmail());
        chatas.put(magicalNames.getChats_Column_SendingUsername(), n.getSendingUsername());
        chatas.put(magicalNames.getChats_Column_DateSent(), n.getDateSent());

        DocumentReference dr = collectionRef.document();

        String date = (n.getDateSent()).replace("/", "");
        date = date.replace(" ", "");
        String docID = date + dr.getId();


        collectionRef.document(docID).set(chatas).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + collectionRef.document().getId());
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
