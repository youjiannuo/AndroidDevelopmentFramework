package com.yn.framework.review.manager;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yn.framework.activity.YNCommonActivity;
import com.yn.framework.controller.BackTask;
import com.yn.framework.controller.BaseController;
import com.yn.framework.controller.OnCreateNewNetworkTaskListener;
import com.yn.framework.data.JSON;
import com.yn.framework.data.MyGson;
import com.yn.framework.http.HttpExecute;
import com.yn.framework.interfaceview.YNOperationRemindView;
import com.yn.framework.remind.ToastUtil;
import com.yn.framework.system.StringUtil;
import com.yn.framework.view.YNOperationListView;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * Created by youjiannuo on 16/3/17.
 */
public class YNController extends BaseController {

    private Class mClass;
    //发送http请求的参数配置
    private int mHttp;
    //发送参数
    private String mValues[];
    //获取数据
    private String mDataName;
    //子节点对应的key
    private String mChildName;
    //是否有加载更多
    private boolean mLoreMore = false;
    //加载显示加载错误
    private boolean mShowError = false;
    //回调
    private OnHttpDataListener mOnHttpDataListener;
    //显示的条数
    private int mShowListNum = -1;
    //是否设置的是ExpandListView
    private boolean mExpandListView = false;

    //分页类型
    public int mTypePage = -1;

    public YNController(YNCommonActivity activity) {
        super(activity);
    }

    public YNController(Object methodObj, YNCommonActivity activity) {
        super(methodObj, activity);
    }

    public YNController(YNOperationRemindView hfhFrameLayout, Object methodObj) {
        super(hfhFrameLayout, methodObj);
    }

    public YNController(YNOperationRemindView frameLayout) {
        super(frameLayout);
    }

    public YNController(YNCommonActivity activity, YNOperationListView YNOperationListView) {
        super(activity, YNOperationListView);
    }

    public YNController(YNOperationRemindView hfhFrameLayout, YNOperationListView YNOperationListView) {
        super(hfhFrameLayout, YNOperationListView);
    }

    public void getList(int http, int pageNumber, String... values) {
        if (http == 0) {
            ToastUtil.showFailMessage("listView 发送网络请求，请使用设置app:list_http");
        }
        mHttp = http;
        mValues = values;
        mPage = PAGE_START;
        getListInfo(PAGE_START, pageNumber, BaseController.GET_LIST_USER_INFO_START);
    }

    public YNController setDataName(String name) {
        mDataName = name;
        return this;
    }

    public void setChildName(String name) {
        mChildName = name;
    }

    //加载错误需要显示
    public void showError(boolean is) {
        mShowError = is;
    }

    @Override
    protected void getListInfo(final int page, int pageNumber, int call) {
        super.getListInfo(page, pageNumber, call);
        if (pageNumber <= 0 || pageNumber == Integer.MAX_VALUE) {
            sendMessage(mHttp, call, mValues);
        } else if (mTypePage == 1) {
            final String values[];
            values = new String[mValues.length + 2];
            values[0] = valueOf(page);
            values[1] = valueOf(pageNumber);
            System.arraycopy(mValues, 0, values, 2, mValues.length);
            sendMessage(mHttp, call, values);
        } else {
            final String values[];
            values = new String[mValues.length + 2];
            values[0] = valueOf(page * pageNumber);
            values[1] = valueOf(pageNumber);
            System.arraycopy(mValues, 0, values, 2, mValues.length);
            setOnCreateNewBackTaskListener(new OnCreateNewNetworkTaskListener() {
                @Override
                public void onNewNetworkTask(HttpExecute.NetworkTask backTask) {
                    if (backTask.hostIndex == 2 && backTask.values.length > 1) {
                        backTask.values[0] = valueOf(page + 1);
                    } else if (backTask.hostIndex == 2 && backTask.method == HttpRequest.HttpMethod.GET) {
                        int i = backTask.url.indexOf("?page=");
                        if (i != -1) {
                            String end = backTask.url.substring(i + 6);
                            int j = end.indexOf("&");
                            backTask.url = backTask.url.substring(0, i + 6) + (page + 1) + end.substring(j, end.length());
                        }
                    }
                }
            });
            sendMessage(mHttp, call, values);
        }
    }


