package com.example.echat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class User_Info_Activity extends AppCompatActivity {
    private String username = UserInfo.username;
    private String password = UserInfo.password;
    private String nickname = UserInfo.nickname;
    private String phone = "181 6734 9935";

    private EditText username_editText;
    private EditText password_editText;
    private EditText nickname_editText;
    private EditText phoneNumber_editText;

    private LinearLayout password_layout;
    private LinearLayout action_layout;

    private Button save_button;
    private TextView cancel_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        username_editText = findViewById(R.id.username_editText);
        password_editText = findViewById(R.id.password_editText);
        nickname_editText = findViewById(R.id.nickname_editText);
        phoneNumber_editText = findViewById(R.id.phoneNumber_editText);
        password_layout = findViewById(R.id.password_layout);
        action_layout = findViewById(R.id.action_layout);
        save_button = findViewById(R.id.save_button);
        cancel_textView = findViewById(R.id.cancel_textView);


        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        username_editText.setText(username);
        username_editText.setEnabled(false);


        if (!username.equals(UserInfo.username)) {
            password_layout.setEnabled(false);
            password_layout.setVisibility(LinearLayout.GONE);
            action_layout.setEnabled(false);
            action_layout.setVisibility(LinearLayout.GONE);

            nickname_editText.setText(CacheUtil.userInfoMap.get(username));
            phoneNumber_editText.setText("-用户不愿意透露-");
            nickname_editText.setEnabled(false);
            phoneNumber_editText.setEnabled(false);
        } else {
            password_layout.setEnabled(true);
            password_layout.setVisibility(LinearLayout.VISIBLE);
            action_layout.setEnabled(true);
            action_layout.setVisibility(LinearLayout.VISIBLE);

            password_editText.setText(password);
            nickname_editText.setText(nickname);
            phoneNumber_editText.setText(phone);
        }

        save_button.setOnClickListener(v -> {
            if(!password_editText.getText().toString().equals(password)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(User_Info_Activity.this);
                builder.setTitle("确认以更改密码");
                builder.setView(R.layout.dialog_password);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String original_password = ((EditText) ((AlertDialog) dialog).findViewById(R.id.original_editText)).getText().toString();
                        String new_password = ((EditText) ((AlertDialog) dialog).findViewById(R.id.new_editText)).getText().toString();
                        if(original_password.equals(password)) {
                           if(new_password.equals(password_editText.getText().toString())){
                               password = password_editText.getText().toString();
                               nickname = nickname_editText.getText().toString();
                               phone = phoneNumber_editText.getText().toString();
                               Toast.makeText(User_Info_Activity.this, "修改成功", Toast.LENGTH_SHORT).show();
                           }else{
                                 Toast.makeText(User_Info_Activity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                           }
                        }else{
                            Toast.makeText(User_Info_Activity.this, "原密码错误", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(User_Info_Activity.this, "negative: " + which, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
            }else{
                nickname = nickname_editText.getText().toString();
                phone = phoneNumber_editText.getText().toString();
                Toast.makeText(User_Info_Activity.this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        cancel_textView.setOnClickListener(v -> {
            Toast.makeText(User_Info_Activity.this, "取消", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
