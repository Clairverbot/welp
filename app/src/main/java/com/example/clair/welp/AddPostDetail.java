package com.example.clair.welp;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.clair.welp.Firebase.NoteFirestore;
import com.example.clair.welp.Objects.Note;
import com.example.clair.welp.Objects.Tag;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddPostDetail extends AppCompatActivity {

    Tag tag;
    List<String> yr=new ArrayList<String>();
    List<String> sub=new ArrayList<String>();
    EditText etNoteTitle,etNoteDesc;
    Spinner spGrade,spSubject;
    RadioButton rbNotes, rbPractice,rbLesson;
    RadioGroup rg;
    Button btnPost;
    Uri path;
    String email,username;
    NoteFirestore noteFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etNoteTitle=findViewById(R.id.etNoteTitle);
        etNoteDesc=findViewById(R.id.etNoteDesc);
        spGrade=findViewById(R.id.sp_YearOfStudy);
        spSubject=findViewById(R.id.sp_Subject);
        rbLesson=findViewById(R.id.rbLesson);
        rbNotes=findViewById(R.id.rbNotes);
        rbPractice=findViewById(R.id.rbPractice);
        rg=findViewById(R.id.rg);
        btnPost=findViewById(R.id.btnPost);

        path= (Uri) getIntent().getExtras().get("path");
        email=getIntent().getExtras().getString("email");
        username=getIntent().getExtras().getString("username");

        noteFirestore=new NoteFirestore();
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etNoteDesc.getText()!=null &&etNoteTitle!=null){
                    RadioButton rb=findViewById(rg.getCheckedRadioButtonId());
                    List<String> tags= Arrays.asList((String) spSubject.getSelectedItem(),(String)spGrade.getSelectedItem(),(String)rb.getText(),(String) spSubject.getSelectedItem()+" + "+(String)spGrade.getSelectedItem());

                    //CHANGE DATE FORMAT
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String datePosted = dateFormat.format(new Date());

                    Note note=new Note(email,username,null,etNoteTitle.getText().toString(),etNoteDesc.getText().toString(),path.toString(), datePosted,null,tags,0,0,null);
                    //noteFirestore.storage(note);
                    noteFirestore.add(note);
                    Intent intent=new Intent(AddPostDetail.this,MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Snackbar.make(findViewById(R.id.clAddPostDetail), "Please Enter all fields", Snackbar.LENGTH_SHORT);
                }
            }
        });

        tag=new Tag();
        for (String year:tag.getYearsOfStudy()) {
            yr.add(year);
        }
        ArrayAdapter<String> dataAdapter=new ArrayAdapter<String>(this,R.layout.spinner_black_preview_text,yr);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item_style);
        spGrade.setAdapter(dataAdapter);

        for (String subject:tag.getSubjects()) {
            sub.add(subject);
        }
        ArrayAdapter<String> DataAdapter=new ArrayAdapter<String>(this,R.layout.spinner_black_preview_text,sub);
        DataAdapter.setDropDownViewResource(R.layout.spinner_item_style);
        spSubject.setAdapter(DataAdapter);
    }

    //back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return true;
    }

}
