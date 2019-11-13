package com.yn.framework.view;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yn.framework.R;
import com.yn.framework.http.HttpVisitCallBack;
import com.yn.framework.imageLoader.ImageLoaderOperationListener;
import com.yn.framework.imageLoader.ImageLoaderScrollerObservation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.yn.framework.imageLoader.ImageLoaderScrollerObservation.TYPE_START;

/**
 * Created by youjiannuo on 16/11/14
 */
public class YJNRecyclerView<T> extends RecyclerView
        implements ListViewInterface<T>,
        YNOperationListView<T>,
        ImageLoaderScrollerObservation.OnListViewProxy,
        ImageLoaderOperationListener {

    protected final int WRAP_CONTENT = LayoutParams.WRAP_CONTENT;
    protected final int MATCH_PARENT = LayoutParams.MATCH_PARENT;

    protected static final int LAYOUT_LINEAR = 0;
    protected static final int LAYOUT_GRID = 1;

    private LayoutInflater mInflater;
    protected int mLayoutId;
    protected float mItemHeight;
    protected float mItemWidth = MATCH_PARENT;
    private List<T> mT;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected YNAdapter mYNAdapter;
    private AdapterProxy mAdapterProxy;
    private OnItemClickListener mOnItemClickListener;
    //是否需要添加点击事件
    protected boolean mIsClickItemListener = false;
    private OnItemLongClickListener mOnItemLongListener;
    private ItemTouchHelper mItemTouchHelper;
    //头部管理
    private HashMap<Integer, View> mOtherHashMap;
    //加载head
    private List<Integer> mHeadResourceViews;
    //加载底部
    private List<Integer> mFootResourceViews;
    //头部的加载更多
    private final int VIEW_HEAD_LORE_MORE = -500;
    //添加头部
    private final int VIEW_HEAD = -400;

    //加载更多的
    private final int VIEW_LORE_MORE = -100;
    //普通的
    private final int VIEW_NORMAL = 0;
    //添加底部
    private final int VIEW_FOOT = -300;

    private boolean mIsLoadMore;
    private boolean mIsHeadLoadMore;
    //监听是否处在滚动状态
    private ImageLoaderScrollerObservation mImageLoaderScrollerObservation;

    //正真的数据
    private int mContentCount = 0;
    private BucketListAdapter.LoadMoreListener mLoadMoreListener;
    private OnHeadLoadMore mOnHeadLoadMore;
    private YNSwipeRefreshLayout mSwipeRefreshLayout;
    //加载错误
    private View mLoadErrorView;
    private View mLoadProgressLayoutView;

    public YJNRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mAdapterProxy = new AdapterProxy();
        mInflater = LayoutInflater.from(context);
        mIsLoadMore = isLoadMore();
        mIsHeadLoadMore = isHeadLoadMore();
        if (getLayoutType() == LAYOUT_LINEAR) {
            mLayoutManager = new MyLayoutManager(getContext());
        }
        if (mIsHeadLoadMore || mIsLoadMore) {
            mOtherHashMap = new HashMap<>();
        }
        if (isOpenScrollerLoadImage()) {
            mImageLoaderScrollerObservation = new ImageLoaderScrollerObservation(this);
            addOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
//                    mImageLoaderScrollerObservation.changeState(newState);
                    mImageLoaderScrollerObservation.changeRecyclerState(newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }
    }


    protected void setLoadMore() {
        mIsLoadMore = isLoadMore();
        if (mIsLoadMore) {
            initMap();
        }
    }


    public List<T> setAdapter(String data) {
        List<T> list = new Gson().fromJson(data, new TypeToken<List<T>>() {
        }.getType());
        setAdapter(list);
        return list;
    }

    //是否需要启动处在高速滚动的时候，不需要加载图片
    public boolean isOpenScrollerLoadImage() {
        return false;
    }

    public boolean isHeadLoadMore() {
        return false;
    }

    //是否加载更多
    public boolean isLoadMore() {
        return false;
    }

    public View findViewByPosition(int position) {
        return mLayoutManager.findViewByPosition(position);
    }

    public void scrollToPositionWithOffset(int position, int offset) {
        ((LinearLayoutManager) mLayoutManager).scrollToPositionWithOffset(position, offset);
    }

    public void scrollToPositionLayout(int position) {
        mLayoutManager.scrollToPosition(position);
    }

    @Override
    public void setAdapter(List<T> data) {
        mT = data;
        mContentCount = mT.size();
        if (mYNAdapter == null) {
            mYNAdapter = new YNAdapter();
            mAdapterProxy.setAdapter(mYNAdapter);
            setLayoutManager(mLayoutManager);
            if (getLayoutType() == LAYOUT_LINEAR) {
                LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) mLayoutManager);
                linearLayoutManager.setStackFromEnd(isStackFromEnd());
                linearLayoutManager.setOrientation(getOrientation());
            }
            setItemAnimator(new DefaultItemAnimator());
            setAdapter(mYNAdapter);
        } else {
            mYNAdapter.notifyDataSetChanged();
        }
    }

    protected int getOrientation() {
        return OrientationHelper.VERTICAL;
    }

    //是否从底部开始
    protected boolean isStackFromEnd() {
        return false;
    }


    public AdapterProxy getAdapterProxy() {
        return mAdapterProxy;
    }

    protected int getLayoutType() {
        return LAYOUT_LINEAR;
    }

    protected int getItemBackgroundResource() {
        return R.drawable.yn_click;
    }

    @Override
    public void addData(List<T> data) {
        if (mT.size() > 0 && mT.get(mT.size() - 1) == null) {
            mT.remove(mT.size() - 1);
        }
        mT.addAll(data);
        mContentCount = mT.size();
        notifyDataSetChanged();
    }

    @Override
    public void addData(T data, int index) {
        getList().add(index, data);
        notifyDataSetChanged();
    }

    @Override
    public void addData(T data) {
        addData(data, getItemCount());
    }

    @Override
    public void addReData(List<T> data) {

    }

    @Override
    public void remove(T data) {

    }

    @Override
    public void removes(List<T> data) {

    }

    @Override
    public void removeAll() {

    }

    @Override
    public void notifyDataSetChanged() {
        if (mYNAdapter != null) {
            mYNAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setOnLoadMoreListener(BucketListAdapter.LoadMoreListener l) {
        mLoadMoreListener = l;
    }

    public void setOnHeadLoadMore(OnHeadLoadMore l) {
        mOnHeadLoadMore = l;
    }

    @Override
    public void setRefreshListener(SwipeRefreshLayout.OnRefreshListener l) {
        ViewParent viewParent = getParent();
        while (!(viewParent instanceof YNFrameWork)) {
            if (viewParent instanceof SwipeRefreshLayout) {
                mSwipeRefreshLayout = (YNSwipeRefreshLayout) viewParent;
                mSwipeRefreshLayout.setOnRefreshListener(l);
                mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
                break;
            }
            if (viewParent == null) {
                break;
            }
            viewParent = viewParent.getParent();
        }
    }


    @Override
    public void setIsLoadMore(boolean is) {

    }

    @Override
    public boolean getIsLoadMore() {
        return false;
    }

    @Override
    public void setIsRefresh(boolean is) {

    }

    @Override
    public boolean getIsRefresh() {
        return false;
    }

    @Override
    public void closeRefresh() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }, 500);
        }
    }

    public void openHeadLoadMore() {
        mIsHeadLoadMore = true;
    }

    public void closeHeadLoadMore() {
        mIsHeadLoadMore = false;
    }

    @Override
    public void closeLoadMore() {
//        if (isLoadMore()) {
//            if (mT != null) {
//                int footSize = mFootResourceViews == null ? 0 : mFootResourceViews.size();
//                int index = mT.size() - footSize - 1;
//                if (index < mT.size() && index >= 0 && mT.get(index) == null) {
//                    mT.remove(index);
//                }
//                notifyDataSetChanged();
//            }
        //显示没有更多数据
        mIsLoadMore = false;
        notifyChangeSet();
//        }
    }

    @Override
    public void openLoadMore() {
        if (mT != null) {
            mIsLoadMore = true;
            notifyDataSetChanged();
        }
    }

    @Override
    public void disableRefresh() {

    }

    @Override
    public void enableRefresh() {

    }

    @Override
    public int getSize() {
        return mT == null ? 0 : mT.size();
    }

    @Override
    public int getPageNumber() {
        return 10;
    }

    @Override
    public HttpVisitCallBack getHttpVisitCallBack(HttpVisitCallBack callBack) {
        return null;
    }

    @Override
    public void setHttpVisitCallBack(HttpVisitCallBack callBack) {

    }

    @Override
    public void setLoadError() {
        if (mOtherHashMap != null) {
            View loadMoreView = mOtherHashMap.get(R.layout.y_bucket_progress_bar);
            if (loadMoreView != null) {
                if (mLoadErrorView == null) {
                    mLoadErrorView = loadMoreView.findViewById(R.id.error);
                    mLoadErrorView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mLoadErrorView.setVisibility(GONE);
                            mLoadProgressLayoutView.setVisibility(VISIBLE);
                            reLoadMore();
                        }
                    });
                }
                if (mLoadProgressLayoutView == null) {
                    mLoadProgressLayoutView = loadMoreView.findViewById(R.id.progressTotal);
                }
                mLoadErrorView.setVisibility(VISIBLE);
                mLoadProgressLayoutView.setVisibility(GONE);
            }
        }
    }

    protected void reLoadMore() {

    }


    @Override
    public View createView(int position, T data) {
        return null;
    }

    @Override
    public void setViewData(View view, int position, T data) {

    }

    /**
     * resourceView不允许是一样的控件
     *
     * @param resourceView
     */
    public void addHeaderView(int resourceView) {
        if (mHeadResourceViews == null) {
            mHeadResourceViews = new ArrayList<>();
        }
        initExtraView(mHeadResourceViews, resourceView);
    }

    public void addFooterView(int resourceView) {
        if (mFootResourceViews == null) {
            mFootResourceViews = new ArrayList<>();
        }
        initExtraView(mFootResourceViews, resourceView);
    }

    //初始化额外的布局
    private void initExtraView(List<Integer> resourceViews, int resourceView) {
        initMap();
        View view = mInflater.inflate(resourceView, null);
        mOtherHashMap.put(resourceView, view);
        resourceViews.add(resourceView);
    }

    private void initMap() {
        if (mOtherHashMap == null) {
            mOtherHashMap = new HashMap<>();
        }
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
        mIsClickItemListener = true;
        if (mYNAdapter != null) {
            mYNAdapter.notifyDataSetChanged();
        }
    }

    public void setOnItemLongListener(OnItemLongClickListener l) {
        mOnItemLongListener = l;
        mYNAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mT == null) {
            return 0;
        }
        return mT.size();
    }

    public T getItem(int position) {
        return mT.get(position);
    }

    protected Object findView(View view) {
        return null;
    }

    public View getHeadView(int resourceView) {
        if (mOtherHashMap != null) {
            return mOtherHashMap.get(resourceView);
        }
        return null;
    }

    @Override
    public void notifyChangeSet() {
        if (mYNAdapter != null) {
            mYNAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getStatue() {
        if (mImageLoaderScrollerObservation != null) {
            return mImageLoaderScrollerObservation.getState();
        }
        return TYPE_START;
    }

    public class YNAdapter extends Adapter<MyViewHolder> {


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder;
            if (viewType >= 0) {
                holder = new MyViewHolder(viewType);
                holder.itemView.setBackgroundResource(getItemBackgroundResource());
                holder.setLayoutParams();
            } else {
                int layout;
                boolean isHead = false;
                if (viewType / 10 == VIEW_HEAD / 10) {
                    layout = mHeadResourceViews.get(Math.abs(viewType - VIEW_HEAD));
                } else if (viewType / 10 == VIEW_FOOT / 10) {
                    layout = mFootResourceViews.get(Math.abs(viewType - VIEW_FOOT));
                } else {
                    layout = R.layout.y_bucket_progress_bar;
                }
                View view = mOtherHashMap.get(layout);
                if (view == null) {
                    view = mInflater.inflate(layout, null);
                    mOtherHashMap.put(layout, view);
                }
                holder = new MyViewHolder(view);
                holder.setOtherLayoutParams();
            }
            return holder;
        }

        @Override
        public int getItemViewType(int position) {
            int headCount = getHeadCount();
            int footCount = getFootCount();
            int headLoadMore = getHeadLoadMore();
            if (mIsHeadLoadMore && position == 0) {
                return VIEW_HEAD_LORE_MORE;
            } else if (position < headCount) {
                return VIEW_HEAD - position;
            } else if (isLoadMore() &&
                    (position == (mContentCount + headCount + headLoadMore))) {
                return VIEW_LORE_MORE;
            } else if (position >= getSize() + headLoadMore + getHeadCount() - footCount) {
                return VIEW_FOOT - (position - getSize());
            } else {
                int viewType = getViewType(position - (getHeadCount() + getHeadLoadMore()));
                if (viewType < 0) {
                    throw new RuntimeException("recyclerView getViewType is not return < 0 ");
                }
                return viewType;
            }
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            int itemViewType = getItemViewType(position);
            if (itemViewType < 0) {
                if (itemViewType == VIEW_LORE_MORE) {
                    if (mLoadMoreListener != null) {
                        mLoadMoreListener.onLoadMore();
                    }
                } else if (mOnHeadLoadMore != null && itemViewType == VIEW_HEAD_LORE_MORE) {
                    mOnHeadLoadMore.loadMore();
                }
                return;
            }
            position -= getHeadCount() + getHeadLoadMore();
            setViewData(holder.itemView, position, mT.get(position));
            final int po = position;
            if (mIsClickItemListener) {
                holder.itemView.setOnClickListener(new OnClickListener() {
                    int p = po;

                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(p);
                        }
                        onItemClick(v);
                    }
                });
            }
            if (mOnItemLongListener != null) {
                holder.itemView.setOnLongClickListener(new OnLongClickListener() {
                    int p = po;

                    @Override
                    public boolean onLongClick(View v) {
                        mOnItemLongListener.onItemLongClick(p);
                        return false;
                    }
                });
            }
        }


        private int getLoadMore() {
            return mIsLoadMore ? 1 : 0;
        }


        @Override
        public int getItemCount() {
            return mT.size() + getHeadCount() + getFootCount() + getLoadMore() + getHeadLoadMore();
        }
    }

    protected void onItemClick(View v) {

    }

    private int getHeadLoadMore() {
        return mIsHeadLoadMore ? 1 : 0;
    }

    private int getHeadCount() {
        return mHeadResourceViews == null ? 0 : mHeadResourceViews.size();
    }

    private int getFootCount() {
        return mFootResourceViews == null ? 0 : mFootResourceViews.size();
    }


    protected int getViewResource(int viewType) {
        return 0;
    }

    protected int getViewType(int position) {
        return VIEW_NORMAL;
    }

    public class MyViewHolder extends ViewHolder {

        public Object obj;

        public MyViewHolder(int viewType) {
            this(mInflater.inflate(mLayoutId == 0 ? getViewResource(viewType) : mLayoutId, null));
        }

        public MyViewHolder(View itemView) {
            super(itemView);
        }

        MyViewHolder setOtherLayoutParams() {
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
            return this;
        }

        MyViewHolder setLayoutParams() {
            LayoutParams params = new LayoutParams((int) mItemWidth, (int) mItemHeight);
            itemView.setLayoutParams(params);
            obj = findView(itemView);
            itemView.setTag(obj);
            return this;
        }
    }

    //設置拖拽
    public void setDrag(OnDragListener l) {
        mItemTouchHelper = new ItemTouchHelper(new CallBack(l));
        mItemTouchHelper.attachToRecyclerView(this);
    }

    public void setLayoutId(int layoutId) {
        mLayoutId = layoutId;
    }

    private boolean mEnableDrag = true;

    public void setEnableDrag(boolean enable) {
        mEnableDrag = enable;
    }

    public class MyLayoutManager extends LinearLayoutManager {

        public MyLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onMeasure(Recycler recycler, State state, int widthSpec, int heightSpec) {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
            View view = recycler.getViewForPosition(0);
            if (view != null) {
                measureChild(view, widthSpec, heightSpec);
                int measuredWidth = MeasureSpec.getSize(widthSpec);
                int measuredHeight = view.getMeasuredHeight();
                setMeasuredDimension(measuredWidth, measuredHeight);
            }
        }
    }

    class CallBack extends ItemTouchHelper.Callback {

        private OnDragListener mDragListener;

        public CallBack(OnDragListener l) {
            mDragListener = l;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return mEnableDrag;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
            int dragFlags;
            if (mDragListener != null) mDragListener.startDrag();
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(70);
            if (getLayoutType() == LAYOUT_LINEAR) {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            } else {
                dragFlags = ItemTouchHelper.UP |
                        ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }
            return makeMovementFlags(dragFlags, 0);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            if (!isDrag(fromPosition, toPosition)) {
                return false;
            }
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mT, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mT, i, i - 1);
                }
            }
            mYNAdapter.notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(ViewHolder viewHolder, int direction) {

        }

        @Override
        public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            if (mDragListener != null) {
                mDragListener.endDrag();
            }
            notifyDataSetChanged();
        }
    }

    public List<T> getList() {
        return mT;
    }

    //是否可以拖拽
    protected boolean isDrag(int fromPosition, int topPosition) {
        return true;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public interface OnDragListener {
        void startDrag();

        void endDrag();
    }

    public interface OnHeadLoadMore {
        void loadMore();
    }

}
