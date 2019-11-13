package com.yn.framework.review;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.yn.framework.R;
import com.yn.framework.activity.BaseFragment;
import com.yn.framework.activity.YNCommonActivity;
import com.yn.framework.controller.BackTask;
import com.yn.framework.data.JSON;
import com.yn.framework.http.HttpExecute;
import com.yn.framework.http.HttpVisitCallBack;
import com.yn.framework.http.HttpVisitCallBackImp;
import com.yn.framework.imageLoader.ImageLoaderOperationListener;
import com.yn.framework.review.manager.OnBackListener;
import com.yn.framework.review.manager.OnClickInterceptListener;
import com.yn.framework.review.manager.YNController;
import com.yn.framework.review.manager.YNManager;
import com.yn.framework.system.SystemUtil;
import com.yn.framework.thread.YNAsyncTask;
import com.yn.framework.view.YJNRecyclerView;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yn.framework.system.StringUtil.isEmpty;

/**
 * Created by youjiannuo on 16/11/14
 */
public class YNRecyclerView<T> extends YJNRecyclerView<T>
        implements YNHttpOperation, OnYNOperation, YNController.OnHttpDataListener {


    int mTitleValue;
    int mHeadValue;
    int mFooterValue;
    int mLineColors;
    float mLineHeight;
    boolean mLineVisible;
    int mBackground = R.drawable.yn_click;
    private int mCol;
    private OnBackListener<T> mOnBackListener;
    protected int mListHttp;
    int mLoadMore;
    boolean mShowAllView;
    boolean mShowLoadOver;
    private String mDataName;
    private String mClickKeys[];
    private int mShowListNum; //显示条数
    //head佈局
    protected View mHeadView;
    protected View mFootView;
    protected View mLoadMoreFootView;
    private Class<T> clz;

    //获取一次请求
    private boolean mFirstHttp = true;
    private YNController mController = null;
    private boolean mShowError = false;
    private YNListView.OnBindListener mBindListener;
    private OnHttpListener mOnHttpListener;
    private OnClickInterceptListener mOnClickInterceptListener;
    private HttpVisitCallBackImp mHttpVisitCallBackImp;
    private Handler mHandler;

    public YNRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.YNView);
        mLayoutId = array.getResourceId(R.styleable.YNView_layout_value, 0);
        mTitleValue = array.getResourceId(R.styleable.YNView_layout_title, 0);
        mHeadValue = array.getResourceId(R.styleable.YNView_layout_head, 0);
        mFooterValue = array.getResourceId(R.styleable.YNView_layout_foot, 0);
        mBackground = array.getResourceId(R.styleable.YNView_onItemBackground, mBackground);
        mCol = array.getInt(R.styleable.YNView_list_col, 1);
        mLoadMore = array.getInt(R.styleable.YNView_list_load_more, -1);
        mListHttp = array.getResourceId(R.styleable.YNView_list_http, 0);
        mLineVisible = array.getBoolean(R.styleable.YNView_line_visible, true);
        mShowAllView = array.getBoolean(R.styleable.YNView_list_show_all_view, false);
        mItemHeight = (int) array.getDimension(R.styleable.YNView_list_item_height, WRAP_CONTENT);
        mLineHeight = array.getDimension(R.styleable.YNView_onItemLineHeight, 1);
        mLineColors = array.getColor(R.styleable.YNView_onItemLineColor, mLineColors);
        mDataName = array.getString(R.styleable.YNView_set_data_name);
        mFirstHttp = array.getBoolean(R.styleable.YNView_http_first, true);
        mShowLoadOver = array.getBoolean(R.styleable.YNView_list_show_load_over, true);
        mShowListNum = array.getInt(R.styleable.YNView_list_show_num, -1);
        String key = array.getString(R.styleable.YNView_onClickKey);
        if (!isEmpty(key)) {
            mClickKeys = key.split(",");
        }
        array.recycle();
