package cn.fmall.common;

import com.google.common.cache.CacheLoader;

/**
 * 本地缓存抽象类的实现类,当前仅为TokenCache提供返回值
 * 本地缓存使用基本过程:创建本地缓存，当本地缓存不命中时，调用load方法，返回结果，再缓存结果
 */
public class CacheLoaderExt extends CacheLoader<String,String>{

    //默认的数据加载实现
    //在TokenCache中当调用localCache.get(key)取值的时候,如果key无对应的value,就调用该方法进行加载
    @Override
    public String load(String key) throws Exception {
        //这里不能直接是null,需使用"null"字符串
        //由于返回的是key,如果为null当匹配时使用key.equal()会引发空指针异常
        return "null";
    }
}
