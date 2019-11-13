package com.yn.framework.view;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yn.framework.R;
import com.yn.framework.system.StringUtil;
import com.yn.framework.system.SystemUtil;


/**
 * Created by youjiannuo on 16/11/21
 */
public class MenuListView extends YJNListView<ListMenuFloatWindow.Model> {


    private View mTitleLayout;
    private TextView mTitleView;

    private int mNeedSetColorItem = -1;
    private int mSetColor = -1;

    public MenuListView(Context context) {
        super(context);
    }

    public MenuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public View createView(int position, ListMenuFloatWindow.Model model) {
        return getView(R.layout.item_menu_list, MATCH_PARENT, SystemUtil.dipTOpx(50));
    }

    @Override
    public void setViewData(View view, int position, final ListMenuFloatWindow.Model model) {
        TextView textView = (TextView) view.findViewById(R.id.name);
        textView.setText(model.name);
        View lineView = view.findViewById(R.id.line);
        lineView.setVisibility(VISIBLE);

        if (isLast(position)) {
            lineView.setVisibility(GONE);
        }
        view.setOnClickListener(new OnClickListener() {
            ListMenuFloatWindow.Model data = model;

            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(data);
            }
        });
        if (position == mNeedSetColorItem) {
            textView.setTextColor(mSetColor);
        } else {
            textView.setTextColor(0xFF333333);
        }
    }

    public void setTitle(String s) {
        if (StringUtil.isEmpty(s)) return;
        if (mTitleLayout == null) {
            mTitleLayout = LayoutInflater.from(getContext()).inflate(R.layout.item_menu_title, null);
            addHeaderView(mTitleLayout);
            mTitleView = (TextView) mTitleLayout.findViewById(R.id.name);
            mTitleView.setEnabled(false);
        }
        mTitleView.setText(Html.fromHtml(s));
    }

    public void setItemColor(int item, int color) {
        mNeedSetColorItem = item;
        mSetColor = color;
    }

    public TextView getTitleTextView() {
        return mTitleView;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public boolean isLast(int position) {
        return position == getSize() - 1;
    }


    @Override
    public void setLoadError() {

    }
}
