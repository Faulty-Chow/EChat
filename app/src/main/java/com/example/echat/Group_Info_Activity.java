package com.example.echat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Group_Info_Activity extends AppCompatActivity {
    private TextView groupId_textView;
    private EditText groupName_editText;
    private TextView groupOwner_textView;
    private ListView groupMember_listView;

    private String groupId;
    private String groupName;
    private String groupOwner;
    private List<String> groupMember = new ArrayList<>();

    private UserAdapter userAdapter;


    private List<String> groupInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        groupId = getIntent().getStringExtra("groupId");

        groupId_textView = findViewById(R.id.groupId_textView2);
        groupName_editText = findViewById(R.id.groupName_editText);
        groupOwner_textView = findViewById(R.id.groupOwner_textView2);
        groupMember_listView = findViewById(R.id.groupMember_listView);

        try {
            groupInfoList = CacheUtil.getCache(this, Chat_List_Activity.ChatInfo_CacheFile + "_group");
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            JSONObject jsonObject = getGroupInfo(groupId);
            groupId = jsonObject.getString("sessionId");
            groupName = jsonObject.getString("sessionName");
            groupOwner = jsonObject.getString("ownerId");
            String groupMemberStr = jsonObject.getString("members");
            List<UserAdapter.User> userList = getMemberList(groupMemberStr);

            groupId_textView.setText(groupId);
            groupName_editText.setText(groupName);
            groupOwner_textView.setText(groupOwner);
            userAdapter = new UserAdapter(this, R.layout.user_list_item, userList, false);
            groupMember_listView.setAdapter(userAdapter);

            if (groupOwner.equals(UserInfo.username)) {
                initEditGroupInfo();
            } else {
                initViewGroupInfo();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<UserAdapter.User> getMemberList(String groupMember) throws JSONException {
        List<UserAdapter.User> userList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(groupMember);
        for (int i = 0; i < jsonArray.length(); i++) {
            String username = jsonArray.getString(i);
            this.groupMember.add(username);
            userList.add(new UserAdapter.User(username, CacheUtil.userInfoMap.get(username)));
        }
        return userList;
    }

    private JSONObject getGroupInfo(String groupId) throws JSONException {
        for (String groupInfo : groupInfoList) {
            JSONObject jsonObject = new JSONObject(groupInfo);
            if (jsonObject.getString("sessionId").equals(groupId)) {
                return jsonObject;
            }
        }
        // TODO: 2020/6/10 像服务器请求群组信息
        return null;
    }

    private void initViewGroupInfo() {
        ((EditText) findViewById(R.id.groupName_editText)).setEnabled(false);
        ((TextView) findViewById(R.id.editGroupMember)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.saveNewGroupName)).setVisibility(View.GONE);
        Button action_button = findViewById(R.id.action_button);
        action_button.setText("退 出 群 聊");
        ((TextView) findViewById(R.id.action_textView)).setVisibility(View.GONE);
        action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(Group_Info_Activity.this);
                normalDialog.setTitle("Dialog");
                normalDialog.setMessage("确定要退出群聊吗?");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                normalDialog.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                // 显示
                normalDialog.show();
            }
        });

    }

    private void initEditGroupInfo() {
        TextView saveNewGroupName = findViewById(R.id.saveNewGroupName);
        saveNewGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newGroupName = groupName_editText.getText().toString();
                if (groupName.equals("")) {
                    groupName_editText.setError("群名不能为空");
                } else {
                    if (!groupName.equals(newGroupName)) {
                        final AlertDialog.Builder normalDialog =
                                new AlertDialog.Builder(Group_Info_Activity.this);
                        normalDialog.setTitle("Dialog");
                        normalDialog.setMessage("确定要将群组名称修改为：" + newGroupName + " 吗?");
                        normalDialog.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //...To-do
                                    }
                                });
                        normalDialog.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //...To-do
                                    }
                                });
                        normalDialog.show();
                    }
                }
            }
        });

        TextView editGroupMember = findViewById(R.id.editGroupMember);

        editGroupMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Group_Info_Activity", "Action add members.");

                final View layout = getLayoutInflater().inflate(R.layout.dialog_user_list, null);
                ListView userListView = layout.findViewById(R.id.user_listView);
                List<UserAdapter.User> allUserList = new ArrayList<>();
                Set<String> memberSet = new HashSet<>(groupMember);
                CacheUtil.userInfoMap.forEach((key, value) -> {
                    if (memberSet.contains(key))
                        allUserList.add(new UserAdapter.User(key, value, true));
                    else
                        allUserList.add(new UserAdapter.User(key, value, false));
                });
                UserAdapter userAdapter = new UserAdapter(Group_Info_Activity.this, R.layout.user_list_item, allUserList, true);
                userListView.setAdapter(userAdapter);

                final AlertDialog.Builder dialog =
                        new AlertDialog.Builder(Group_Info_Activity.this);
                dialog.setTitle("邀请成员加入");
                dialog.setView(layout);
                dialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                dialog.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                dialog.show();
            }
        });

        Button action_button = findViewById(R.id.action_button);
        TextView action_textView = findViewById(R.id.action_textView);
        action_button.setText("解 散 群 聊");
        action_textView.setText("取  消");

        action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(Group_Info_Activity.this);
                normalDialog.setTitle("Dialog");
                normalDialog.setMessage("确定要解散群聊吗?");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                normalDialog.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });
                // 显示
                normalDialog.show();
            }
        });

        action_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