    public YNController setJsonClass(Class cls) {
        mClass = cls;
        return this;
    }

    public void setOnHttpDataListener(OnHttpDataListener l) {
        mOnHttpDataListener = l;
    }


    @Override
    public Object visitBackgroundSuccess(final Object object, BackTask backTask) {
        if (mYNOperationListView != null) {
            if (mOnHttpDataListener != null && mActivity != null) {
                mActivity.getMainHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mOnHttpDataListener.onHttpData(object);
                    }
                });
            }
            if (object instanceof List) {
                return object;
            }
            String result;
            if (!StringUtil.isEmpty(mDataName)) {
                result = new JSON(object.toString()).getStrings(mDataName);
            } else {
                result = object.toString();
            }
            List data;
            if (mClass == JSON.class) {
                data = new ArrayList();
                List<String> jsonString = new MyGson().fromJson(result, new TypeToken<List<String>>() {
                }.getType());
                for (String item : jsonString) {
                    data.add(new JSON(item));
                }
            } else {
                if (mClass == null) {
                    mClass = String.class;
                }
                data = new MyGson().getList(mClass, result);
            }

            if (mShowListNum == -1 || mShowListNum >= data.size()) {
                return data;
            }
            List values = new ArrayList<>();
            for (int i = 0; i < mShowListNum; i++) {
                values.add(data.get(i));
            }
            return values;
        }

        if (mClass != null) {
            return new MyGson().fromJson(object.toString(), mClass);
        }
        return object.toString();

    }

//    @Override
//    public Object visitSuccess(Object object, BackTask backTask) {
//
//        if (mYNOperationListView != null) {
//            if (mOnHttpDataListener != null) {
//                mOnHttpDataListener.onHttpData(object);
//            }
//            if (object instanceof List) {
//                return object;
//            }
//            String result;
//            if (!StringUtil.isEmpty(mDataName)) {
//                result = new JSON(object.toString()).getStrings(mDataName);
//            } else {
//                result = object.toString();
//            }
//
//
//            List<String> data = new MyGson().fromJson(result, new TypeToken<List<String>>() {
//            }.getType());
//
//            //处理expandListView
//            if (mExpandListView) {
//                List<BaseExpandListViewModel<String, String>> models = new ArrayList<>();
//                for (int i = 0; i < data.size(); i++) {
//                    BaseExpandListViewModel<String, String> model = new BaseExpandListViewModel<>();
//                    model.setP(data.get(i));
//                    model.setC(new MyGson().<String>fromJson(new JSON(data.get(i)).getString(mChildName), new TypeToken<List<String>>() {
//                    }.getType()));
//                    models.add(model);
//                }
//                return models;
//            }
//
//            if (mShowListNum == -1 || mShowListNum >= data.size()) {
//                return data;
//            }
//            List<String> values = new ArrayList<>();
//            for (int i = 0; i < mShowListNum; i++) {
//                values.add(data.get(i));
//            }
//
//            return values;
//
//        }
//
//        if (mClass != null) {
//            return new MyGson().fromJson(object.toString(), mClass);
//        }
//        return object.toString();
//    }

    public void setExpandListView(boolean oos) {
        mExpandListView = oos;
    }


    @Override
    public Object visitFail(Object obj, BackTask backTask) {
        if (mYNOperationListView == null) {
            if (backTask.isShowErrorView && mShowError && !backTask.isGetCache) {
                mYNOperationRemindView.showLoadFailDialog();
            }
        }
        return obj == null ? "error" : obj.toString();
    }

    public void setShowListNum(int num) {
        mShowListNum = num;
    }

    public interface OnHttpDataListener {
        void onHttpData(Object data);
    }

}

