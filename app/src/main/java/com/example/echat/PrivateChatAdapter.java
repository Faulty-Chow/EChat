package com.example.echat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PrivateChatAdapter extends BaseAdapter {

    public static class PrivateChat extends EChat.User{
        public boolean newMsg;
        public PrivateChat(String userId, String userName) {
            super(userId, userName);
            newMsg = false;
        }
    }

    private final Context mContext;
    private final int mResourceId;
    private List<PrivateChat> mPrivateChat;

    public PrivateChatAdapter(Context context, int resourceId, List<EChat.User> users) {
        mContext = context;
        mResourceId = resourceId;
        mPrivateChat = new ArrayList<>();
        for (EChat.User user : users) {
            mPrivateChat.add(new PrivateChat(user.userId, user.nickName));
        }
    }

    @Override
    public int getCount() {
        return mPrivateChat.size();
    }

    @Override
    public Object getItem(int i) {
        return mPrivateChat.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        PrivateChat privateChat = mPrivateChat.get(i);
        View view;
        PrivateChatAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(mResourceId, null);
            viewHolder = new PrivateChatAdapter.ViewHolder();
            viewHolder.sessionType = (TextView) view.findViewById(R.id.session_type);
            viewHolder.sessionName = (TextView) view.findViewById(R.id.session_name);
            viewHolder.sessionId = (TextView) view.findViewById(R.id.session_id);
            viewHolder.newMsg = (TextView) view.findViewById(R.id.new_message);

            viewHolder.sessionType.setBackground(mContext.getDrawable(R.drawable.private_chat));

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (PrivateChatAdapter.ViewHolder) view.getTag();
        }

        viewHolder.sessionName.setText(privateChat.nickName);
        viewHolder.sessionId.setText(privateChat.userId);
        if(privateChat.newMsg)
            viewHolder.newMsg.setVisibility(View.VISIBLE);
        else
            viewHolder.newMsg.setVisibility(View.GONE);

        return view;
    }

    class ViewHolder {
        TextView sessionType;
        TextView sessionName;
        TextView sessionId;
        TextView newMsg;
    }

    public void notifyNewMessage(String userId) {
        for (PrivateChat privateChat : mPrivateChat) {
            if (privateChat.userId.equals(userId)) {
                privateChat.newMsg = true;
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void haveReadNewMessage(String userId) {
        for (PrivateChat privateChat : mPrivateChat) {
            if (privateChat.userId.equals(userId)) {
                privateChat.newMsg = false;
                break;
            }
        }
        notifyDataSetChanged();
    }
}
