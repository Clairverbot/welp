package com.example.clair.welp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class SignUp_Auth extends AppCompatActivity {
    EditText etUsername,etEmail,etPassword,etConfirmPassword;
    Button btnRegister;
    RelativeLayout activity_sign_up_auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__auth);
        etUsername=findViewById(R.id.et_Username);
        etEmail=findViewById(R.id.et_Email);
        etPassword=findViewById(R.id.et_CfmPassword);
        btnRegister=findViewById(R.id.btnRegister);
        activity_sign_up_auth=findViewById(R.id.activity_sign_up_auth);
    }
}
