package com.example.echat;

import android.util.Log;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HttpUtil {
    public static String host = "http://192.168.31.229:80/EChat_Web_exploded";

    public static String login_url = host + "/AndroidLogin";
    public static String register_url = host + "/AndroidRegister";
    public static String chatInfo_url = host + "/AndroidChatInfo";
    public static String chatAction_url = host + "/AndroidChatAction";

    public void getSyn(final String url) {
        new Thread(() -> {
            try {
                //创建OkHttpClient对象
                OkHttpClient client = new OkHttpClient();
                //创建Request
                Request request = new Request.Builder()
                        .url(url)//访问连接
                        .get()
                        .build();
                //创建Call对象
                Call call = client.newCall(request);
                //通过execute()方法获得请求响应的Response对象
                Response response = call.execute();
                if (response.isSuccessful()) {
                    //处理网络请求的响应，处理UI需要在UI线程中处理
                    //...
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void getAsyn(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //...
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    //处理UI需要切换到UI线程处理
                }
            }
        });
    }

    public static void post(String url, Map params, Callback callback) {
        String cookie = "browser_uid=";
        if (UserInfo.uuid == null) {
            cookie += UUID.randomUUID().toString();
        } else {
            cookie += UserInfo.uuid;
        }
        post(url, params, callback, cookie);
    }

    public static void post(String url, Map params, Callback callback, String cookie) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        for (Object key : params.keySet()) {
            builder.add((String) key, (String) params.get(key));
        }
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .header("Cookie", cookie)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void post(String url, JSONObject jsonObject, Callback callback) {
        String cookie = "browser_uid=";
        if (UserInfo.uuid == null) {
            cookie += UUID.randomUUID().toString();
        } else {
            cookie += UserInfo.uuid;
        }
        post(url, jsonObject, callback, cookie);
    }

    public static void post(String url, JSONObject jsonObject, Callback callback, String cookie) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.Companion.parse("application/json;charset=utf-8");
        RequestBody body = RequestBody.Companion.create(jsonObject.toString(), mediaType);
        Request request = new Request.Builder()
                .url(url)
                .header("Cookie", cookie)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
