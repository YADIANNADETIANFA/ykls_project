package com.kai.webby.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kai.webby.model.EntityType;
import com.kai.webby.model.Feed;
import com.kai.webby.model.User;
import com.kai.webby.service.FeedService;
import com.kai.webby.service.FollowService;
import com.kai.webby.util.RedisKeyUtil;
import com.kai.webby.util.ResultTemplate;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class FeedControllerWebby {
    private static final Logger logger = Logger.getLogger(FeedControllerWebby.class);

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @RequestMapping(path = {"/pushfeedsWebby"},method = {RequestMethod.GET})
    @ResponseBody
    private String getPushFeeds(@RequestParam(value = "currentPage") int currentPage){

        Subject subject = SecurityUtils.getSubject();

        try{
            JSONObject jsonObject = new JSONObject();
            int limitFirst = (currentPage - 1) * 10;    //起始位置
            int limitSec = currentPage * 10;    //偏移量
            List<String> feedIds = stringRedisTemplate.opsForList().range(RedisKeyUtil.getTimelineKey(((User)subject.getPrincipal()).getId()), limitFirst, limitSec);
            long count = stringRedisTemplate.opsForList().size(RedisKeyUtil.getTimelineKey(((User)subject.getPrincipal()).getId()));
            JSONArray jsonArray = new JSONArray();
            for(String feedId : feedIds){
                Feed feed = feedService.getById(Integer.parseInt(feedId));
                if(feed != null){
                    JSONObject jsonObjectFeed = new JSONObject();
                    jsonObjectFeed.put("feed", feed);
                    jsonArray.add(jsonObjectFeed);
                }
            }
            jsonObject.put("feeds", jsonArray);
            jsonObject.put("count", count);

            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/pushfeedsWebby success", jsonObject.toJSONString());
            return JSON.toJSONString(resultTemplate);
        }catch(Exception e){
            logger.error("/pushfeedsWebby异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }

    

    @RequestMapping(path = {"/pullfeedsWebby"}, method = {RequestMethod.GET})
    @ResponseBody
    private String getPullFeeds(@RequestParam(value = "currentPage") int currentPage){

        Subject subject = SecurityUtils.getSubject();

        try{
            JSONObject jsonObject = new JSONObject();

            //当前登录用户所关注的人们
            List<Integer> followees = followService.getFolloweesAll(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_USER, Integer.MAX_VALUE);
            //将返回的pullList信息。若当前登录用户存在已关注的人（即followees有值），则pullList中的信息只是followees中人的信息；
            //若当前登录用户不存在已关注的人（即followees无值），则pullList中的信息会是所有系统用户人的信息。
            //此逻辑于FeedDAO.xml中的if来控制
            List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, currentPage);
            int count = feedService.getUserFeedsCount(Integer.MAX_VALUE, followees);
            JSONArray jsonArray = new JSONArray();
            for(Feed  feed : feeds){
                JSONObject jsonObjectFeed = new JSONObject();
                jsonObjectFeed.put("feed", feed);
                jsonArray.add(jsonObjectFeed);
            }
            jsonObject.put("feeds", jsonArray);
            jsonObject.put("count", count);

            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/pullfeedsWebby success", jsonObject.toJSONString());
            return JSON.toJSONString(resultTemplate);
        }catch (Exception e){
            logger.error("/pullfeedsWebby异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }
}

/**
 * 拉：直接从数据库中读取即可，登陆打开的页面时，根据关注的实体，生成Timeline（动态）内容
 * EventHandler；新鲜事的触发（FeedHandle的getSupportEventTypes）；注册好能触发新鲜事的事件；推，给每个人创建一个Timeline(动态)队列，当被关注的人产生
 *新鲜事的时候，推入到该人的Timeline内，该人登陆后，直接从本人的Timeline内读取即可，以Redis的list进行存储
 *优化：
 * （1）多好友合并
 * （2）关联实体删除清理
 * （3）取消/新增关注实体更新
 * （4）分时段存储
 * （5）缓存和增量拉
 *
 * 未登录，拉取所有人的最新动态（动态内容包括：关注某一个问题；评论某一个问题）
 * */
