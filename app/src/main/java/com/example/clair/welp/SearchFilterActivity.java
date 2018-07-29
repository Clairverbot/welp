package com.example.clair.welp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.clair.welp.Objects.Note;
import com.example.clair.welp.Objects.Tag;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFilterActivity extends AppCompatActivity {
    @BindView(R.id.sp_YearOfStudy)
    Spinner spGrade;
    @BindView(R.id.sp_Subject)
    Spinner spSubject;
    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.btnSearch)
    Button btnSearch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchfilter);
        ButterKnife.bind(this);

        //Title and backbutton
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Search by Filter");

        //Add items to Spinners
        ArrayAdapter<String> DataAdapter=new ArrayAdapter<String>(this,R.layout.spinner_black_preview_text, Tag.Subjects);
        DataAdapter.setDropDownViewResource(R.layout.spinner_item_style);
        spSubject.setAdapter(DataAdapter);
        ArrayAdapter<String> dataAdapter=new ArrayAdapter<String>(this,R.layout.spinner_black_preview_text,Tag.YearsOfStudy);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item_style);
        spGrade.setAdapter(dataAdapter);


        //OnClick Search
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb=findViewById(rg.getCheckedRadioButtonId());
                ArrayList<String> passedSearchQuery = new ArrayList<>();

                passedSearchQuery.add((String) spSubject.getSelectedItem() + " + " + (String) spGrade.getSelectedItem());
                passedSearchQuery.add((String) rb.getText());

                Intent i = new Intent(SearchFilterActivity.this, SearchResultsActivity.class);
                i.putExtra("passedSearchQuery", passedSearchQuery);
                startActivity(i);

            }
        });
    }


    //UP BUTTON (BACK)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //GOTO : Profile Activity with Notebook Tab Selected
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

}
