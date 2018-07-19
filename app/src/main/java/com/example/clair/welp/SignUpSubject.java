package com.example.clair.welp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.support.constraint.ConstraintLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.clair.welp.Objects.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignUpSubject extends AppCompatActivity {
    GridView gvSubjects;
    ArrayList<String> Subjects;
    ArrayList<Integer> SubjectImgs;
    Tag tag;
    long[] selectedItem;
    List<String> subject=new ArrayList<>();
    String yrOfStudy;
    Button btnNext;
    ImageButton ibBack;
    boolean selected=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_subject);

        tag = new Tag();
        yrOfStudy=getIntent().getExtras().getString("yrOfStud");


        Subjects = new ArrayList<String>(Arrays.asList(tag.getSubject()));
        SubjectImgs = new ArrayList<Integer>(Arrays.asList(tag.getImg()));

        btnNext = findViewById(R.id.btn_Next);
        ibBack = findViewById(R.id.ib_Back);
        btnNext.setOnClickListener(mListener);
        ibBack.setOnClickListener(mListener);
        gvSubjects = findViewById(R.id.gvSubjects);
        gvSubjects.setAdapter(new SubjectAdapter(this));
        for (String subject : tag.getSubjects()) {
            Subjects.add(subject);
        }
        for (Integer subjectImg : tag.getImg()) {
            SubjectImgs.add(subjectImg);
        }

        gvSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (selected == false) {
                    selected = true;
                    view.setBackgroundColor(getResources().getColor(R.color.selectSubClicked));
                    btnNext.setEnabled(true);
                    gvSubjects.getOnItemSelectedListener().onItemSelected(adapterView,view,i,l);
                    //selectedItem = (RelativeLayout) adapterView.getItemAtPosition(i);
                    //subject = selectedItem.findViewById(R.id.tv_subjects).getContext().toString();
                } else {
                    selected = false;
                    selectedItem = null;
                    view.setBackgroundColor(getResources().getColor(R.color.notThatWhite));
                    btnNext.setEnabled(false);
                }
            }
        });
        gvSubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gvSubjects.setSelection(i);
                long selectedItem = gvSubjects.getSelectedItemId();
                //RelativeLayout test=gvSubjects.findViewById((int)selectedItem);
                subject.add(Subjects.get(i));
                Log.d("test2",""+gvSubjects.findViewById(R.id.tv_subjects));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
    */private View.OnClickListener mListener=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if(view.getId()==btnNext.getId()){
                selectedItem = gvSubjects.getCheckedItemIds();
                for (long id:
                     selectedItem) {
                    ConstraintLayout test=gvSubjects.findViewById((int)id);
                    subject.add(test.findViewById(R.id.tv_subjects).getContext().toString());
                }
                Intent intent=new Intent(SignUpSubject.this,SignUp_Auth.class);
                intent.putExtra("yrOfStud",yrOfStudy);
                intent.putExtra("subjects",(ArrayList)subject);
                startActivity(intent);
            }
            else if (view.getId()==ibBack.getId()){
                onBackPressed();
            }

        }
    };
}
