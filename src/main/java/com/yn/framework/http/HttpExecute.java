package com.yn.framework.http;


import android.content.Context;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yn.framework.R;
import com.yn.framework.controller.BackTask;
import com.yn.framework.controller.BaseController;
import com.yn.framework.data.JSON;
import com.yn.framework.data.UserSharePreferences;
import com.yn.framework.data.YNSharedPreferences;
import com.yn.framework.exception.YNVisitNetworkFailException;
import com.yn.framework.exception.YNVisitNetworkSuccessException;
import com.yn.framework.exception.YNVisitTokenFailureException;
import com.yn.framework.remind.ToastUtil;
import com.yn.framework.system.BuildConfig;
import com.yn.framework.system.ContextManager;
import com.yn.framework.system.MethodUtil;
import com.yn.framework.system.StringUtil;
import com.yn.framework.system.SystemUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yn.framework.data.UserSharePreferences.getToken;


public class HttpExecute {

    private static final String UID_PREF = "uniqid_pref";

    public static final String PARAM_UNIQID = "uniqid";

    public static final String PARAM_MODEL = "model";

    public static final String PARAM_APPID = "appid";

    public static final String PARAM_MAC = "mac";

    public static final String PARAM_OS = "os_version";

    public static final String PARAM_SCREEN = "screen";

    public static final String PARAM_FROM = "from";

    public static final String PARAM_VERSION = "version";

    public static final String PARAM_VERSIONCODE = "versioncode";

    public static String UMENG_KEY = null;

    //post请求
    public static HttpRequest.HttpMethod METHOD_POST = HttpRequest.HttpMethod.POST;
    //get请求
    public static final HttpRequest.HttpMethod METHOD_GET = HttpRequest.HttpMethod.GET;
    //PUT请求
    public static final HttpRequest.HttpMethod METHOD_PUT = HttpRequest.HttpMethod.PUT;
    //DELETE请求
    public static final HttpRequest.HttpMethod METHOD_DELETE = HttpRequest.HttpMethod.DELETE;
    //OPTIONS
    public static final HttpRequest.HttpMethod METHOD_OPTIONS = HttpRequest.HttpMethod.OPTIONS;
    //HEAD
    public static final HttpRequest.HttpMethod METHOD_HEAD = HttpRequest.HttpMethod.HEAD;
    //TRACE
    public static final HttpRequest.HttpMethod METHOD_TRACE = HttpRequest.HttpMethod.TRACE;
    //CONNECT
    public static final HttpRequest.HttpMethod METHOD_CONNECT = HttpRequest.HttpMethod.CONNECT;
    //copy
    public static final HttpRequest.HttpMethod METHOD_COPY = HttpRequest.HttpMethod.COPY;

    NetworkTask mTask = null;

