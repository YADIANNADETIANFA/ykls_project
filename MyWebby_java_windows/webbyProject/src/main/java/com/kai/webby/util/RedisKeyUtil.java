package com.kai.webby.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String BIZ_LIKE = "LIKE";
    private static final String BIZ_DISLIKE = "DISLIKE";

    //获取粉丝
    private static final String BIZ_FOLLOWER = "FOLLOWER";
    //关注对象
    private static final String BIZ_FOLLOWEE = "FOLLOWEE";
    private static final String BIZ_TIMELINE = "TIMELINE";

    public static String getLikeKey(String entityType, int entityId) {
        return BIZ_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getDisLikeKey(String entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //某个实体的粉丝key
    public static String getFollowerKey(String entityType, int entityId) {
        return BIZ_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    //每个用户对某类实体的关注key
    public static String getFolloweeKey(int userId, String entityType) {
        return BIZ_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    public static String getTimelineKey(int userId) { return BIZ_TIMELINE + SPLIT + userId; }
}
