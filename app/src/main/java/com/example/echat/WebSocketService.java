package com.example.echat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketService extends Service {
    private static final String TAG = "WebSocketService";

    private OkHttpClient mClient;
    private WebSocket mWebSocket;

    class WebSocketServiceBinder extends Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }
    }

    private WebSocketServiceBinder mBinder = new WebSocketServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public interface onMessageListener {
        void onMessage(String message);
    }

    private Set<onMessageListener> activityListeners = new HashSet<>();

    public void subscriptionService(onMessageListener listener) {
        activityListeners.add(listener);
    }

    //监听事件，用于收消息，监听连接的状态
    class WsListener extends WebSocketListener {
        private static final String tag = "WebSocketListener";
        public static final int NOTIFY_CONNECT_ERROR_CODE = -1;
        public static final int NOTIFY_NEW_MESSAGE_CODE = 0;
        public static final int NOTIFY_NEW_CHAT_CODE = 1;

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
            Log.e(tag, "onClosed: " + code + " " + reason);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);
            Log.e(tag, "onClosing: " + code + " " + reason);
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            Log.e(tag, "onFailure: " + t.getMessage());
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
            Log.d(tag, "onMessage: " + text);
            for (onMessageListener activityListener : activityListeners) {
                activityListener.onMessage(text);
            }
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            super.onMessage(webSocket, bytes);
        }

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            Log.e(tag, "onOpen");
        }
    }


    //发送String消息
    public void send(final String message) {
        if (mWebSocket != null) {
            mWebSocket.send(message);
        }
    }

    /**
     * 发送byte消息
     *
     * @param message
     */
    public void send(final ByteString message) {
        if (mWebSocket != null) {
            mWebSocket.send(message);
        }
    }

    //主动断开连接
    public void disconnect(int code, String reason) {
        if (mWebSocket != null)
            mWebSocket.close(code, reason);
    }


    public void init(String url) {
        mClient = new OkHttpClient.Builder()
                .pingInterval(10, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        mWebSocket = mClient.newWebSocket(request, new WsListener());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}
