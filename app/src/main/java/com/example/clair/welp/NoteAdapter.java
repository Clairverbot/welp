package com.example.clair.welp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.clair.welp.MainActivity.FakeDataGenerator;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    public List<Note> mDataset;
    Context context;

    public NoteAdapter(){}
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvUsername,tvPostTimeDetail,tvNoteTitle,tvNoteDesc,tv_Upvote,tv_Downvote;
        public ViewHolder(View v) {
            super(v);
            tvUsername = v.findViewById(R.id.tvUsername);
            tvPostTimeDetail = v.findViewById(R.id.tvPostTimeDetail);
            tvNoteTitle = v.findViewById(R.id.tvNoteTitle);
            tvNoteDesc = v.findViewById(R.id.tvNoteDesc);
            tv_Upvote = v.findViewById(R.id.tv_Upvote);
            tv_Downvote = v.findViewById(R.id.tv_Downvote);

        }
    }
    public NoteAdapter(Context context){
        this.context=context;
        this.mDataset=new ArrayList<>();
    }
    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View ciew=LayoutInflater.from(parent.getContext()).inflate(R.layout.note_template,parent,false);
        return new ViewHolder(ciew);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
        final Note n=mDataset.get(position);
        holder.tvUsername.setText(n.getUsername());
        holder.tvPostTimeDetail.setText(n.getDatePosted());
        holder.tvNoteTitle.setText(n.getNoteTitle());
        holder.tvNoteDesc.setText(n.getNoteDescription());
        String upvote= n.getUpvote()+"";
        String downvote=n.getDownvote()+"";
        holder.tv_Upvote.setText((upvote));
        holder.tv_Downvote.setText(downvote);


    }

    @Override
    public int getItemCount() {
        if(mDataset==null) return 0;
        else return mDataset.size();
    }
    public void addItem (Note t) {
        mDataset.add(t);
        notifyItemChanged(mDataset.size() - 1);
    }

    public void addAllItems(List<Note> notesList) {
        for (Note note : notesList) {
            addItem(note);
        }
    }

}

