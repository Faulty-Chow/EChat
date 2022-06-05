package com.example.echat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.FileUtils;
import android.util.Log;
import android.util.LruCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CacheUtil {
    public static String lastRequest_CacheDir = "lastRequest";
    public static String userInfo_CacheDir = "userInfo";
    public static String chatList_CacheDir = "chatList";
    public static String chatMessage_CacheDir = "chatMessage";

    // 运行时缓存
    public static Map<String, String> userInfoMap = new HashMap<>();

    public static void saveCache(Context context, String filename, String tag, String value) {
        SharedPreferences.Editor note = context.getSharedPreferences(filename, Context.MODE_PRIVATE).edit();
        note.putString(tag, value);
        note.commit();
    }

    /**
     * 将字符串数据保存到本地
     *
     * @param context  上下文
     * @param filename 生成XML的文件名
     * @param map      <生成XML中每条数据名,需要保存的数据>
     */
    public static void saveCache(Context context, String filename, Map<String, String> map) {
        SharedPreferences.Editor note = context.getSharedPreferences(filename, Context.MODE_PRIVATE).edit();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
            note.putString(entry.getKey(), entry.getValue());
        }
        note.commit();
    }

    /**
     * 从本地取出要保存的数据
     *
     * @param context  上下文
     * @param filename 文件名
     * @param dataname 生成XML中每条数据名
     * @return 对应的数据(找不到为NUll)
     */
    public static String getCache(Context context, String filename, String dataname) {
        SharedPreferences read = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return read.getString(dataname, null);
    }

    public static void saveCache(Context context, String filename, List<String> list, int mode) throws IOException {
        FileOutputStream fos = context.openFileOutput(filename, mode);
        for (String s : list) {
            try {
                fos.write((s + System.getProperty("line.separator")).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fos.flush();
        fos.close();
    }

    public static List<String> getCache(Context context, String filename) throws IOException {
        List<String> result = new ArrayList<>();
        FileInputStream fis = context.openFileInput(filename);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            result.add(line);
        }
        br.close();
        isr.close();
        fis.close();
        return result;
    }

    public static JSONArray List_to_JSONArray(List<String> list) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (String s : list) {
            JSONObject jsonObject = new JSONObject(s);
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public static List<String> JSONArray_to_List(JSONArray jsonArray) throws JSONException {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.get(i).toString());
        }
        return list;
    }

}
