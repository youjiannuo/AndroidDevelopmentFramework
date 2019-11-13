package com.yn.framework.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yn.framework.R;
import com.yn.framework.remind.FloatWindow;
import com.yn.framework.system.SystemUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by youjiannuo on 16/11/16
 */
public class ListMenuFloatWindow extends FloatWindow {

    private MenuListView mListView;
    private OnItemListener mOnItemListener;
    private List<Integer> mItems;
    private List<Integer> mItemColors;

    public ListMenuFloatWindow(Context context, OnItemListener l) {
        super(R.layout.float_card_export_type, context,
                SystemUtil.getPhoneScreenWH(context)[0] - SystemUtil.dipTOpx(60)
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        mOnItemListener = l;
    }

    public void setOnItemListener(OnItemListener l) {
        mOnItemListener = l;
    }

    @Override
    protected void initView(View v) {
        super.initView(v);
        mListView = (MenuListView) v.findViewById(R.id.list);
        mListView.setOnItemClickListener(new YJNListView.OnItemClickListener<Model>() {
            @Override
            public void onItemClick(Model data) {
                if (mOnItemListener != null) {
                    mOnItemListener.onItemClick(data);
                }
                close();
            }
        });
        if (mItemColors != null && mItems != null) {
            mListView.setItemColor(mItems.get(0), mItemColors.get(0));
        }
    }


    public void setColor(int item, int color) {
        mItemColors = new ArrayList<>();
        mItems = new ArrayList<>();
        mItemColors.add(color);
        mItems.add(item);
        if (mListView != null && mItemColors != null && mItems != null)
            mListView.setItemColor(mItems.get(0), mItemColors.get(0));
    }

    public void show(String... items) {
        List<Model> models = new ArrayList<>();
        for (String item : items) {
            models.add(new Model(item));
        }
        show("", models);
    }

    public void show(List<Model> models) {
        show("", models);
    }


    public void show(String title, List<Model> models) {
        show();
        List<Model> newModels = new ArrayList<>(models);
        mListView.setTitle(title);
        mListView.setAdapter(newModels);
    }


    public TextView getTitleTextView() {
        return mListView.getTitleTextView();
    }

    public int getPosition(String position) {
        for (int i = 0; i < mListView.getSize(); i++) {
            Model s = mListView.getItem(i);
            if (s.name.equals(position)) {
                return i;
            }
        }
        return -1;
    }

    public interface OnItemListener {
        void onItemClick(Model position);
    }

    public static class Model {
        public String name;
        public String id;
        public Object content;

        public Model(String name) {
            this.name = name;
        }

        public Model(String name, String id) {
            this(name);
            this.id = id;
        }

        public Model(String name, String id, Object content) {
            this(name, id);
            this.content = content;
        }

    }

}
