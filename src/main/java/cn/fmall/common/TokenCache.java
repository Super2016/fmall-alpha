package cn.fmall.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import cn.fmall.common.CacheLoaderExt;
/**
 * Token缓存
 */
public class TokenCache {

    //设置输出日志
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    //使用Guava的本地缓存LoadingCache
    //[使用Guava中的本地缓存]
    private static LoadingCache<String,String> localCache = CacheBuilder
            //构建本地cache,采用了调用链模式
            .newBuilder()
            //[initialCapacity()初始容量],
            .initialCapacity(1000)
            //[maximumSize()最大容量,超过最大容量后进入catch块,使用LRU算法[即最近最小使用算法/缓存淘汰算法],以移除缓存项],
            .maximumSize(10000)
            //有效期超过时间即删除,两个值是时间量/单位
            .expireAfterAccess(24, TimeUnit.HOURS)
            //创建本地缓存,localCache.get(key)未命中本地缓存时调用.build
            .build(new CacheLoaderExt());

    //设置键值,token缓存的名称与token值
    public static void setKey(String key,String value){
        //将token名称与值的存入本地缓存
        localCache.put(key, value);
    }

    public static String getKey(String key){
        //初始化value
        String value = null;
        try {
            //根据token的key取值
            value = localCache.get(key);
            if (value.equals("null")) {
                //如果取得的值匹配字符串"null",则代表无值,返回空
                return null;
            }
        }catch (Exception e){
            logger.error("异常localcache get error:",e);
            localCache.cleanUp();
        }
        return value;
    }
}
