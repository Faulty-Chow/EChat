package com.example.echat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends BaseAdapter {

    public static class User {
        public String username;
        public String nickname;
        public boolean isSelected;

        public User(String username, String nickname) {
            this.username = username;
            this.nickname = nickname;
            this.isSelected = false;
        }

        public User(String username, String nickname, boolean isSelected) {
            this.username = username;
            this.nickname = nickname;
            this.isSelected = isSelected;
        }
    }

    private Context mContext;
    private int mResourceId;
    private List<User> mUsers;
    private boolean selectable;

    public UserAdapter(Context context, int resourceId, List<User> users, boolean selectable) {
        mContext = context;
        mResourceId = resourceId;
        mUsers = users;
        this.selectable = selectable;
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return mUsers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        User user = mUsers.get(i);
        View view;
        UserAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(mResourceId, null);
            viewHolder = new UserAdapter.ViewHolder();
            viewHolder.nickname = (TextView) view.findViewById(R.id.nickname_textView);
            viewHolder.username = (TextView) view.findViewById(R.id.username_textView);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkBox);

            if (!selectable) {
                viewHolder.checkBox.setEnabled(false);
                viewHolder.checkBox.setVisibility(View.GONE);
            }

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (UserAdapter.ViewHolder) view.getTag();
        }

        viewHolder.checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                user.isSelected = b;
            }
        });

        viewHolder.nickname.setText(user.nickname);
        viewHolder.username.setText(user.username);
        viewHolder.checkBox.setChecked(user.isSelected);
        return view;
    }

    class ViewHolder {
        TextView username;
        TextView nickname;
        CheckBox checkBox;
    }

    public List<User> getSelectedUsers() {
        List<User> selectedUsers = new ArrayList<>();
        for (User user : mUsers) {
            if(user.isSelected)
                selectedUsers.add(user);
        }
        return selectedUsers;
    }

    public void reset() {
        for (User user : mUsers) {
            user.isSelected = false;
        }
        notifyDataSetChanged();
    }
}
