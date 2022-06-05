package com.example.echat;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserInfo {
    public static String cacheFile = "userInfo";
    public static String uuid;
    public static String username;
    public static String password;
    public static String nickname;
    public static String phone;

    private final Context mContext;

    public UserInfo(Context context) {
        mContext = context;
        getUserInfoCache();
    }

    void getUserInfoCache() {
        uuid = CacheUtil.getCache(mContext, cacheFile, "uuid");
        username = CacheUtil.getCache(mContext, cacheFile, "username");
        password = CacheUtil.getCache(mContext, cacheFile, "password");
        nickname = CacheUtil.getCache(mContext, cacheFile, "nickname");
        phone = CacheUtil.getCache(mContext, cacheFile, "phone");
    }

    public static void updateCache(Context context) {
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("uuid", uuid);
        userInfo.put("username", username);
        userInfo.put("password", password);
        userInfo.put("nickname", nickname);
        userInfo.put("phone", phone);
        CacheUtil.saveCache(context, UserInfo.cacheFile, userInfo);
    }

    public static void clearCache(Context context) {
        CacheUtil.saveCache(context, UserInfo.cacheFile, "uuid", "");
        CacheUtil.saveCache(context, UserInfo.cacheFile, "username", "");
        CacheUtil.saveCache(context, UserInfo.cacheFile, "password", "");
        CacheUtil.saveCache(context, UserInfo.cacheFile, "nickname", "");
        CacheUtil.saveCache(context, UserInfo.cacheFile, "phone", "");
        uuid = null;
        username = null;
        password = null;
        nickname = null;
        phone = null;
    }
}
