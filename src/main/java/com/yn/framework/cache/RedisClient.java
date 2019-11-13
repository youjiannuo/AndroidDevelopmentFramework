package com.yn.framework.cache;

import android.support.v4.util.LruCache;

import com.yn.framework.imageLoader.Cache;

import static com.yn.framework.system.StringUtil.isEmpty;

/**
 * Created by youjiannuo on 18/3/2.
 * Email by 382034324@qq.com
 */

public class RedisClient extends LruCache<String, RedisItem> implements Cache<RedisItem> {

    private static final String SYNCHRONIZED = "1";

    private static RedisClient REDIS_CLINE = null;

    public static RedisClient getInstance() {
        if (REDIS_CLINE == null) {
            synchronized (SYNCHRONIZED) {
                if (REDIS_CLINE == null) {
                    REDIS_CLINE = new RedisClient();
                }
            }
        }
        return REDIS_CLINE;
    }

    public static void putItem(String key, RedisItem redisItem) {
        getInstance().put(key, redisItem);
    }

    public static <R> R getItem(String key) {
        return (R) getInstance().getCacheItem(key);
    }

    public RedisClient() {
        super((int) (Runtime.getRuntime().maxMemory() / 11));
    }

    @Override
    public void addCacheItem(String key, RedisItem item) {
        if (!isEmpty(key) && item != null) {
            put(key, item);
        }
    }

    @Override
    public RedisItem getCacheItem(String key) {
        return super.get(key);
    }


    @Override
    protected int sizeOf(String key, RedisItem value) {
        return value.sizeOf();
    }

    @Override
    public void clear() {
        if (size() > 0) {
            evictAll();
        }
    }

    @Override
    public void removes(String key) {
        remove(key);
    }

    @Override
    public void Recycling() {

    }
}
