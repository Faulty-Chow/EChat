package com.example.echat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, password, nickname;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_register);

        username = findViewById(R.id.username_editText);
        password = findViewById(R.id.password_editText);
        nickname = findViewById(R.id.nickname_editText);
        register = findViewById(R.id.register_button);
    }
}
