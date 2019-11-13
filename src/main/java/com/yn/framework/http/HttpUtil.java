package com.yn.framework.http;

import android.os.Handler;
import android.os.Looper;

import com.lidroid.xutils.http.client.HttpRequest;
import com.yn.framework.data.JSON;
import com.yn.framework.data.LogManager;
import com.yn.framework.data.UserSharePreferences;
import com.yn.framework.system.SystemUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.yn.framework.system.StringUtil.isEmpty;

/**
 * Created by youjiannuo on 17/12/22.
 * Email by 382034324@qq.com
 */

public class HttpUtil {
    public static final MediaType JSON = MediaType.parse("application/json;charset=UTF-8");

    public final static int CONNECT_TIMEOUT = 60;
    public final static int READ_TIMEOUT = 100;
    public final static int WRITE_TIMEOUT = 60;
    public static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
            .build();

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public static void http(final String url, final String json, final OnHttpBack back, final HttpRequest.HttpMethod method) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    RequestBody body = RequestBody.create(JSON, json);

                    Request.Builder builder = new Request.Builder()
                            .addHeader("Content-Type", "application/json")
                            .url(url);
                    StringBuilder sb = new StringBuilder();
                    String token = UserSharePreferences.getToken();
                    if (!isEmpty(token)) {
                        builder.addHeader("Authorization", "Bearer " + token);
                        sb.append("token = ").append(token);
                    }
                    if (method == HttpRequest.HttpMethod.POST) {
                        builder.post(body);
                        sb.append("\n发送请求:post");
                    } else if (method == HttpRequest.HttpMethod.GET) {
                        builder.get();
                        sb.append("\n发送请求:get");
                    } else if (method == HttpRequest.HttpMethod.PUT) {
                        builder.put(body);
                        sb.append("\n发送请求:put");
                    }
                    SystemUtil.printlnInfo(sb.toString());
                    Request request = builder.build();
                    Response response;
                    response = CLIENT.newCall(request).execute();
                    final String keys[] = new String[]{HttpConfig.STATUS_KEY, HttpConfig.DATA_KEY, HttpConfig.ERROR_KEY};
                    final String status;
                    String data;
                    String error = "";
                    String value, message, tag;
                    if (response.body() == null) {
                        if (response == null) {
                            LogManager.addLog("错误日志信息为空");
                        } else {
                            LogManager.addLog("错误日志信息为空 " + response.toString());
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                back.onError();
                            }
                        });
                        return;
                    } else {
                        value = response.body().string();
                        message = response.message();
                        tag = request.toString();
                    }
                    int code = response.code();
                    if (code == 200 || code == 201) {
                        status = HttpConfig.STATUS_SUCCESS_VALUE;
                        data = value;
                        String t = response.header("Token", "");
                        if (!isEmpty(t)) {
                            SystemUtil.printlnInfo("获取token = " + t);
                            UserSharePreferences.saveToken(t);
                        }
                    } else {
                        status = response.code() + "";
                        error = new JSON(value).getString("message");
                        data = value;
                        LogManager.addLog("错误，访问接口:" + url + "  token = " + token + "  json = " + json + "  code =  " + code + "  response = " + value + "   message  =" + message + "   " + tag);
                    }
                    final String d = data;
                    final String e = error;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (back != null) {
                                back.onSuccess(new JSON(keys, new String[]{status, d, e}).toString());
                            }
                        }
                    });

                } catch (Exception e) {
                    LogManager.addLog("错误，访问接口:" + url + "  token = " + UserSharePreferences.getToken() + "  json = " + json + "   " + e.getMessage());
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (back != null) {
                                back.onError();
                            }
                        }
                    });
                }
            }
        }).start();

    }


    public interface OnHttpBack {
        void start();

        void onSuccess(String response);

        void onError();
    }


}
