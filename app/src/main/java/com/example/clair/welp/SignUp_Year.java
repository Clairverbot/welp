package com.example.clair.welp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SignUp_Year extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spYrOfStudy;
    Tag tag;
    Button btnNext;
    ImageButton ibBack;
    List<String> yr=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__year);
        spYrOfStudy=findViewById(R.id.sp_YearOfStudy);
        btnNext=(Button)findViewById(R.id.btn_Next);
        btnNext.setOnClickListener(mListener);
        ibBack=findViewById(R.id.ib_Back);
        ibBack.setOnClickListener(mListener);
        tag=new Tag();

        for (String year:tag.getYearsOfStudy()
             ) {
            yr.add(year);
        }
        ArrayAdapter<String> dataAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,yr);

        spYrOfStudy.setAdapter(dataAdapter);
    }
    //region spinner adapter
@Override
public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    // On selecting a spinner item
    String item = parent.getItemAtPosition(position).toString();
}

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //dont really need cos by default sec 1 is selected
    btnNext.setEnabled(false);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    //Todo: find out wad dis
    }
    //endregion

    private View.OnClickListener mListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId()==btnNext.getId()){
                //todo: test this!
                String y=spYrOfStudy.getSelectedItem().toString();
                Intent intent=new Intent(SignUp_Year.this,SignUpSubject.class);
                intent.putExtra("yrOfStud",y);
                startActivity(intent);

            }
        }
    };
}
