package com.example.echat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class GroupChatAdapter extends BaseAdapter {

    private final Context mContext;
    private final int mResourceId;
    private List<EChat.Session> mSessions;

    public GroupChatAdapter(Context context, int resourceId, List<EChat.Session> sessions) {
        mContext = context;
        mResourceId = resourceId;
        mSessions = sessions;
    }

    @Override
    public int getCount() {
        return mSessions.size();
    }

    @Override
    public Object getItem(int i) {
        return mSessions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EChat.Session session = mSessions.get(position);
        View view;
        GroupChatAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(mResourceId, null);
            viewHolder = new GroupChatAdapter.ViewHolder();
            viewHolder.sessionType = (TextView) view.findViewById(R.id.session_type);
            viewHolder.sessionName = (TextView) view.findViewById(R.id.session_name);
            viewHolder.sessionId = (TextView) view.findViewById(R.id.session_id);
            viewHolder.newMsg = (TextView) view.findViewById(R.id.new_message);

            viewHolder.sessionType.setBackground(mContext.getDrawable(R.drawable.group_chat));

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (GroupChatAdapter.ViewHolder) view.getTag();
        }

        viewHolder.sessionName.setText(session.sessionName + "(" + session.memberIds.size() + ")");
        viewHolder.sessionId.setText(session.sessionId);
        if (session.newMsg)
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

    public void notifyNewMessage(String sessionId) {
        for (EChat.Session session : mSessions) {
            if (session.sessionId.equals(sessionId)) {
                session.newMsg = true;
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void haveReadNewMessage(String sessionId) {
        for (EChat.Session session : mSessions) {
            if (session.sessionId.equals(sessionId)) {
                session.newMsg = false;
                break;
            }
        }
        notifyDataSetChanged();
    }
}
