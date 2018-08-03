package com.example.clair.welp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clair.welp.Firebase.MagicalNames;
import com.example.clair.welp.Objects.Chat;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DiscussionsAdapter extends RecyclerView.Adapter<DiscussionsAdapter.ViewHolder> {
    public List<Chat> mDataset;
    Context context;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MagicalNames magicalNames = new MagicalNames();



    public DiscussionsAdapter() {
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvOtherUserName, tvMessage, tvMessageTime;
        ConstraintLayout clDiscussionItem;


        public ViewHolder(View v) {
            super(v);
            tvOtherUserName = v.findViewById(R.id.tvOtherUserName);
            tvMessage = v.findViewById(R.id.tvMessage);
            tvMessageTime = v.findViewById(R.id.tvMessageTime);
            clDiscussionItem = v.findViewById(R.id.clDiscussionItem);


            //SET onClickListeners for views in note_template here
            clDiscussionItem.setOnClickListener((View.OnClickListener) this);
        }

        //DO something when view is clicked on, based on view's id
        @Override
        public void onClick(View v) {
            String clickedBtnText;

            int downvotes, upvotes;
            Chat clickedChat = new Chat();
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                clickedChat = mDataset.get(pos);
            }
            switch (v.getId()) {

                case R.id.clDiscussionItem:
                    Intent i = new Intent(context, MessageListActivity.class);
                    i.putExtra("passedSendingEmail", clickedChat.getSendingEmail());
                    i.putExtra("passedSendingUsername", clickedChat.getSendingUsername());
                    context.startActivity(i);
                    break;

                default:

                    break;
            }
        }
    }


    public DiscussionsAdapter(Context context) {
        this.context = context;
        this.mDataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public DiscussionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_discussion_item, parent, false);
        return new DiscussionsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscussionsAdapter.ViewHolder holder, int position) {
        Chat n = mDataset.get(position);
        //Set date format to timeAgo
        long time = Long.valueOf(TimeUtility.getDateFromDateTime(n.getDateSent()));//2016-09-01 15:57:20 pass your date here
        String timeStr = TimeUtility.timeAgo(time / 1000);

        holder.tvOtherUserName.setText(n.getSendingUsername());
        holder.tvMessageTime.setText(timeStr);
        holder.tvMessage.setText(n.getMessage());


    }


    @Override
    public int getItemCount() {
        if (mDataset == null) return 0;
        else return mDataset.size();
    }

    public void addItem(Chat t) {
        mDataset.add(t);
        notifyItemChanged(mDataset.size() - 1);
    }

    public void addAllItems(List<Chat> chatList) {
        for (Chat chat : chatList) {
            addItem(chat);
        }
    }

    public void deleteEverything() {
        if (mDataset != null) mDataset.clear();
    }

}
