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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Chat_Activity extends AppCompatActivity {
    public static final int GROUP_CHAT_CODE = 0;
    public static final int PRIVATE_CHAT_CODE = 1;
    public static final int GET_MESSAGE_ACTION_CODE = 0;
    public static final int SEND_MESSAGE_ACTION_CODE = 1;
    public static final int REQUEST_ERROR_CODE = -1;
    public static final int REQUEST_CHAT_MESSAGE_CODE = 0;
    public static final int SEND_MESSAGE_CODE = 1;
    public static final int RECEIVE_MESSAGE_CODE = 2;

    private int chatType;
    private String chatId;
    private String chatName;

    private TextView chatName_textView;
    private TextView chatId_textView;

    private ListView message_ListView;
    private MessageAdapter messageAdapter;
    private TextView send_textView;

    SimpleDateFormat simpleDateFormat;

    private Callback mChatMessageCallback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e("Request Chat List", "onFailure: " + e.getMessage());
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
            responseData = URLDecoder.decode(responseData, "UTF-8");
            Log.d("Request Chat List", "onResponse: " + responseData);
            Bundle bundle = new Bundle();
            bundle.putString("response", responseData);
            Message message = new Message();
            message.setData(bundle);
            message.what = REQUEST_CHAT_MESSAGE_CODE;
            mHandler.sendMessage(message);
        }
    };

    private Callback mSendMessageCallback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e("Request Chat List", "onFailure: " + e.getMessage());
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
            Log.d("Request Chat List", "onResponse: " + responseData);
            Bundle bundle = new Bundle();
            bundle.putString("response", responseData);
            Message message = new Message();
            message.setData(bundle);
            message.what = SEND_MESSAGE_CODE;
            mHandler.sendMessage(message);
        }
    };

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REQUEST_ERROR_CODE: {
                    Bundle bundle = msg.getData();
                    String errorMsg = bundle.getString("errorMsg");
                    Toast.makeText(Chat_Activity.this, errorMsg, Toast.LENGTH_LONG).show();
                    break;
                }
                case REQUEST_CHAT_MESSAGE_CODE: {
                    try {
                        JSONArray jsonArray = new JSONArray(msg.getData().getString("response"));
                        messageAdapter = new MessageAdapter(Chat_Activity.this, R.layout.message_box, getMessageList(jsonArray), UserInfo.username);
                        message_ListView.setAdapter(messageAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(Chat_Activity.this, "聊天记录已更新", Toast.LENGTH_LONG).show();
                    break;
                }
                case SEND_MESSAGE_CODE: {
                    try {
                        JSONObject jsonObject = new JSONObject(msg.getData().getString("response"));
                        if (jsonObject.getBoolean("status")) {
                            String fromId = jsonObject.getString("fromId");
                            String toId = jsonObject.getString("toId");
                            String content = jsonObject.getString("content");
                            String sendTime = jsonObject.getString("sendTime");
                            messageAdapter.add(new EChat.Message(fromId, toId, content, simpleDateFormat.parse(sendTime)));
                            messageAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case RECEIVE_MESSAGE_CODE: {
                    try {
                        JSONObject jsonObject = new JSONObject(msg.getData().getString("response"));
                        String toId = jsonObject.getString("toId");
                        String fromId = jsonObject.getString("fromId");
                        String content = jsonObject.getString("content");
                        String timeStr = jsonObject.getString("sendTime");

                        if (chatType == GROUP_CHAT_CODE) {
                            if (toId.equals(chatId)) {
                                messageAdapter.add(new EChat.Message(fromId, toId, content, simpleDateFormat.parse(timeStr)));
                                messageAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(Chat_Activity.this, "收到新消息", Toast.LENGTH_LONG).show();
                            }
                        } else if (chatType == PRIVATE_CHAT_CODE) {
                            if (fromId.equals(chatId)) {
                                messageAdapter.add(new EChat.Message(fromId, toId, content, simpleDateFormat.parse(timeStr)));
                                messageAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(Chat_Activity.this, "收到新消息", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private WebSocketService.WebSocketServiceBinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_chat);


        Intent serviceIntent = new Intent(Chat_Activity.this, WebSocketService.class);
        bindService(serviceIntent, mServiceConnection, BIND_AUTO_CREATE);//绑定服务


        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        chatName_textView = findViewById(R.id.chatName_textView);
        chatId_textView = findViewById(R.id.chatId_textView);
        message_ListView = findViewById(R.id.message_listView);
        send_textView = findViewById(R.id.send_TextView);

        Intent intent = getIntent();
        String chatInfo = intent.getStringExtra("chatInfo");

        try {
            JSONObject jsonObject = new JSONObject(chatInfo);
            chatType = jsonObject.getInt("chatType");
            chatId = jsonObject.getString("chatId");
            chatName = jsonObject.getString("chatName");
            chatName_textView.setText(chatName);
            chatId_textView.setText(chatId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            Map<String, String> params = new HashMap<>();
            params.put("actionCode", String.valueOf(GET_MESSAGE_ACTION_CODE));
            params.put("chatType", String.valueOf(chatType));
            params.put("chatId", chatId);
            HttpUtil.post(HttpUtil.chatAction_url, params, mChatMessageCallback);
        }).start();

        send_textView.setOnClickListener(v -> {
            String content = ((EditText) findViewById(R.id.input_EditText)).getText().toString();
            if (content.isEmpty()) {
                Toast.makeText(Chat_Activity.this, "不能发送空消息", Toast.LENGTH_LONG).show();
                return;
            }
            new Thread(() -> {
                Map<String, String> params = new HashMap<>();
                params.put("actionCode", String.valueOf(SEND_MESSAGE_ACTION_CODE));
                params.put("chatType", String.valueOf(chatType));
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("fromId", UserInfo.username);
                    jsonObject.put("toId", chatId);
                    jsonObject.put("content", content);
                    params.put("message", URLEncoder.encode(jsonObject.toString(), "UTF-8"));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                HttpUtil.post(HttpUtil.chatAction_url, params, mSendMessageCallback);
            }).start();
            ((EditText) findViewById(R.id.input_EditText)).setText(null);
        });

        ((TextView)findViewById(R.id.backToChatList)).setOnClickListener(v -> finish());

        ((TextView)findViewById(R.id.chatInfoEdit)).setOnClickListener(v -> {
            if(chatType == PRIVATE_CHAT_CODE){
                Intent intent1 = new Intent(Chat_Activity.this, User_Info_Activity.class);
                intent1.putExtra("username", chatId);
                startActivity(intent1);
            }else if(chatType == GROUP_CHAT_CODE){
                Intent intent1 = new Intent(Chat_Activity.this, Group_Info_Activity.class);
                intent1.putExtra("groupId", chatId);
                startActivity(intent1);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private List<EChat.Message> getMessageList(JSONArray jsonArray) {
        List<EChat.Message> messageList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String fromId = jsonObject.getString("fromId");
                String toId = jsonObject.getString("toId");
                String content = jsonObject.getString("content");
                String timeStr = jsonObject.getString("sendTime");
                messageList.add(new EChat.Message(fromId, toId, content, simpleDateFormat.parse(timeStr)));
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }
        }
        return messageList;
    }

    private WebSocketService mWebSocketService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBinder = (WebSocketService.WebSocketServiceBinder) iBinder;
            mWebSocketService = mBinder.getService();
            mWebSocketService.subscriptionService(new WebSocketService.onMessageListener() {
                @Override
                public void onMessage(String message) {
                    Log.d("Chat_Activity", "onMessage: " + message);
                    Bundle bundle = new Bundle();
                    bundle.putString("response", message);
                    Message mes = new Message();
                    mes.setData(bundle);
                    mes.what = RECEIVE_MESSAGE_CODE;
                    mHandler.sendMessage(mes);
                }
            });
        }

        //当服务失去连接时调用的方法
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBinder = null;
        }
    };

    private final WebSocketService.onMessageListener mOnMessageListener = new WebSocketService.onMessageListener() {
        @Override
        public void onMessage(String message) {
            Log.d("Chat_Activity", "onMessage: " + message);

            try {
                JSONObject jsonObject = new JSONObject(message);
                String toId = jsonObject.getString("toId");
                if (!toId.equals(chatId)) {
                    return;
                }
                String fromId = jsonObject.getString("fromId");
                String content = jsonObject.getString("content");
                String timeStr = jsonObject.getString("sendTime");
                messageAdapter.add(new EChat.Message(fromId, toId, content, simpleDateFormat.parse(timeStr)));
                messageAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

//    private void sendMessage(String content) throws JSONException, UnsupportedEncodingException {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("cookie", HttpUtil.uuid);
//        jsonObject.put("toId", chatId);
//        jsonObject.put("content", content);
//        String message = URLEncoder.encode(jsonObject.toString(), "utf-8");
//        mWebSocketService.send(message);
//    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}
