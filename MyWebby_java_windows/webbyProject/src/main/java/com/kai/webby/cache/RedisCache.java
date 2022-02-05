package com.kai.webby.cache;

import com.kai.webby.util.ApplicationContextUtils;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.DigestUtils;

import java.util.concurrent.TimeUnit;

public class RedisCache implements Cache {

    //当前放入缓存的mapper的namespace
    private final String id;

    //必须存在的构造方法
    public RedisCache(String id) {
        this.id = id;
    }

    //返回cache唯一标识
    @Override
    public String getId() {
        return this.id;
    }

    //缓存放入值
    @Override
    public void putObject(Object key, Object value) {

        //使用redisHash类型作为缓存存储模型 key hashKey value
        getRedisTemplate().opsForHash().put(id, getKeyToMD5(key.toString()), value);

        /*
        * 设置缓存超时时间  随机值，避免缓存雪崩      也可根据不同业务模块的特性，设置不同的超时时间，比如省市县时间长一点
        * 现在是每次更新该namespace下的缓存，都会重置该namespace缓存的超时时间
        * redis貌似仅支持一级key的超时配置，不支持内部hashKey的超时配置。后续看看有没有什么解决方案吧
        * */
        int randomTim = 10 + (int)(Math.random()*10);
        getRedisTemplate().expire(id, randomTim, TimeUnit.MINUTES);
    }

    //获取缓存中的数据
    @Override
    public Object getObject(Object key) {
        return getRedisTemplate().opsForHash().get(id, getKeyToMD5(key.toString()));
    }

    //注意：该方法为mybatis保留方法，默认没有实现，后续版本可能会实现
    @Override
    public Object removeObject(Object o) {
        return null;
    }

    //增删改，mybatis默认的清理缓存的方法
    @Override
    public void clear() {
        //清空namespace
        getRedisTemplate().delete(id);
    }

    //用来计算缓存的数量
    @Override
    public int getSize() {
        //获取hash中key value数量
        return getRedisTemplate().opsForHash().size(id).intValue();
    }

    //封装redisTemplate
    private RedisTemplate getRedisTemplate() {
        //通过application工具类获取redisTemplate
        RedisTemplate redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
        //更改key与HashKey的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    /**
     * --	MD5:
     * 		1、一切文件字符串等经过md5加密处理后，都会生成32位16进制字符串
     * 		2、不同内容文件经过md5进行加密，加密结果一定不一致。可用于判断两个文件内容是否相同！！
     * 		3、相同内容文件，多次md5生成的结果始终一致
     * 		4、md5可对任何内容进行加密，输入转换成byte数组即可
     * */
    //封装一个对key进行md5处理方法
    private String getKeyToMD5(String key){
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
