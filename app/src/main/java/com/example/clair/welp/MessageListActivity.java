package com.example.clair.welp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clair.welp.Firebase.ChatFirestore;
import com.example.clair.welp.Firebase.MagicalNames;
import com.example.clair.welp.Firebase.NoteFirestore;
import com.example.clair.welp.Objects.Chat;
import com.example.clair.welp.Objects.Comment;
import com.example.clair.welp.Objects.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

public class MessageListActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    //region firebase auth stuff
    private FirebaseAuth fFirebaseAuth;
    private FirebaseAuth.AuthStateListener fAuthStateListener;
    FirebaseUser mFirebaseUser;
    ChatFirestore f;
    @BindView(R.id.edittext_chatbox)
    EditText etChat;
    @BindView(R.id.button_chatbox_send)
    ImageButton ibChat;

    CollectionReference collectionref = FirebaseFirestore.getInstance().collection("Chats");
    List<Chat> chats;
    MagicalNames magicalNames = new MagicalNames();

    String passedSendingEmail, passedSendingUsername;
    ChatFirestore chatFirestore = new ChatFirestore();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        ButterKnife.bind(this);


        //get Extras
        passedSendingEmail = getIntent().getExtras().getString("passedSendingEmail");
        passedSendingUsername = getIntent().getExtras().getString("passedSendingUsername");


        //Title and backbutton
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(passedSendingUsername);


        //set chat adapter
        fFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = fFirebaseAuth.getCurrentUser();
        mMessageRecycler = (RecyclerView) findViewById(R.id.recyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, chats);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);

        //Send Message
        ibChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chatString = (etChat.getText().toString()).trim();
                if (chatString.equals("") || chatString == null) {
                    Toast.makeText(MessageListActivity.this, "Enter a Message", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(chatString);

                    etChat.setText("");
                    Toast.makeText(MessageListActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void UpdateList(List<Chat> n) {
        mMessageAdapter.deleteEverything();
        mMessageAdapter.addAllItems(n);
        mMessageRecycler.getLayoutManager().smoothScrollToPosition(mMessageRecycler, new RecyclerView.State(), mMessageRecycler.getAdapter().getItemCount());
    }

    private void sendMessage(String chatString) {
        //CHANGE DATE FORMAT
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String dateSent = dateFormat.format(date);

        Chat chat = new Chat(chatString, passedSendingEmail, passedSendingUsername, mFirebaseUser.getEmail(), mFirebaseUser.getDisplayName(), dateSent);
        chatFirestore.add(chat);

        MessageListActivity r = this;

        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        if (imm.isActive()){
            // HIDE keyboard
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }

        f = new ChatFirestore(r, mFirebaseUser.getEmail(), passedSendingEmail);
        createNotification();
    }

    //region create Notification on follow
    public void createNotification() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateSent = dateFormat.format(new Date());
        String currentUsername = mFirebaseUser.getDisplayName();

        DocumentReference drNotif = FirebaseFirestore.getInstance().collection("Notifications").document();
        Map<String, Object> notification = new HashMap<>();
        notification.put("ReceivingEmail", passedSendingEmail);
        notification.put("SendingUsername", currentUsername);
        notification.put("SendingEmail", mFirebaseUser.getEmail());
        notification.put("NotificationType", "Discussion");
        notification.put("NotificationString", currentUsername+" has sent you a message");
        notification.put("DateSent", dateSent);
        drNotif.set(notification);
    }


    @Override
    protected void onStart() {
        super.onStart();
        MessageListActivity r = this;
        f = new ChatFirestore(r, mFirebaseUser.getEmail(), passedSendingEmail);
    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if(fAuthStateListener!=null){
//            fFirebaseAuth.removeAuthStateListener(fAuthStateListener);
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        fFirebaseAuth.addAuthStateListener(fAuthStateListener);
//    }


    @Override
    protected void onResume() {
        super.onResume();
        MessageListActivity r = this;
        f = new ChatFirestore(r, mFirebaseUser.getEmail(), passedSendingEmail);
    }

    //UP BUTTON (BACK)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //GOTO : Discussion Activity
        if(id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }
}
