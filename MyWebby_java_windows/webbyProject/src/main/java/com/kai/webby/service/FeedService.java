package com.kai.webby.service;

import com.kai.webby.dao.FeedDAO;
import com.kai.webby.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeedService {
    @Autowired
    FeedDAO feedDAO;

    //拉模式
    public List<Feed> getUserFeeds(int maxId, List<Integer> userIds, int currentPage){
        int limitFirst = (currentPage - 1) * 10;    //起始位置
        int limitSec = currentPage * 10;    //偏移量

        Map<String, Object> map = new HashMap<>();
        map.put("maxId", maxId);
        map.put("userIds", userIds);
        map.put("limitFirst", limitFirst);
        map.put("limitSec", limitSec);
        return feedDAO.selectUserFeeds(map);
    }

    public int getUserFeedsCount(int maxId, List<Integer> userIds) {
        Map<String, Object> map = new HashMap<>();
        map.put("maxId", maxId);
        map.put("userIds", userIds);
        return feedDAO.getUserFeedsCount(map);
    }

    public boolean addFeed(Feed feed){
        feedDAO.addFeed(feed);
        return feed.getId() > 0;
    }

    //推模式
    public Feed getById(int id){
        return feedDAO.getFeedById(id);
    }
}
