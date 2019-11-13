package com.yn.framework.review;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.yn.framework.R;
import com.yn.framework.activity.BaseFragment;
import com.yn.framework.activity.YNCommonActivity;
import com.yn.framework.controller.BackTask;
import com.yn.framework.data.JSON;
import com.yn.framework.data.MyGson;
import com.yn.framework.http.HttpExecute;
import com.yn.framework.http.HttpVisitCallBack;
import com.yn.framework.http.HttpVisitCallBackImp;
import com.yn.framework.imageLoader.ImageLoaderOperationListener;
import com.yn.framework.review.manager.OnBackListener;
import com.yn.framework.review.manager.OnClickInterceptListener;
import com.yn.framework.review.manager.YNController;
import com.yn.framework.review.manager.YNManager;
import com.yn.framework.thread.YNAsyncTask;
import com.yn.framework.view.YJNListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yn.framework.system.StringUtil.isEmpty;


/**
 * Created by youjiannuo on 16/3/16.
 */
public class YNListView extends YJNListView<String> implements YNHttpOperation, OnYNOperation, YNController.OnHttpDataListener {

    int mValueLayout;
    int mTitleValue;
    int mHeadValue;
    int mFooterValue;
    protected List<Integer> mRes = null;
    int mLineColors = 0xFFF1F3F8;
    float mLineHeight = 1;
    boolean mLineVisible = true;
    int mBackground = R.drawable.hfh_border_gray_bg_white_click;
    private int mCol = 1;
    protected OnBackListener mOnBackListener;
    int mListHttp = 0;
    int mLoadMore = 10;
    boolean mShowAllView = false;
    boolean mShowLoadOver = true;
    int mItemHeight = WRAP_CONTENT;
    protected String mDataName = "";
    private String mClickKeys[];
    private int mShowListNum = -1; //显示条数
    //head佈局
    protected View mHeadView, mLoadMoreFootView, mFootView;
    protected View mDealView;

    //获取一次请求
    private boolean mFirstHttp = true;
    private YNController mController = null;
    private boolean mShowError = false;
    private OnBindListener mBindListener;
    private OnHttpListener mOnHttpListener;
    private OnClickInterceptListener mOnClickInterceptListener;
    //处理HTTP请求返回的数据
    private OnHttpListBack mOnHttpListBack;
    private HttpVisitCallBackImp mHttpVisitCallBackImp;
    private OnAllHttpListBack mOnAllHttpListBack;

    public YNListView(Context context) {
        super(context);
    }

