package com.kai.webby.service;

import com.kai.webby.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public boolean follow(int userId,String entityType,int entityId){
        //某一个实体(entityType,entityId)，都有哪些用户关注着这一实体，列表名followerKey
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        //某一用户userId对某种内容entityType，都关注了它们中的哪些，列表的名字followeeKey
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        Date date = new Date();

        //https://blog.csdn.net/crossroads10/article/details/88985398
        //redis事务本来就很水了，我们用的更水，找机会学一下吧  暂不影响运行，末期留作优化
        //而且我们也没用watch监视什么，现在也只是保证了一个连接执行两个语句
        //用Lua来保证原子性么？
        //某一实体的粉丝表中，新加当前用户
        SessionCallback sessionCallback = new SessionCallback<Boolean>() {
            @Override
            public Boolean execute(RedisOperations redisOperations) throws DataAccessException {
                //已时间为分数值，进行排序
                Boolean res1 = redisOperations.opsForZSet().add(followerKey, String.valueOf(userId), date.getTime());
                //当前用户userId对某种内容entityType，新增了关注entityId
                Boolean res2 = redisOperations.opsForZSet().add(followeeKey, String.valueOf(entityId), date.getTime());
                return res1 && res2;
            }
        };
        return (Boolean)stringRedisTemplate.execute(sessionCallback);
    }

    //todo:待测试
    public boolean unfollow(int userId, String entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);

        SessionCallback sessionCallback = new SessionCallback<Boolean>() {
            @Override
            public Boolean execute(RedisOperations redisOperations) throws DataAccessException {
                //返回移除的元素个数
                Long res1 = redisOperations.opsForZSet().remove(followerKey,String.valueOf(userId));
                Long res2 = redisOperations.opsForZSet().remove(followeeKey,String.valueOf(entityId));
                return (res1 == 1 && res2 == 1) ? true : false;
            }
        };
        return (Boolean)stringRedisTemplate.execute(sessionCallback);
    }

    public List<Integer> getFollowers(String entityType, int entityId, int currentPage){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        int limitFirst = (currentPage - 1) * 10;    //起始位置
        int limitSec = currentPage * 10;    //偏移量
        return getIdsFromSet(stringRedisTemplate.opsForZSet().reverseRange(followerKey, limitFirst, limitSec));
    }

    public List<Integer> getFolloweesAll(int userId, String entityType, int maxNum){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdsFromSet(stringRedisTemplate.opsForZSet().reverseRange(followeeKey, 0, maxNum));
    }

    public List<Integer> getFollowees(int userId, String entityType, int currentPage){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        int limitFirst = (currentPage - 1) * 10;    //起始位置
        int limitSec = currentPage * 10;    //偏移量
        return getIdsFromSet(stringRedisTemplate.opsForZSet().reverseRange(followeeKey, limitFirst, limitSec));
    }

    public long getFollowerCount(String entityType,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return stringRedisTemplate.opsForZSet().size(followerKey);
    }

    public long getFolloweeCount(int userId,String entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return stringRedisTemplate.opsForZSet().size(followeeKey);
    }

    private List<Integer> getIdsFromSet(Set<String> idset){
        List<Integer> ids = new ArrayList<>();
        for(String str : idset){
            ids.add(Integer.parseInt(str));
        }
        return ids;
    }

    //判断用户是否关注了某个实体
    public boolean isFollower(int userId, String entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return (stringRedisTemplate.opsForZSet().score(followerKey,String.valueOf(userId)) != null);
    }
}
