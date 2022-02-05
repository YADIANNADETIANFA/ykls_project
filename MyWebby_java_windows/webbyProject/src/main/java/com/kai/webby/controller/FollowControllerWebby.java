package com.kai.webby.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kai.webby.async.EventModel;
import com.kai.webby.async.EventType;
import com.kai.webby.model.*;
import com.kai.webby.service.CommentService;
import com.kai.webby.service.FollowService;
import com.kai.webby.service.QuestionService;
import com.kai.webby.service.UserService;
import com.kai.webby.util.ConstValue;
import com.kai.webby.util.ResultTemplate;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class FollowControllerWebby {

    private static final Logger logger = Logger.getLogger(FollowControllerWebby.class);

    @Autowired
    FollowService followService;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping(path = {"/followUserWebby"}, method = {RequestMethod.GET})
    @ResponseBody
    public String followUser(@RequestParam(value = "userId") int userId){

        Subject subject = SecurityUtils.getSubject();

        try {
            boolean ret = followService.follow(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_USER, userId);

            if (ret) {
                //MQ消息发送    不影响主流程
                try {
                    rabbitTemplate.convertAndSend(ConstValue.ConstValue_YKLS_TOPIC, "followUser", new EventModel(EventType.FOLLOW_USER)
                            .setActorId(((User)subject.getPrincipal()).getId())
                            .setEntityType(EntityType.ENTITY_USER)
                            .setEntityId(userId)
                            .setEntityOwnerId(userId));
                } catch (AmqpException amqp) {
                    logger.error(amqp.getMessage());
                }

                //返回当前用户所关注的人的数量
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("success", String.valueOf(followService.getFolloweeCount(((User)subject.getPrincipal()).getId(),EntityType.ENTITY_USER)));
                ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/followUserWebby success", jsonObject.toJSONString());
                return JSON.toJSONString(resultTemplate);

            } else {
                ResultTemplate resultTemplate = ResultTemplate.fail("my402", "服务器异常，请联系管理员", null);
                return JSON.toJSONString(resultTemplate);
            }
        } catch(Exception e) {
            logger.error("/followUserWebby异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }

    @RequestMapping(path = {"/unfollowUserWebby"}, method = {RequestMethod.GET})
    @ResponseBody
    public String unfollowUser(@RequestParam(value = "userId") int userId){

        Subject subject = SecurityUtils.getSubject();

        try {
            boolean ret = followService.unfollow(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_USER,userId);

            if (ret) {

                //todo：取关需要MQ操作什么么？
                //返回当前用户所关注的人的数量
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("success", String.valueOf(followService.getFolloweeCount(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_USER)));
                ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/unfollowUserWebby success", jsonObject.toJSONString());
                return JSON.toJSONString(resultTemplate);
            } else {
                ResultTemplate resultTemplate = ResultTemplate.fail("my402", "服务器异常，请联系管理员", null);
                return JSON.toJSONString(resultTemplate);
            }
        } catch(Exception e) {
            logger.error("/unfollowUserWebby异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }

    @RequestMapping(path={"/followQuestionWebby"}, method = {RequestMethod.GET})
    @ResponseBody
    public String followQuestion(@RequestParam(value = "questionId") int questionId){

        Subject subject = SecurityUtils.getSubject();

        try {
            Question question = questionService.getById(questionId);
            if (null == question) {
                ResultTemplate resultTemplate = ResultTemplate.fail("my402", "question not exist", null);
                return JSON.toJSONString(resultTemplate);
            }

            boolean ret = followService.follow(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_QUESTION, questionId);
            if (ret) {
                //MQ消息发送    不影响主流程
                try {
                    rabbitTemplate.convertAndSend(ConstValue.ConstValue_YKLS_TOPIC, "followQuestion", new EventModel(EventType.FOLLOW_QUESTION)
                            .setActorId(((User)subject.getPrincipal()).getId())
                            .setEntityId(questionId)
                            .setEntityType(EntityType.ENTITY_QUESTION)
                            .setEntityOwnerId(question.getUserId()));
                } catch (AmqpException amqp) {
                    logger.error(amqp.getMessage());
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("headUrl", ((User)subject.getPrincipal()).getHeadUrl());
                jsonObject.put("name", ((User)subject.getPrincipal()).getName());
                jsonObject.put("id", ((User)subject.getPrincipal()).getId());
                jsonObject.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
                ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/followQuestionWebby success", jsonObject.toJSONString());
                return JSON.toJSONString(resultTemplate);
            } else {
                ResultTemplate resultTemplate = ResultTemplate.fail("my402", "/followQuestionWebby fail", null);
                return JSON.toJSONString(resultTemplate);
            }
        } catch (Exception e) {
            logger.error("/followQuestionWebby异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }

    @RequestMapping(path = {"/unfollowQuestionWebby"}, method = {RequestMethod.GET})
    @ResponseBody
    public String unfollowQuestion(@RequestParam(value = "questionId") int questionId){

        Subject subject = SecurityUtils.getSubject();

        try {
            Question question = questionService.getById(questionId);
            if (null == question) {
                ResultTemplate resultTemplate = ResultTemplate.fail("my402", "question not exist", null);
                return JSON.toJSONString(resultTemplate);
            }

            boolean ret = followService.unfollow(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_QUESTION, questionId);
            if (ret) {

                //todo：取关需要MQ操作什么么？
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", ((User)subject.getPrincipal()).getId());
                jsonObject.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
                ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/unfollowQuestionWebby success", jsonObject.toJSONString());
                return JSON.toJSONString(resultTemplate);
            } else {
                ResultTemplate resultTemplate = ResultTemplate.fail("my402", "/unfollowQuestionWebby fail", null);
                return JSON.toJSONString(resultTemplate);
            }
        } catch (Exception e) {
            logger.error("/unfollowQuestionWebby异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }

    @RequestMapping(path = {"/userWebby/{uid}/followers"}, method = {RequestMethod.GET})
    @ResponseBody
    public String followers(@PathVariable("uid") int userId, @RequestParam(value = "currentPage") int currentPage){

        Subject subject = SecurityUtils.getSubject();

        try {
            List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, currentPage);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("followerList", getUsersInfo(((User)subject.getPrincipal()).getId(), followerIds));
            jsonObject.put("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER, userId));
            jsonObject.put("currentUser",userService.getUser(userId));
            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/userWebby/{uid}/followers success", jsonObject.toJSONString());
            return JSON.toJSONString(resultTemplate);
        } catch (Exception e) {
            logger.error("/userWebby/{uid}/followers异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }

    @RequestMapping(path = {"/userWebby/{uid}/followees"}, method = {RequestMethod.GET})
    @ResponseBody
    public String followees(@PathVariable("uid") int userId, @RequestParam(value = "currentPage") int currentPage){

        Subject subject = SecurityUtils.getSubject();

        try {
            List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, currentPage);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("followeeList", getUsersInfo(((User)subject.getPrincipal()).getId(), followeeIds));
            jsonObject.put("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
            jsonObject.put("currentUser", userService.getUser(userId));
            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/userWebby/{uid}/followees success", jsonObject.toJSONString());
            return JSON.toJSONString(resultTemplate);
        } catch (Exception e) {
            logger.error("/userWebby/{uid}/followees异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }

    private JSONArray getUsersInfo(int localUserId, List<Integer> userIds){

        JSONArray jsonArray = new JSONArray();
        for (Integer userId : userIds) {
            JSONObject jsonObject = new JSONObject();
            User user = userService.getUser(userId);
            if (null == user) {
                continue;
            }
            jsonObject.put("user", user);
            jsonObject.put("commentCount", commentService.getUserCommentCount(userId));
            jsonObject.put("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER,userId));
            jsonObject.put("followeeCount", followService.getFolloweeCount(userId,EntityType.ENTITY_USER));
            jsonObject.put("followed", followService.isFollower(localUserId,EntityType.ENTITY_USER,userId));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
