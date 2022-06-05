package com.example.echat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button login, register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserInfo userInfo = new UserInfo(this);

        login = findViewById(R.id.login_button);
        register = findViewById(R.id.register_button);

        login.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Login_Register_Activity.class);
            intent.putExtra("page", "login");
            startActivity(intent);
        });
        register.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Login_Register_Activity.class);
            intent.putExtra("page", "register");
            startActivity(intent);
        });

//        ActivityResultLauncher registerActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            Intent data = result.getData();
//            int resultCode = result.getResultCode();
//        });
//        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
//        register.setOnClickListener(v -> {
//            registerActivity.launch(intent);
//        });
    }


}