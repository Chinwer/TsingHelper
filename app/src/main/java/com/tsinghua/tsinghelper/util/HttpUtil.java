package com.tsinghua.tsinghelper.util;

import java.util.HashMap;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {

    public static final String SERVER_URL = "http://192.168.1.116:3000/";
    public static final String USER_PREFIX = SERVER_URL + "users/";
    public static final String USER_LOGIN = USER_PREFIX + "login/";
    public static final String USER_REGISTER = USER_PREFIX + "register/";
    public static final String TASK_PREFIX = SERVER_URL + "tasks/";

    private static final OkHttpClient mClient = new OkHttpClient();

    // Initiate an asynchronous get request
    public static void get(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(callback);
    }

    // Initiate an asynchronous post request
    public static void post(String url, HashMap<String, String> params, Callback callback) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder().url(url).post(requestBody).build();
        mClient.newCall(request).enqueue(callback);
    }
}