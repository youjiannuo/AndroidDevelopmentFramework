package com.yn.framework.controller;

import com.yn.framework.http.HttpExecute;

/**
 * Created by youjiannuo on 17/2/21.
 */

public interface OnCreateNewNetworkTaskListener {

    void onNewNetworkTask(HttpExecute.NetworkTask backTask);

}