    public YNListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.YNView);
        mValueLayout = array.getResourceId(R.styleable.YNView_layout_value, 0);
        mTitleValue = array.getResourceId(R.styleable.YNView_layout_title, 0);
        mHeadValue = array.getResourceId(R.styleable.YNView_layout_head, 0);
        mFooterValue = array.getResourceId(R.styleable.YNView_layout_foot, 0);
        mBackground = array.getResourceId(R.styleable.YNView_onItemBackground, mBackground);
        mCol = array.getInt(R.styleable.YNView_list_col, 1);
        mLoadMore = array.getInt(R.styleable.YNView_list_load_more, 10);
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
        if (mHeadValue != 0) {
            mHeadView = LayoutInflater.from(getContext()).inflate(mHeadValue, null);
            addHeaderView(mHeadView);
        }
        if (mFooterValue != 0) {
            mFootView = LayoutInflater.from(getContext()).inflate(mFooterValue, null);
            addFooterView(mFootView);
        }
        if (mShowLoadOver && isLoadMore()) {
            mLoadMoreFootView = LayoutInflater.from(getContext()).inflate(R.layout.y_view_foot_load_over, null);
            addFooterView(mLoadMoreFootView);
            mLoadMoreFootView.setVisibility(View.GONE);
        }
    }


    public void setFirstHttp(boolean firstHttp) {
        mFirstHttp = firstHttp;
    }

    public YNListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setLayoutValue(int layoutValue) {
        mValueLayout = layoutValue;
    }

    private ViewGroup getFragmentLayout() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        if (mLineVisible) {
            View view = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, (int) mLineHeight);
            view.setLayoutParams(params);
            view.setBackgroundColor(mLineColors);
            layout.addView(view);
        }
        return layout;
    }

    public void setLineVisible(boolean is) {
        mLineVisible = is;
    }

    @Override
    public void closeLoadMore() {
        super.closeLoadMore();
        if (mShowLoadOver && isLoadMore() && getList().size() > 10) {
            if (mLoadMoreFootView != null) {
                mLoadMoreFootView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void openLoadMore() {
        super.openLoadMore();
        if (mLoadMoreFootView != null) {
            mLoadMoreFootView.setVisibility(View.GONE);
        }
    }


    @Override
    protected boolean isLoadMore() {
        return mLoadMore > 0;
    }

    @Override
    public View createView(int position, String data) {
        View valueView = LayoutInflater.from(getContext()).inflate(mValueLayout, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, mItemHeight);
        valueView.setLayoutParams(params);
        ViewGroup viewGroup = getFragmentLayout();
        viewGroup.addView(valueView, 0);
        if (mTitleValue != 0) {
            View titleView = LayoutInflater.from(getContext()).inflate(mTitleValue, null);
            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            titleView.setLayoutParams(titleParams);
            viewGroup.addView(titleView, 1);
        }
        viewGroup.setBackgroundResource(mBackground);
        return viewGroup;
    }

    public void setOnClickInterceptListener(OnClickInterceptListener l) {
        mOnClickInterceptListener = l;
    }

    public void setBindListener(OnBindListener<String> l) {
        mBindListener = l;
    }

    protected boolean isOnInterceptHttpInfo() {
        return false;
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
                        YNListView.this.visitAllNetworkSuccess(obj, backTask);
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

    protected void visitAllNetworkSuccess(Object obj, BackTask backTask) {

    }

    public JSON json(String s) {
        return JSON.json(s);
    }

    //处理服务数据返回
    private void dealHttpBackInfo(String obj, BackTask backTask) {
        new YNAsyncTask<Object, Void, List<String>>() {

            private BackTask backTask;

            @Override
            protected List<String> doInBackground(Object... v) {
                backTask = (BackTask) v[1];
                String info = v[0].toString();
                if (mOnAllHttpListBack != null) {
                    mOnAllHttpListBack.onAllHttp(info);
                }
                onBackHttp(info);
                if (!isEmpty(mDataName)) {
                    info = new JSON(info).getStrings(mDataName);
                }
                return onHttpInfo(info);
            }

            @Override
            protected void onPostExecute(List<String> strings) {
                super.onPostExecute(strings);
                onUIThread(strings);
                if (mController != null) {
                    mController.visitNetworkSuccess(strings, backTask);
                }
            }
        }.executeOnExecutor(obj, backTask);
    }

    protected void onBackHttp(String http) {

    }

    protected List<String> onHttpInfo(String http) {
        return null;
    }

    protected void onUIThread(List<String> strings) {

    }

    @Override
    public void setHttpVisitCallBack(HttpVisitCallBack callBack) {

    }

    @Override
    public void setLoadError() {

    }

    public void setOnAllHttpListBack(OnAllHttpListBack onAllHttpListBack) {
        this.mOnAllHttpListBack = onAllHttpListBack;
    }

    @Override
    public void setViewData(View view, int position, String data) {
        mDealView = view;
        OnYNOperation operations[];
        if (view.getTag() == null) {
            if (mRes == null) {
                mRes = new ArrayList<>();
                YNManager.getResourceId((ViewGroup) view, mRes);
            }
            Map<Integer, View> map = new HashMap<>();
            operations = new OnYNOperation[mRes.size()];
            for (int i = 0; i < mRes.size(); i++) {
                View v = view.findViewById(mRes.get(i));
                operations[i] = (OnYNOperation) v;
                operations[i].setOnClickInterceptListener(mOnClickInterceptListener);
                map.put(mRes.get(i), v);
            }
            Model model = new Model(map, operations, data);
            view.setTag(model);
            initViewModel(model);
        } else {
            Model model = (Model) view.getTag();
            operations = model.onYNOperations;
        }
        if (mTitleValue != 0) {
            if (position == 0) {
                view.setClickable(false);
                ((ViewGroup) view).getChildAt(1).setVisibility(View.VISIBLE);
                ((ViewGroup) view).getChildAt(0).setVisibility(View.GONE);
                addFloatTitle("title", position, data, mTitleValue);
                return;
            } else {
                ((ViewGroup) view).getChildAt(1).setVisibility(View.GONE);
                ((ViewGroup) view).getChildAt(0).setVisibility(View.VISIBLE);
                view.setClickable(true);
            }
        } else view.setClickable(true);
        setClick(view, data);
        for (OnYNOperation operation : operations) {
            operation.setPosition(position);
            operation.setData(json(data));
            if (mOnBackListener != null && operation.getOnClick() != 0) {
                operation.setOnBackListener(mOnBackListener);
            }
        }

        if (mBindListener != null) {
            mBindListener.onBindView(view, position, data);
        }
    }

    protected <T> T findListViewById(int id) {
        Model model = (Model) mDealView.getTag();
        return model.getView(mDealView, id);
    }

    protected void initViewModel(Model model) {

    }


    /**
     * 设置发送网络请求的资源文件
     *
     * @param httpId
     */
    public void setHttpId(int httpId) {
        mListHttp = httpId;
    }

    public void setDataName(String dataName) {
        mDataName = dataName;
    }

    private void setClick(View view, final String data) {
        if (view != null) {
            if (view instanceof YNLinearLayout) {
                if (((YNLinearLayout) view).getOnClick() != 0) {
                    return;
                }
            }
            if (mOnItemClickListener != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    String s = data;

                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(s);
                        }
                    }
                });
            }
        }
    }

    public void setLoadMore(int loadMore) {
        this.mLoadMore = loadMore;
    }

    public View getHeadView() {
        return mHeadView;
    }

    public View getFootView() {
        return mFootView;
    }

    @Override
    public void setData(Object obj) {
        JSON json;
        if (obj instanceof JSON) {
            json = (JSON) obj;
        } else {
            json = json(obj.toString());
        }
        setHeadData(obj);
        if (!isEmpty(mDataName)) {
            List<String> list = setAdapter(json.getString(mDataName));
            if (getList().size() == 0 && list.size() == 0) {
                ((YNCommonActivity) getContext()).showLoadDataNullView();
            }
        }
    }

    public void setHeadData(Object obj) {
        if (mHeadView != null) {
            View view = ((ViewGroup) mHeadView).getChildAt(0);
            if (mHeadView instanceof OnYNOperation) {
                ((OnYNOperation) mHeadView).setData(obj);
            } else if (view instanceof YNLinearLayout) {
                ((YNLinearLayout) view).setData(obj);
            }
        }
    }

    @Override
    public int getType() {
        return 1;
    }

    public void setOnBackListener(OnBackListener l) {
        mOnBackListener = l;
    }

    @Override
    public void setImageLoaderOperationListener(ImageLoaderOperationListener l) {

    }

    public void changeDataAndNotifyDataSetChanged(String key, String value, int index) {
        changeData(key, value, index);
        notifyDataSetChanged();
    }

    public void changeData(String key, String value, int index) {
        List<String> result = getList();
        if (index >= 0 && index < result.size()) {
            String t = result.get(index);
            JSON json = new JSON(t);
            result.remove(index);
            String s = json.changeData(key, value).toString();
            if (!isEmpty(s)) {
                result.add(index, s);
            }
        }
    }

    @Override
    public void setAdapter(List<String> data) {
        if (data.size() == 0 && mLoadMoreFootView != null) {
            mLoadMoreFootView.setVisibility(GONE);
        }
        if (mTitleValue != 0) {
            data.add(0, "");
        }
        super.setAdapter(data);
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
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getColumn() {
        return mCol;
    }


    @Override
    public void setOnHttpListener(OnHttpListener l) {
        mOnHttpListener = l;
    }

    @Override
    public void showErrorView() {
        mShowError = true;
    }

    public YNController startHttp(String... values) {
        return startHttp((BaseFragment) null, values);
    }

    public YNController startHttp(BaseFragment baseFragment, String... values) {
        if (!(getAdapter() != null && mFirstHttp)) {
            if (mController == null) {
                if (baseFragment == null && getContext() instanceof YNCommonActivity) {
                    mController = new YNController((YNCommonActivity) getContext(), this);
                } else if (baseFragment != null) {
                    mController = new YNController(baseFragment, this);
                } else {
                    mController = new YNController(null, this);
                }
                initController(mController);
            }
            mController.setOnHttpDataListener(this);
            mController.showError(mShowError);
            mController.setDataName(mDataName);
            mController.setShowListNum(mShowListNum);
            mController.setOnHttpListBack(mOnHttpListBack);
            mController.getList(mListHttp, mLoadMore, getValues(values));
        }
        return mController;
    }

    protected void initController(YNController controller) {

    }

    public void startHttp(YNController controller, String... values) {
        mController = controller;
        controller.setOnHttpDataListener(this);
        controller.showError(mShowError);
        controller.setDataName(mDataName);
        controller.setShowListNum(mShowListNum);
        controller.getList(mListHttp, mLoadMore, getValues(values));
    }

    public void setOnHttpListBack(YNListView.OnHttpListBack onHttpListBack) {
        mOnHttpListBack = onHttpListBack;
    }

    public void refresh() {
        if (mController != null) {
            mController.refresh();
        }
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
    public int getPageNumber() {
        if (mLoadMore <= 0) return Integer.MAX_VALUE;
        return mLoadMore;
    }


    public List<String> setAdapter(String data) {
        List<String> list = new MyGson().fromJson(data, new TypeToken<List<String>>() {
        }.getType());
        setAdapter(list);
        return list;
    }


    @Override
    public boolean isShowAllView() {
        return mShowAllView;
    }

    @Override
    public void onHttpData(Object data) {
        if (mOnHttpListener != null) {
            mOnHttpListener.onHttpSuccess(data, this);
        }
    }

    public interface OnBindListener<T> {
        void onBindView(View view, int position, T data);
    }

    public static class Model {
        public Map<Integer, View> map;
        public OnYNOperation[] onYNOperations;
//        public JSON json;

        public Model(Map<Integer, View> map, OnYNOperation[] onYNOperations) {
            this.map = map;
            this.onYNOperations = onYNOperations;
        }

        public Model(Map<Integer, View> map, OnYNOperation[] onYNOperations, String data) {
            this(map, onYNOperations);
//            json = new JSON(data);
        }

        public <T> T getView(View v, int id) {
            View view = map.get(id);
            if (view == null) {
                view = v.findViewById(id);
                map.put(id, view);
            }
            return (T) view;
        }

    }


    //处理分页的接口
    public interface OnHttpListBack {
        //是否分页
        void onHttpInfo(Object view, String httpInfo);
    }

    public interface OnAllHttpListBack {
        void onAllHttp(String httpInfo);
    }


}
