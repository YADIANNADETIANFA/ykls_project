package com.kai.webby.dao;

import com.kai.webby.model.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface FeedDAO {

    int addFeed(Feed feed);

    //用于推模式，推给我这个Id。我根据这个Id去取数据
    Feed getFeedById(@Param("id") int id);

    //用于pull拉模式，当有用户登录的时候，pull该用户关注的人的新鲜事；当未登录状态，pull所有用户的新鲜事；
    //增量的概念；maxId；第一次110刷到100，下一次刷肯定是从100开始往90刷     然而并没有使用
    //userIds我关注的人
    List<Feed> selectUserFeeds(Map<String, Object> map);


    //用于查询pullList的总记录数
    int getUserFeedsCount(Map<String, Object> map);
}
