package com.cybermkd.plugin.redis;

import com.cybermkd.common.util.Stringer;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis.
 * redis 工具类
 * <pre>
 * 例如：
 * Redis.use().set("key", "value");
 * Redis.use().get("key");
 * </pre>
 */
public class Redis {

    static Cache mainCache = null;

    private static final ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<String, Cache>();

    public static void addCache(Cache cache) {
        if (cache == null)
            throw new IllegalArgumentException("cache can not be null");
        if (cacheMap.containsKey(cache.getName()))
            throw new IllegalArgumentException("The cache name already exists");

        cacheMap.put(cache.getName(), cache);
        if (mainCache == null)
            mainCache = cache;
    }

    public static Cache removeCache(String cacheName) {
        return cacheMap.remove(cacheName);
    }

    /**
     * 提供一个设置设置主缓存 mainCache 的机会，否则第一个被初始化的 Cache 将成为 mainCache
     */
    public static void setMainCache(String cacheName) {
        if (Stringer.isBlank(cacheName))
            throw new IllegalArgumentException("cacheName can not be blank");
        cacheName = cacheName.trim();
        Cache cache = cacheMap.get(cacheName);
        if (cache == null)
            throw new IllegalArgumentException("the cache not exists: " + cacheName);

        Redis.mainCache = cache;
    }

    public static Cache use() {
        return mainCache;
    }

    public static Cache use(String cacheName) {
        return cacheMap.get(cacheName);
    }

    public static Object call(ICallback callback) {
        return call(callback, use());
    }

    public static Object call(ICallback callback, String cacheName) {
        return call(callback, use(cacheName));
    }

    private static Object call(ICallback callback, Cache cache) {
        Jedis jedis = cache.getThreadLocalJedis();
        boolean notThreadLocalJedis = (jedis == null);
        if (notThreadLocalJedis) {
            jedis = cache.jedisPool.getResource();
            cache.setThreadLocalJedis(jedis);
        }
        try {
            return callback.call(cache);
        } finally {
            if (notThreadLocalJedis) {
                cache.removeThreadLocalJedis();
                jedis.close();
            }
        }
    }
}




