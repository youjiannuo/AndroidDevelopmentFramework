package com.yn.framework.view;

/**
 * Created by youjiannuo on 16/11/21
 */
public class AdapterProxy {

    private YJNRecyclerView.YNAdapter mYNAdapter;

    public AdapterProxy() {

    }

    public void setAdapter(YJNRecyclerView.YNAdapter adapter) {
        mYNAdapter = adapter;
    }

    public void notifyDataSetChanged() {
        mYNAdapter.notifyDataSetChanged();
    }


}