//        if (mHeadValue != 0) {
//            mHeadView = LayoutInflater.from(getContext()).inflate(mHeadValue, null);
//            addHeaderView(mHeadView);
//        }
//        if (mFooterValue != 0) {
//            mFootView = LayoutInflater.from(getContext()).inflate(mFooterValue, null);
//            addFooterView(mFootView);
//        }
//        if (mShowLoadOver && isLoadMore()) {
//            mLoadMoreFootView = LayoutInflater.from(getContext()).inflate(R.layout.y_view_foot_load_over, null);
//            addFooterView(mLoadMoreFootView);
//            mLoadMoreFootView.setVisibility(View.GONE);
//        }
        setLoadMore();
    }

    @Override
    protected Object findView(View view) {
        Map<Integer, View> map = new HashMap<>();
        List<Integer> mRes = new ArrayList<>();
        YNManager.getResourceId((ViewGroup) view, mRes);
        OnYNOperation operations[] = new OnYNOperation[mRes.size()];
        for (int i = 0; i < mRes.size(); i++) {
            View v = view.findViewById(mRes.get(i));
            operations[i] = (OnYNOperation) v;
            operations[i].setOnClickInterceptListener(mOnClickInterceptListener);
            map.put(mRes.get(i), v);
        }

        return new Model(map, operations);
    }

    public void setFirstHttp(boolean is) {
        mFirstHttp = is;
    }

    @Override
    public boolean isHeadLoadMore() {
        return super.isHeadLoadMore();
    }

    @Override
    public boolean isLoadMore() {
        return mLoadMore > 0;
    }

    public void setBindListener(YNListView.OnBindListener mBindListener) {
        this.mBindListener = mBindListener;
    }

    protected View getViewById(View v, Model model, int id) {
        Map<Integer, View> map = model.map;
        View view = map.get(id);
        if (view == null) {
            view = v.findViewById(id);
            map.put(id, view);
        }
        return view;
    }


    protected boolean isOnInterceptHttpInfo() {
        return false;
    }

    @Override
    protected int getItemBackgroundResource() {
        if (mBackground == 0) {
            return super.getItemBackgroundResource();
        }
        return mBackground;
    }

    public void setDataClass(Class clz) {
        this.clz = clz;
    }

    @Override
    protected void onItemClick(View v) {
        super.onItemClick(v);
        if (mOnBackListener != null) {
            Model model = (Model) v.getTag();
            mOnBackListener.onItemClick(v, model.index, getItem(model.index));
        }
    }

    public void refresh() {
        mController.refresh();
    }

    @Override
    public final HttpVisitCallBack getHttpVisitCallBack(HttpVisitCallBack callBack) {
        if (isOnInterceptHttpInfo()) {
            if (mHttpVisitCallBackImp == null) {
                mHttpVisitCallBackImp = new HttpVisitCallBackImp() {
                    @Override
                    public void visitNetworkSuccess(Object obj, BackTask backTask) {
                        super.visitNetworkSuccess(obj, backTask);
                        dealHttpBackInfo(obj.toString(), backTask);
                    }

                    @Override
                    public void visitNetworkStart(BackTask backTask) {
                        super.visitNetworkStart(backTask);
                        mController.visitNetworkStart(backTask);
                    }

                    @Override
                    public boolean visitAllNetworkSuccess(Object obj, BackTask backTask) {
                        return mController.visitAllNetworkSuccess(obj, backTask);
                    }

                    @Override
                    public void visitNetworkFail(Object obj, BackTask backTask) {
                        super.visitNetworkFail(obj, backTask);
                        mController.visitNetworkFail(obj, backTask);
                    }

                    @Override
                    public void visitTokenFailure(HttpExecute.NetworkTask task) {
                        super.visitTokenFailure(task);
                        mController.visitTokenFailure(task);
                    }

                    @Override
                    public boolean getCache(BackTask task) {
                        return mController.getCache(task);
                    }

                };
            }
        }
        return mHttpVisitCallBackImp;
    }


    //处理服务数据返回
    private void dealHttpBackInfo(String obj, BackTask backTask) {
        new YNAsyncTask<Object, Void, List<T>>() {

            private BackTask backTask;

            @Override
            protected List<T> doInBackground(Object... v) {
                backTask = (BackTask) v[1];
                String info = v[0].toString();
                onBackHttp(info);
                if (!isEmpty(mDataName)) {
                    info = new JSON(info).getStrings(mDataName);
                }
                return onHttpInfo(info, backTask);
            }

            @Override
            protected void onPostExecute(List<T> strings) {
                super.onPostExecute(strings);
                if (mController != null) {
                    mController.visitNetworkSuccess(strings, backTask);
                }
            }
        }.executeOnExecutor(obj, backTask);
    }

    protected void onBackHttp(String http) {

    }

    protected List<T> onHttpInfo(String http, BackTask backTask) {
        return null;
    }


    @Override
    public int getPageNumber() {
        if (mLoadMore <= 0) return Integer.MAX_VALUE;
        return mLoadMore;
    }

    public void setLoadMore(int more) {
        mLoadMore = more;
    }


    @Override
    public void setViewData(View view, int position, final T data) {
        super.setViewData(view, position, data);
        if (data == null) {
            return;
        }
        Model model = (Model) view.getTag();
        model.index = position;
        OnYNOperation operations[] = model.onYNBackListener;
        if (mTitleValue != 0) {
            if (position == 0) {
                view.setClickable(false);
                ((ViewGroup) view).getChildAt(1).setVisibility(View.VISIBLE);
                ((ViewGroup) view).getChildAt(0).setVisibility(View.GONE);
                return;
            } else {
                ((ViewGroup) view).getChildAt(1).setVisibility(View.GONE);
                ((ViewGroup) view).getChildAt(0).setVisibility(View.VISIBLE);
                view.setClickable(true);
            }
        } else view.setClickable(true);

        for (OnYNOperation operation : operations) {
            operation.setImageLoaderOperationListener(this);
            operation.setPosition(position);
            operation.setData(data);
            if (operation.getOnClick() != 0 && mOnBackListener != null) {
                operation.setOnBackListener(mOnBackListener);
            }
        }

        if (mBindListener != null) {
            mBindListener.onBindView(view, position, data);
        }
    }

    public void setHttpId(int httpId) {
        mListHttp = httpId;
    }


    public View getHeadView() {
        return mHeadView;
    }

    public View getFootView() {
        return mFootView;
    }


    @Override
    public void setOnHttpListener(OnHttpListener l) {
        mOnHttpListener = l;
    }

    @Override
    public void showErrorView() {

    }

    @Override
    protected void reLoadMore() {
        super.reLoadMore();
        mController.reOnLoadMore();
    }

    public YNController startHttp(String... values) {
        return startHttp((BaseFragment) null, values);
    }


    public YNController startHttp(BaseFragment baseFragment, String... values) {
        if (!(getAdapter() != null && mFirstHttp)) {
            if (mController == null) {
                if (baseFragment == null) {
                    mController = new YNController((YNCommonActivity) getContext(), this);
                } else {
                    mController = new YNController(baseFragment, this);
                }
            }
            mController.setOnHttpDataListener(this);
            mController.showError(mShowError);
            mController.setDataName(mDataName);
            mController.setShowListNum(mShowListNum);
            mController.setJsonClass(getClz());
            mController.getList(mListHttp, mLoadMore, getValues(values));
        }
        if (mFirstHttp && getAdapter() != null) {
            SystemUtil.printlnInfo("提出设置了Adapter，所以导致请求无法继续");
        }
        return mController;
    }

    public void startHttp(YNController controller, String... values) {
        mController = controller;
        controller.setOnHttpDataListener(this);
        controller.showError(mShowError);
        controller.setDataName(mDataName);
        controller.setShowListNum(mShowListNum);
        controller.setJsonClass(getClz());
        controller.getList(mListHttp, mLoadMore, getValues(values));
    }

    @Override
    public YNController getYNController() {
        return mController;
    }

    public YNController getController() {
        return mController;
    }

    private String[] getValues(String values[]) {
        if (mClickKeys == null || mClickKeys.length == 0) {
            return values;
        }
        String data[] = new String[mClickKeys.length + values.length];
        Intent intent = ((Activity) getContext()).getIntent();
        for (int i = 0; i < mClickKeys.length; i++) {
            data[i] = intent.getStringExtra(mClickKeys[i]);
        }
        for (int i = mClickKeys.length; i < data.length; i++) {
            data[i] = values[i];
        }
        return data;
    }


    @Override
    public void setData(Object obj) {
        List<T> list = null;
        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            Object value = map.get(mDataName);
            if (value instanceof List) {
                list = (List<T>) value;
                setAdapter(list);
            }
        } else if (obj instanceof String) {
            JSON json = new JSON(obj.toString());
            setAdapter(json.getString(mDataName));
        } else if (obj instanceof JSON) {
            JSON json = (JSON) obj;
            setAdapter(json.getString(mDataName));
        }
        if (list != null && list.size() == 0) {
            ((YNCommonActivity) getContext()).showLoadDataNullView();
        }
    }

    public void changeDataAndNotifyDataSetChanged(String key, String value, int position) {
        changeData(key, value, position);
        notifyDataSetChanged();
    }

    public void changeData(String key, String value, int index) {
        List<String> result = (List<String>) getList();
        if (index >= 0 && index < result.size()) {
            JSON json = new JSON(result.get(index));
            result.remove(index);
            String s = json.changeData(key, value).toString();
            if (!isEmpty(s)) {
                result.add(index, s);
            }
        }
    }


    @SuppressWarnings("unchecked")
    public Class<T> getClz() {
        if (clz == null) {
            try {
                clz = (Class<T>) (((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
            } catch (Exception e) {
                e.printStackTrace();
                clz = (Class<T>) JSON.class;
            }
        }
        return clz;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public void setOnBackListener(OnBackListener l) {
        mOnBackListener = l;
        mIsClickItemListener = true;
        notifyDataSetChanged();
    }

    @Override
    public void setOnClickInterceptListener(OnClickInterceptListener l) {

    }

    @Override
    public void setImageLoaderOperationListener(ImageLoaderOperationListener l) {

    }

    @Override
    public void setPosition(int index) {

    }

    @Override
    public int getOnClick() {
        return 0;
    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public OnYNOperation[] getYNOperation() {
        return new OnYNOperation[0];
    }

    @Override
    public void setYNOperation(OnYNOperation[] operations) {

    }

    @Override
    public void onHttpData(final Object data) {
        post(new Runnable() {
            @Override
            public void run() {
                if (mOnHttpListener != null) {
                    mOnHttpListener.onHttpSuccess(data, YNRecyclerView.this);
                }
            }
        });

    }


    public static class Model {
        public int index;
        public Map<Integer, View> map;
        public OnYNOperation onYNBackListener[];
        public Map<String, Object> info;

        public Model(Map<Integer, View> map, OnYNOperation onYNBackListener[]) {
            this.map = map;
            this.onYNBackListener = onYNBackListener;

        }

        public View findView(View v, int viewId) {
            View childView = map.get(viewId);
            if (childView == null) {
                childView = v.findViewById(viewId);
                map.put(viewId, childView);
            }
            return childView;
        }

    }
}
