package com.example.clair.welp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


import java.util.Date;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{
    public List<Note> mDataset;
    Context context;
    public NoteAdapter(){}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public String Username,NoteTitle,NoteDescription,ResourceURL;
        public Date DatePosted,Deleted  ;
        public String[] Tags,Upvote,Downvote;
        public String[][] Comments;

        public ViewHolder(View itemView) {
            super(itemView);
        }

    }
}
