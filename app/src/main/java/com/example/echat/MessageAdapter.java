package com.example.echat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<EChat.Message> {
    private final Context mContext;
    private String mUsername;
    private String mNickname;
    int mResourceId;

    public MessageAdapter(Context context, int textViewResourceId, List<EChat.Message> objects, String username) {
        super(context, textViewResourceId, objects);
        mContext = context;
        mResourceId = textViewResourceId;
        mUsername = username;
        mNickname=CacheUtil.userInfoMap.get(username);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EChat.Message msg = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(mResourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.time = (TextView) view.findViewById(R.id.messageTime_textView);
            viewHolder.leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            viewHolder.fromUser = (TextView) view.findViewById(R.id.from_username);
            viewHolder.leftMsg = (TextView) view.findViewById(R.id.left_msg);
            viewHolder.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
            viewHolder.rightMsg = (TextView) view.findViewById(R.id.right_msg);
            viewHolder.mUserName = (TextView) view.findViewById(R.id.mUsername);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.time.setVisibility(View.VISIBLE);
        viewHolder.time.setText(msg.time.toString());
        if (!msg.fromId.equals(mUsername)) {
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.fromUser.setText(CacheUtil.userInfoMap.get(msg.fromId));
            viewHolder.leftMsg.setText(msg.content);
        } else {
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.mUserName.setText(mNickname);
            viewHolder.rightMsg.setText(msg.content);
        }

        return view;
    }

    public void add(EChat.Message msg) {
        super.add(msg);
    }

    class ViewHolder {
        TextView time;
        LinearLayout leftLayout;
        TextView fromUser;
        TextView leftMsg;
        LinearLayout rightLayout;
        TextView rightMsg;
        TextView mUserName;
    }
}
