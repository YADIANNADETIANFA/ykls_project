package com.kai.webby.service;

import com.kai.webby.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public long getLikeCount(String entityType,int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return stringRedisTemplate.opsForSet().size(likeKey);
    }

    public int getLikeStatus(int userId, String entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if (stringRedisTemplate.opsForSet().isMember(likeKey, String.valueOf(userId))) {
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        if (stringRedisTemplate.opsForSet().isMember(disLikeKey, String.valueOf(userId))) {
            return -1;
        } else {
            return 0;
        }
    }

    public long like(int userId, String entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        stringRedisTemplate.opsForSet().add(likeKey, String.valueOf(userId));

        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        stringRedisTemplate.opsForSet().remove(disLikeKey, String.valueOf(userId));

        return stringRedisTemplate.opsForSet().size(likeKey);
    }

    public long disLike(int userId, String entityType, int entityId) {
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        stringRedisTemplate.opsForSet().add(disLikeKey, String.valueOf(userId));

        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        stringRedisTemplate.opsForSet().remove(likeKey, String.valueOf(userId));

        return stringRedisTemplate.opsForSet().size(likeKey);
    }
}
