package com.example.clair.welp;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import javax.security.auth.Subject;

public class SignUpSubject extends AppCompatActivity {
    GridView gvSubjects;
    ArrayList<String> Subjects;
    ArrayList<Integer> SubjectImgs;
    Tag tag;
    Button btnNext;
    boolean selected=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_subject);

        tag=new Tag();

        Subjects=new ArrayList<String>(Arrays.asList(tag.getSubject()));
        SubjectImgs=new ArrayList<Integer>(Arrays.asList(tag.getImg()));

        btnNext=findViewById(R.id.btn_Next);
        gvSubjects=findViewById(R.id.gvSubjects);
        gvSubjects.setAdapter(new SubjectAdapter(this));
        for (String subject:tag.getSubjects()) {
            Subjects.add(subject);
        }
        for (Integer subjectImg:tag.getImg()) {
            SubjectImgs.add(subjectImg);
        }

        gvSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(selected==false){
                    selected=true;
                    view.setBackgroundColor(getResources().getColor(R.color.selectSubClicked));
                    btnNext.setEnabled(true);
                }
                else{
                    selected=false;
                    view.setBackgroundColor(getResources().getColor(R.color.veriWhite));
                }
            }
        });
    }
    public class SubjectAdapter extends BaseAdapter {
        Context _context;
        public SubjectAdapter(Context context)

        {
            _context=context;
        }

        @Override
        public int getCount() {
            return Subjects.size()-1;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v;
            if(view==null){
                LayoutInflater li=getLayoutInflater();
                v=li.inflate(R.layout.pick_subject_template,null);
                TextView tv=v.findViewById(R.id.tv_subjects);
                ImageView iv=v.findViewById(R.id.iv_subjectsIMG);

                tv.setText(Subjects.get(i+1));

                iv.setImageResource(SubjectImgs.get(i));
            }
            else {
                v=view;
            }
            return v;
        }
    }

    /*
    public class MultiChoiceModeListener implements GridView.MultiChoiceModeListener{

        @Override
        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

        }

    }
    */
}