    public void execute(final NetworkTask task) {
        if (task == null) {
            throw new NullPointerException("NetworkTask is not null");
        }
        if (task.isCheckSingle) {
            try {
                if (!HttpManager.HTTP_MANAGER.checkoutHttp(task)) return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mTask = task;
        //判断当前是否有可用
//        if (!SystemUtil.isNetworkAvailable()) {
//            if (mTask.backTask.isToast) {
//                ToastUtil.showFailMessage("当前网络不可用，请检查网络设置");
//            }
//            mTask.call.getCache(mTask.backTask);
//            mTask.call.visitNetworkFail("", mTask.backTask);
//            return;
//        }
        task.oldUrl = task.url;
        StringBuilder result = new StringBuilder("\nurl = " + task.url + "\n");
        RequestParams requestParams = new RequestParams();

        if (mTask.keys != null && mTask.values != null) {
            if (mTask.keys.length != mTask.values.length) {
                SystemUtil.printlnInfo("url = %s http key.length != value.length", task.url);
            }
            for (int i = 0; i < mTask.keys.length; i++) {
                result.append(mTask.keys[i]).append(" : ").append(mTask.values[i]).append("\n");
                requestParams.addBodyParameter(mTask.keys[i], mTask.values[i]);
            }
        }

        if (mTask.call != null) {
            mTask.call.visitNetworkStart(mTask.backTask);
        }
        String params;
        if (task.isSendJson) {
            params = task.values[0];
        } else {
            params = new JSON(mTask.keys, mTask.values).toString();
        }
        if (task.hostIndex == 0 || task.isSendJson) {
            HttpUtil.http(task.url, params, new HttpUtil.OnHttpBack() {
                @Override
                public void start() {

                }

                @Override
                public void onSuccess(String response) {
                    if (task.hostIndex == 0) {
                        dealHttpInfo(response);
                    } else {
                        dealNewResponse(new JSON(response).getString("data"));
                    }
                }

                @Override
                public void onError() {
                    if (mTask.call != null) {
                        if (mTask.backTask != null && mTask.backTask.isToast) {
                            ToastUtil.showFailMessage(R.string.hfh_network_shutdown);
                        }
                        mTask.call.visitNetworkFail("", mTask.backTask);
                    }
                }
            }, task.method);
        } else {
            requestParams.addHeader("Authorization", "Bearer " + getToken());
            requestParams.addHeader("versionCode", BuildConfig.VERSION_NAME);
            if (params == null) {
                new Http(ContextManager.getContext()).send(HttpRequest.HttpMethod.GET, task.url, getBackCall());
            } else {
                new Http(ContextManager.getContext()).send(mTask.method, task.url, dealRequestParams(requestParams), getBackCall());
            }
        }
        SystemUtil.printlnInfo(result.toString());
    }

    private RequestCallBack<String> getBackCall() {
        return new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dealNewResponse(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                super.onFailure(error, msg);
                SystemUtil.printlnInfo(msg);
                if (mTask.call != null) {
                    if (mTask.backTask != null && mTask.backTask.isToast) {
                        ToastUtil.showFailMessage(R.string.hfh_network_shutdown);
                    }
                    mTask.call.visitNetworkFail("", mTask.backTask);
                }
            }
        };
    }

    private void dealNewResponse(String s) {
        String response = s;
        try {
            JSONObject jsonObject = new JSONObject(s);
            jsonObject.put(HttpConfig.STATUS_KEY, jsonObject.get("errcode"));
            jsonObject.put(HttpConfig.DATA_KEY, jsonObject.get("result"));
            jsonObject.put(HttpConfig.ERROR_KEY, jsonObject.get("errmsg"));
            jsonObject.remove("errmsg");
            jsonObject.remove("errcode");
            jsonObject.remove("result");
            response = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dealHttpInfo(response);
    }

    //处理返回参数
    private void dealHttpInfo(String response) {
        SystemUtil.printlnInfo("\nurl = " + mTask.url + "\t\t\n获取数据:" + response);
        try {
            if (mTask.call.visitAllNetworkSuccess(response, mTask.backTask)) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSON json = new JSON(response);

        int status = -1;
        try {
            status = Integer.parseInt(json.getString(HttpConfig.STATUS_KEY));
        } catch (Exception e) {
            e.printStackTrace();
            //数据异常
            if (!BuildConfig.ENVIRONMENT) {
                if (mTask.backTask != null && mTask.backTask.isToast) {
                    ToastUtil.showFailMessage("服务器数据异常");
                }
                String key = "url = " + mTask.url + "\n result = " + response + "\n" + new Date().toLocaleString() + "\n";
                key += YNSharedPreferences.getInfo("erro", "erro");
                YNSharedPreferences.saveInfo("erro", key, "erro");
            }
        }

        if (isRightStatue(status)) {
            try {
                String data = json.getString(HttpConfig.DATA_KEY);
                if ("{}".equals(data)) {
                    data = "";
                }
                BaseController.saveCache(data, null, mTask.backTask);
                mTask.call.visitNetworkSuccess(data, mTask.backTask);
            } catch (Exception e) {
                e.printStackTrace();
                new YNVisitNetworkSuccessException(e).throwException();
            }

        } else if (Integer.parseInt(HttpConfig.TOKEN_FAIL_KEY) == status) {
            //失效token
            try {
                mTask.call.visitTokenFailure(mTask);
            } catch (Exception e) {
                e.printStackTrace();
                new YNVisitTokenFailureException(e).throwException();
            }
        } else {
            String error = json.getStrings(HttpConfig.ERROR_KEY);
            if (mTask.backTask != null && mTask.backTask.isToast) {
                ToastUtil.showNormalMessageHandler(error);
            }
            try {
                mTask.call.visitNetworkFail(response, mTask.backTask);
            } catch (Exception e) {
                e.printStackTrace();
                new YNVisitNetworkFailException(e).throwException();
            }
        }
    }


    public RequestParams dealRequestParams(RequestParams params) {
        if (params == null) params = new RequestParams();
        params.addBodyParameter(PARAM_MODEL, SystemUtil.getPhone());
        params.addBodyParameter(PARAM_UNIQID, SystemUtil.getDeviceId(ContextManager.getContext()));
        params.addBodyParameter(PARAM_APPID, "1");
        params.addBodyParameter(PARAM_OS, SystemUtil.getAndroidId());
        int wh[] = SystemUtil.getPhoneScreenWH(ContextManager.getContext());
        params.addBodyParameter(PARAM_SCREEN, wh[0] + "," + wh[1]);
        params.addBodyParameter(PARAM_VERSION, BuildConfig.VERSION_NAME);
        params.addBodyParameter(PARAM_VERSIONCODE, BuildConfig.VERSION_CODE);
        params.addBodyParameter(PARAM_MAC, SystemUtil.getMac());
        params.addBodyParameter("token", getToken());
        params.addBodyParameter("userId", UserSharePreferences.getId());

        params.addBodyParameter(PARAM_FROM, UMENG_KEY);
        return params;
    }


    private boolean isRightStatue(int statue) {
        String text = HttpConfig.STATUS_SUCCESS_VALUE;
        try {
            int a = Integer.parseInt(text);
            return a == statue;
        } catch (Exception e) {
        }

        if (text.contains(">")) {
            try {
                int a = Integer.parseInt(text.substring(1, text.length()));
                return statue > a;
            } catch (Exception e) {
                throw new NullPointerException("string.xml of yn_status_right must > number or number");
            }
        }

        return false;
    }

    public static class NetworkTask {
        public HttpRequest.HttpMethod method = METHOD_POST;
        public String url = null;
        public String oldUrl = null;
        public HttpVisitCallBack call = null;
        public String charSet = "UTF-8";
        public String keys[];
        public String values[];
        public boolean isSendJson;
        public boolean isCheckSingle = true;
        public Object params;
        public int hostIndex = 0;
        public List<Head> head = null;
        public BackTask backTask;
        public Context context;
        public boolean notCheckout = false;
        public String cache;
        public boolean isCacheAdd;

        public NetworkTask copy() {
            NetworkTask task = new NetworkTask();
            task.method = method;
            task.url = url;
            task.oldUrl = oldUrl;
            task.call = call;
            task.charSet = charSet;
            task.keys = keys;
            task.values = values;
            task.isSendJson = isSendJson;
            task.isCheckSingle = isCheckSingle;
            task.params = params;
            task.hostIndex = hostIndex;
            task.head = head;
            task.backTask = backTask.copy();
            task.notCheckout = notCheckout;
            task.cache = cache;
            task.isCacheAdd = isCacheAdd;
            return task;
        }

    }


    private void getGetParams() {
        if (mTask.keys == null || mTask.values == null) {
            if (mTask.params != null) {
                List<String> keys = new ArrayList<>();
                List<String> values = new ArrayList<>();
                MethodUtil.getParams(mTask.params, keys, values);
                mTask.keys = new String[keys.size()];
                mTask.values = new String[values.size()];
                keys.toArray(mTask.keys);
                values.toArray(mTask.values);
            }
        }

        boolean is = false;
        if (mTask.keys != null && mTask.values != null && mTask.keys.length != 0 && mTask.values.length != 0) {
            if (mTask.keys.length != mTask.values.length) {
                throw new ArrayIndexOutOfBoundsException("task.key的长度和task.value的长度不等");
            }
            String params = "";
            boolean isFirst = false;
            for (int i = 0; i < mTask.keys.length; i++) {
                //添加在后面的
                if (mTask.keys[i].equals("@add_url")) {
                    mTask.url += "/" + mTask.values[i];
                } else {
                    if (!isFirst) {
                        isFirst = true;
                        is = true;
                        params += "?" + mTask.keys[i] + "=" + mTask.values[i];
                        continue;
                    }
                    params += "&" + mTask.keys[i] + "=" + mTask.values[i];
                    SystemUtil.printlnInfo(mTask.keys[i] + "=" + mTask.values[i]);
                }
            }
            mTask.url += params;
        }
        String token = getToken();
        //get请求添加token到后面
        if (!StringUtil.isEmpty(token)) {
            if (!is) {
                mTask.url += "?token=" + URLEncoder.encode(token);
            } else {
                mTask.url += "&token=" + URLEncoder.encode(token);
            }
        }
    }


    public static class Head {
        private String name;
        private String values;

        public Head(String name, String value) {
            setName(name);
            setValues(value);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValues() {
            return values;
        }

        public void setValues(String values) {
            this.values = values;
        }


    }

}
