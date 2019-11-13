package com.yn.framework.model;

import com.yn.framework.cache.RedisItem;
import com.yn.framework.data.JSON;


/**
 * Created by youjiannuo on 2019/2/22.
 * Email by 382034324@qq.com
 */

public class JSONItem implements RedisItem {

    private JSON json;

    public JSONItem(JSON json) {
        this.json = json;
    }

    @Override
    public int sizeOf() {
        if (json == null) {
            return 0;
        }
        return json.getByteSize() == -1 ?
                json.toString().getBytes().length :
                json.getByteSize();
    }

    public JSON getJson(){
        return json;
    }

}
