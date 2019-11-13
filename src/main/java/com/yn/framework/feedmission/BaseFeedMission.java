package com.yn.framework.feedmission;


import android.content.Context;

import com.yn.framework.controller.BackTask;
import com.yn.framework.controller.OnCreateNewNetworkTaskListener;
import com.yn.framework.http.HttpExecute;
import com.yn.framework.http.HttpVisitCallBack;
import com.yn.framework.system.ContextManager;
import com.yn.framework.system.StringUtil;
import com.yn.framework.system.UrlUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yn.framework.system.StringUtil.isEmpty;


/**
 * 这个类主要是用来发送网络请求，装载一些需要发送的数据
 */
public class BaseFeedMission {

    private static final Map<Integer, HttpExecute.NetworkTask> CACHE_TASK = new HashMap<>();


    protected HttpVisitCallBack mBaseController;
    protected HttpExecute mHttpExecute;
    private OnCreateNewNetworkTaskListener mOnCreateNewNetworkTaskListener;
    private Context mContext;

    public BaseFeedMission(Context context, HttpVisitCallBack httpVisitCallBack) {
        mBaseController = httpVisitCallBack;
        mContext = context;
        mHttpExecute = new HttpExecute();
    }

    public void setOnCreateNewBackTaskListener(OnCreateNewNetworkTaskListener l) {
        mOnCreateNewNetworkTaskListener = l;
    }

    public void sendMessage(HttpExecute.NetworkTask task) {
        task.call = mBaseController;
        if (!StringUtil.isURL(task.url)) {
            task.url = UrlUtil.getUrl(task);
        }
        task.context = mContext;
        mHttpExecute.execute(task);
    }

    public void sendMessage(int param, String... values) {
        sendMessage(param, -1, values);
    }

    public void sendMessage(int param, int call, String... values) {
        sendMessage("", param, call, values);
    }

    public void sendMessage(String backMethod, int param, int call, String... values) {
        HttpExecute.NetworkTask task = getBackTask(backMethod, param, call, values);
        if (mOnCreateNewNetworkTaskListener != null) {
            mOnCreateNewNetworkTaskListener.onNewNetworkTask(task);
        }
        sendMessage(task);
    }

    public static HttpExecute.NetworkTask getBackTask(int param, String... value) {
        return getBackTask("", param, -1, value);
    }

    public static HttpExecute.NetworkTask getBackTask(String backMethod, int param, int call, String... values) {
        HttpExecute.NetworkTask task = CACHE_TASK.get(param);
        if (task == null) {
            String params[] = ContextManager.getArrayString(param);
            task = new HttpExecute.NetworkTask();
            task.backTask = BackTask.build(call);
            task.backTask.method = backMethod;
            for (int i = 0; i < params.length; i++) {
                String result = params[i];
                params[i] = params[i].toLowerCase();
                if (params[i].contains("url:")) {
                    task.url = getString(result);
                } else if (params[i].contains("host")) {
                    task.hostIndex = Integer.parseInt(getString(result));
                } else if (params[i].contains("call:")) {
                    try {
                        task.backTask.callInterface = Integer.parseInt(getString(result));
                    } catch (Exception e) {
                        throw new NullPointerException("call must is integer");
                    }
                } else if (params[i].toLowerCase().contains("key:")) {
                    task.keys = getArray(result);
                } else if (params[i].toLowerCase().contains("value:")) {
                    task.values = getArray(result);
                } else if (params[i].contains("method:") && isEmpty(task.backTask.method)) {
                    task.backTask.method = getString(result);
                } else if (params[i].contains("methoderror:") && isEmpty(task.backTask.methodError)) {
                    task.backTask.methodError = getString(result);
                } else if (params[i].contains("methodstart:") && isEmpty(task.backTask.methodStart)) {
                    task.backTask.methodStart = getString(result);
                } else if (params[i].contains("http:")) {
                    String method = getString(params[i]);
                    if (method == null || method.length() == 0) continue;
                    if (method.contains("get")) {
                        task.method = HttpExecute.METHOD_GET;
                    } else if (method.contains("put")) {
                        task.method = HttpExecute.METHOD_PUT;
                    } else if (method.contains("delete")) {
                        task.method = HttpExecute.METHOD_DELETE;
                    } else if (method.contains("head")) {
                        task.method = HttpExecute.METHOD_HEAD;
                    }
                } else if (params[i].contains("cache:")) {
                    task.cache = getString(result);
                } else if (params[i].contains("cacheaddparam:")) {
                    try {
                        task.isCacheAdd = Boolean.parseBoolean(getString(params[i]));
                    } catch (Exception e) {
                        throw new NullPointerException("cacheaddparams must is false or true");
                    }
                } else if (params[i].contains("ischecksingle")) {
                    task.isCheckSingle = Boolean.parseBoolean(params[i]);
                } else if (params[i].contains("char:")) {
                    task.charSet = getString(getString(result));
                } else if (params[i].contains("istoast:")) {
                    task.backTask.isToast = Boolean.parseBoolean(getString(params[i]));
                } else if (params[i].contains("isprogress")) {
                    task.backTask.isProgress = Boolean.parseBoolean(getString(params[i]));
                } else if (params[i].contains("json")) {
                    task.isSendJson = Boolean.parseBoolean(getString(params[i]));
                } else if (params[i].contains("isshowerrorview")) {
                    task.backTask.isShowErrorView = Boolean.parseBoolean(getString(params[i]));
                }
            }
            CACHE_TASK.put(param, task);
//            SystemUtil.printlnInfo("添加缓存的cache");
        } else {
//            SystemUtil.printlnInfo("获取缓存的cache");
        }
        task = task.copy();
        if (isEmpty(task.backTask.method) && !isEmpty(backMethod)) {
            task.backTask.method = backMethod;
        }
        if (call != -1) {
            task.backTask.callInterface = call;
        }
        if (task.values != null && task.keys != null && task.keys.length != 0 && values != null && values.length != 0) {
            String newValues[] = new String[task.keys.length];
            System.arraycopy(values, 0, newValues, 0, values.length);
            System.arraycopy(task.values, 0, newValues, values.length, task.values.length);
            task.values = newValues;
            values = null;
        }
        if (values != null && values.length != 0) {
            task.values = values;
        }
        StringBuilder cacheStringBuilder = new StringBuilder(StringUtil.getString(task.cache));
        if (task.isCacheAdd) {
            if (task.values != null) {
                for (int i = 0; i < task.values.length; i++) {
                    cacheStringBuilder.append(task.values[i]);
                }
            }
        }
        if (cacheStringBuilder != null && cacheStringBuilder.length() != 0) {
            task.backTask.cacheKey = task.url + cacheStringBuilder.toString();
        }
        if (task.url.contains("${")) {
            List<String> keyList = new ArrayList<>();
            List<String> valueList = new ArrayList<>();
            for (int i = 0; i < task.keys.length; i++) {
                if (task.keys[i].contains("${")) {
                    task.url = task.url.replace(task.keys[i], task.values[i]);
                } else {
                    keyList.add(task.keys[i]);
                    valueList.add(task.values[i]);
                }
            }
            task.keys = new String[keyList.size()];
            task.values = new String[valueList.size()];
            valueList.toArray(task.values);
            keyList.toArray(task.keys);
        }
        return task;
    }

    private static String[] getArray(String s) {
        s = getString(s);
        if (s == null || s.length() == 0) return new String[0];
        return s.split(",");
    }

    private static String getString(String s) {
        int index = s.indexOf(":");
        if (index == -1) return null;
        return s.substring(index + 1, s.length());
    }
}
