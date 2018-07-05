package com.shivam.pgb_signup_login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class Account_SetupActivity extends AppCompatActivity {

    private EditText et_email, et_passswod, et_Name;
    String userName, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account__setup);

        userEmail = getIntent().getExtras().getString("email");
        userName = getIntent().getExtras().getString("name");

        et_email = findViewById(R.id.etEmail);
        et_Name = findViewById(R.id.etName);
        et_passswod = findViewById(R.id.etPassword);


        et_email.setText(userEmail);
        et_Name.setText(userName);
    }
}
