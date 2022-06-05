package com.example.echat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Login_Register_Activity extends AppCompatActivity {
    private static final int REQUEST_ERROR_CODE = -1;
    private static final int REQUEST_LOGIN_CODE = 0;
    private static final int REQUEST_REGISTER_CODE = 1;

    private EditText userName, password, nickName, confirmPassword;
    private Button button;
    private TextView textView;
    private LinearLayout nicknameLayout;
    private LinearLayout confirmPasswordLayout;
    boolean isLoginPage = true;

    private Callback mLoginCallback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e("Login Post", "onFailure: " + e.getMessage());
            Bundle bundle = new Bundle();
            bundle.putString("errorMsg", e.getMessage());
            Message message = new Message();
            message.setData(bundle);
            message.what = REQUEST_ERROR_CODE;
            mHandler.sendMessage(message);
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            String responseData = URLDecoder.decode(response.body().string(), "UTF-8");
            Log.d("Login Post", "onResponse: " + responseData);
            Bundle bundle = new Bundle();
            bundle.putString("response", responseData);
            Message message = new Message();
            message.setData(bundle);
            message.what = REQUEST_LOGIN_CODE;
            mHandler.sendMessage(message);
        }
    };

    private Callback mRegisterCallback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e("Login Post", "onFailure: " + e.getMessage());
            Bundle bundle = new Bundle();
            bundle.putString("errorMsg", e.getMessage());
            Message message = new Message();
            message.setData(bundle);
            message.what = REQUEST_ERROR_CODE;
            mHandler.sendMessage(message);
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            String responseData = response.body().string();
            Log.d("Login Post", "onResponse: " + responseData);
            Bundle bundle = new Bundle();
            bundle.putString("response", responseData);
            Message message = new Message();
            message.setData(bundle);
            message.what = REQUEST_REGISTER_CODE;
            mHandler.sendMessage(message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_register);

        userName = findViewById(R.id.username_editText);
        password = findViewById(R.id.password_editText);
        nickName = findViewById(R.id.nickname_editText);
        confirmPassword = findViewById(R.id.confirmPassword_editText);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        nicknameLayout = findViewById(R.id.nickname_layout);
        confirmPasswordLayout = findViewById(R.id.confirmPassword_layout);

        String page = getIntent().getStringExtra("page");
        if(page.equals("register")){
            changePage();
        }else if(page.equals("login")){
            userName.setText(UserInfo.username);
            password.setText(UserInfo.password);
        }

        button.setOnClickListener(v -> {
            if (isLoginPage) {
                UserInfo.clearCache(Login_Register_Activity.this);
                Map<String, String> params = new HashMap<>();
                params.put("username", userName.getText().toString());
                params.put("password", password.getText().toString());
                HttpUtil.post(HttpUtil.login_url, params, mLoginCallback);
            } else {
                if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("username", userName.getText().toString());
                        jsonObject.put("password", password.getText().toString());
                        jsonObject.put("nickname", URLEncoder.encode(nickName.getText().toString(), "UTF-8"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    HttpUtil.post(HttpUtil.register_url, jsonObject, mRegisterCallback);
                } else {
                    Toast.makeText(Login_Register_Activity.this, "两次输入的密码不一致", Toast.LENGTH_LONG).show();
                }
            }
        });

        textView.setOnClickListener(v -> {
            changePage();
        });
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REQUEST_ERROR_CODE: {
                    Bundle bundle = msg.getData();
                    String errorMsg = bundle.getString("errorMsg");
                    Toast.makeText(Login_Register_Activity.this, errorMsg, Toast.LENGTH_LONG).show();
                    break;
                }
                case REQUEST_LOGIN_CODE: {
                    try {
                        JSONObject jsonObject = new JSONObject(msg.getData().getString("response"));
                        String status = jsonObject.getString("status");
                        if (status.equals("success")) {
                            Toast.makeText(Login_Register_Activity.this, "登录成功", Toast.LENGTH_LONG).show();

                            UserInfo.uuid = jsonObject.getString("uuid");
                            UserInfo.username = jsonObject.getString("username");
                            UserInfo.password = jsonObject.getString("password");
                            UserInfo.nickname = jsonObject.getString("nickname");
                            UserInfo.updateCache(Login_Register_Activity.this);

                            Intent serviceIntent = new Intent(Login_Register_Activity.this, WebSocketService.class);
                            bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);//绑定服务

                            Intent intent = new Intent(Login_Register_Activity.this, Chat_List_Activity.class);
                            startActivity(intent);
                        } else {
                            String errorMsg = jsonObject.getString("errorMsg");
                            errorMsg = URLDecoder.decode(errorMsg, "UTF-8");
                            Toast.makeText(Login_Register_Activity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case REQUEST_REGISTER_CODE: {
                    try {
                        JSONObject jsonObject = new JSONObject(msg.getData().getString("response"));
                        String status = jsonObject.getString("status");
                        if (status.equals("success")) {
                            Toast.makeText(Login_Register_Activity.this, "注册成功", Toast.LENGTH_LONG).show();
                            changePage();
                        } else {
                            int errCode = jsonObject.getInt("errCode");
                            String errorMsg = "注册失败";
                            switch (errCode) {
                                case 1:
                                    errorMsg = "用户名已存在";
                                    break;
                            }
                            Toast.makeText(Login_Register_Activity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    };

    private void changePage() {
        if (isLoginPage) {
            nicknameLayout.setVisibility(LinearLayout.VISIBLE);
            confirmPasswordLayout.setVisibility(LinearLayout.VISIBLE);
            userName.setText(null);
            password.setText(null);
            button.setText(R.string.register_button_text);
            textView.setText(R.string.login_textview_text);

//            {
//                userName.setText("874318263@qq.com");
//                password.setText("123456");
//                nickName.setText("败笔");
//                confirmPassword.setText("123456");
//            }
        } else {
            nicknameLayout.setVisibility(LinearLayout.GONE);
            confirmPasswordLayout.setVisibility(LinearLayout.GONE);
            button.setText(R.string.login_button_text);
            textView.setText(R.string.register_textview_text);
        }
        isLoginPage = !isLoginPage;
    }

    private WebSocketService.WebSocketServiceBinder mBinder;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBinder = (WebSocketService.WebSocketServiceBinder) iBinder;
            WebSocketService webSocketService = mBinder.getService();
            webSocketService.init(HttpUtil.host + "/chat/" + UserInfo.uuid);
        }

        //当服务失去连接时调用的方法
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBinder = null;
        }
    };

}
