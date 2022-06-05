package com.example.echat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Chat_List_Activity extends AppCompatActivity {
    public final static String ChatInfo_CacheFile = "ChatInfo";
    private final static String TAG = "SessionList";
    public static final int REQUEST_ERROR_CODE = -1;
    public static final int REQUEST_GROUP_CHAT_CODE = 0;
    public static final int REQUEST_PRIVATE_CHAT_CODE = 1;
    public static final int REQUEST_PUBLIC_CHAT_CODE = 1;
    public static final int RECEIVE_MESSAGE_CODE = 2;

    private ListView chat_ListView;
    private PrivateChatAdapter mPrivateChatAdapter;
    private GroupChatAdapter mGroupChatAdapter;
    private TextView page_GroupChat;
    private TextView page_PrivateChat;
    private int currentPage = 0;

    private Callback mChatListCallback = new Callback() {
        private final String tag = "RequestChatInfo";

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
            long timestamp = response.sentRequestAtMillis();
            CacheUtil.saveCache(Chat_List_Activity.this, CacheUtil.lastRequest_CacheDir, tag, String.valueOf(timestamp));

            String responseData = URLDecoder.decode(response.body().string(), "UTF-8");
            Log.d("Request Chat List", "onResponse: " + responseData);
            try {
                JSONObject jsonObject = new JSONObject(responseData);
                int chatType = jsonObject.getInt("chatType");
                String chatList = jsonObject.getString("chatList");

                Bundle bundle = new Bundle();
                bundle.putString("response", chatList);
                Message message = new Message();
                message.setData(bundle);
                if (chatType == REQUEST_GROUP_CHAT_CODE) {
                    message.what = REQUEST_GROUP_CHAT_CODE;
                } else if (chatType == REQUEST_PRIVATE_CHAT_CODE) {
                    message.what = REQUEST_PRIVATE_CHAT_CODE;
                }
                mHandler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        ((TextView)findViewById(R.id.nickname_textView)).setText(UserInfo.nickname);
        ((TextView)findViewById(R.id.username_textView)).setText(UserInfo.username);

        chat_ListView = findViewById(R.id.chat_listView);
        page_GroupChat = findViewById(R.id.page_groupChat);
        page_PrivateChat = findViewById(R.id.page_privateChat);

        page_GroupChat.setOnClickListener(v -> {
            changeChatPage(REQUEST_GROUP_CHAT_CODE);
        });

        page_PrivateChat.setOnClickListener(v -> {
            changeChatPage(REQUEST_PRIVATE_CHAT_CODE);
        });

        refreshChatList();

        chat_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Chat_List_Activity.this, Chat_Activity.class);
                JSONObject chatInfo = new JSONObject();
                try {
                    chatInfo.put("chatType", currentPage);
                    chatInfo.put("chatName", ((TextView) view.findViewById(R.id.session_name)).getText().toString());
                    chatInfo.put("chatId", ((TextView) view.findViewById(R.id.session_id)).getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("chatInfo", chatInfo.toString());
                startActivity(intent);
            }
        });

        ((TextView)findViewById(R.id.refreshButton)).setOnClickListener(v -> {
            refreshChatList();
        });

        ((TextView)findViewById(R.id.selfInfo)).setOnClickListener(v -> {
            Intent intent = new Intent(Chat_List_Activity.this, User_Info_Activity.class);
            intent.putExtra("username", UserInfo.username);
            startActivity(intent);
        });
    }

    private void refreshChatList(){
        new Thread(() -> {
            Map<String, String> params = new HashMap<>();
            params.put("chatType", String.valueOf(REQUEST_GROUP_CHAT_CODE));
            HttpUtil.post(HttpUtil.chatInfo_url, params, mChatListCallback);
        }).start();

        new Thread(() -> {
            Map<String, String> params = new HashMap<>();
            params.put("chatType", String.valueOf(REQUEST_PRIVATE_CHAT_CODE));
            HttpUtil.post(HttpUtil.chatInfo_url, params, mChatListCallback);
        }).start();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REQUEST_ERROR_CODE: {
                    Bundle bundle = msg.getData();
                    String errorMsg = bundle.getString("errorMsg");
                    Toast.makeText(Chat_List_Activity.this, errorMsg, Toast.LENGTH_LONG).show();
                    break;
                }
                case REQUEST_GROUP_CHAT_CODE: {
                    try {
                        JSONArray jsonArray = new JSONArray(msg.getData().getString("response"));
                        CacheUtil.saveCache(Chat_List_Activity.this, ChatInfo_CacheFile + "_group", CacheUtil.JSONArray_to_List(jsonArray), Context.MODE_APPEND);
                        mGroupChatAdapter = new GroupChatAdapter(Chat_List_Activity.this, R.layout.session_box, getSessionList(jsonArray));
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case REQUEST_PRIVATE_CHAT_CODE: {
                    try {
                        JSONArray jsonArray = new JSONArray(msg.getData().getString("response"));
                        CacheUtil.saveCache(Chat_List_Activity.this, ChatInfo_CacheFile + "_private", CacheUtil.JSONArray_to_List(jsonArray), Context.MODE_APPEND);
                        mPrivateChatAdapter = new PrivateChatAdapter(Chat_List_Activity.this, R.layout.session_box, getUserList(jsonArray));
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case RECEIVE_MESSAGE_CODE:{

                }
            }
            if (currentPage == REQUEST_GROUP_CHAT_CODE) {
                chat_ListView.setAdapter(mGroupChatAdapter);
            } else if (currentPage == REQUEST_PRIVATE_CHAT_CODE) {
                chat_ListView.setAdapter(mPrivateChatAdapter);
            }
            Toast.makeText(Chat_List_Activity.this, "已刷新聊天列表", Toast.LENGTH_SHORT).show();
        }
    };

//    private Runnable mTimeCounterRunnable = new Runnable() {
//        @Override
//        public void run() {//在此添加需轮寻的接口
//            getUnreadCount();//getUnreadCount()执行的任务
//            mHandler.postDelayed(this, 30 * 1000);
//        }
//    };

    private List<EChat.Session> getSessionList(JSONArray jsonArray) throws JSONException {
        List<EChat.Session> sessionList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String session_id = jsonObject.getString("sessionId");
            String session_name = jsonObject.getString("sessionName");
            String session_owner = jsonObject.getString("ownerId");
            Object members = jsonObject.get("members");
            JSONArray memberJSONArray = (JSONArray) members;
            List<String> memberList = new ArrayList<>();
            for (int j = 0; j < memberJSONArray.length(); j++) {
                memberList.add(memberJSONArray.getString(j));
            }
            sessionList.add(new EChat.Session(session_id, session_name, session_owner, memberList));
        }
        return sessionList;
    }

    private List<EChat.User> getUserList(JSONArray jsonArray) throws JSONException {
        List<EChat.User> userList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String username = jsonObject.getString("username");
            String nickname = jsonObject.getString("nickname");
            userList.add(new EChat.User(username, nickname));
            CacheUtil.userInfoMap.put(username, nickname);
        }
        return userList;
    }

    private void changeChatPage(int page) {
        page_GroupChat.setBackground(null);
        page_PrivateChat.setBackground(null);
        if (page == REQUEST_GROUP_CHAT_CODE) {
            chat_ListView.setAdapter(mGroupChatAdapter);
            page_GroupChat.setBackgroundResource(R.drawable.page_selected_background);
        } else if (page == REQUEST_PRIVATE_CHAT_CODE) {
            chat_ListView.setAdapter(mPrivateChatAdapter);
            page_PrivateChat.setBackgroundResource(R.drawable.page_selected_background);
        }
        currentPage = page;
    }

    private void initAdapterByCache() throws JSONException {
        String groupChatInfo = CacheUtil.getCache(Chat_List_Activity.this, CacheUtil.chatList_CacheDir, "GroupChatInfo");
        String privateChatInfo = CacheUtil.getCache(Chat_List_Activity.this, CacheUtil.chatList_CacheDir, "PrivateChatInfo");
        mGroupChatAdapter = new GroupChatAdapter(Chat_List_Activity.this, R.layout.session_box, getSessionList(new JSONArray(groupChatInfo)));
        mPrivateChatAdapter = new PrivateChatAdapter(Chat_List_Activity.this, R.layout.session_box, getUserList(new JSONArray(privateChatInfo)));
    }

    private void updateUserList() {

    }

    private void updateGroupList() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mHandler.removeCallbacks(mTimeCounterRunnable);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private WebSocketService mWebSocketService;
    private WebSocketService.WebSocketServiceBinder mBinder;

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
}
