package com.kai.webby.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kai.webby.model.*;
import com.kai.webby.service.CommentService;
import com.kai.webby.service.FollowService;
import com.kai.webby.service.QuestionService;
import com.kai.webby.service.UserService;
import com.kai.webby.util.ResultTemplate;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeControllerWebby {
    private static final Logger logger = Logger.getLogger(HomeControllerWebby.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;

    @Autowired
    CommentService commentService;

    @RequestMapping(path={"/indexWebby"}, method = {RequestMethod.GET})//不指定userId的时候
    @ResponseBody
    public String indexWebby(@RequestParam(value = "currentPage") int currentPage) {    //QuestionDAO里面，有<if test="userId != 0" WHERE user_id = #{userId}</if>
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = getQuestions(0, currentPage);
            int totalQuestionCount = questionService.getQuestionsCount();

            jsonObject.put("questions", jsonArray.toJSONString());
            jsonObject.put("totalQuestionCount", totalQuestionCount);
            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/indexWebby success", jsonObject.toJSONString());
            return JSON.toJSONString(resultTemplate);
        } catch(Exception e) {
            logger.error("/indexWebby异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }

    //访问某个人的主页，可以是当前登录用户的主页，也可以是访问其他用户的主页
    @RequestMapping(path={"/userWebby/{userId}"}, method={RequestMethod.GET})
    @ResponseBody
    public String userIndex(@PathVariable("userId") int userId, @RequestParam(value = "currentPage") int currentPage) {

        Subject subject = SecurityUtils.getSubject();

        try{
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = getQuestions(userId, currentPage);
            int totalQuestionCount = questionService.getQuestionsCountByUserId(userId);

            jsonObject.put("userQuestionList", jsonArray);
            jsonObject.put("totalQuestionCount", totalQuestionCount);

            User user = userService.getUser(userId);
            jsonObject.put("user", user);

            jsonObject.put("commentCount", commentService.getUserCommentCount(userId));
            jsonObject.put("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
            jsonObject.put("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
            //已登录状态，判断用户是否关注了某个实体
            jsonObject.put("followed", followService.isFollower(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_USER, userId));

            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/userWebby/{userId} success", jsonObject.toJSONString());
            return JSON.toJSONString(resultTemplate);
        } catch (Exception e) {
            logger.error("/userWebby/{userId}异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }

    private JSONArray getQuestions(int userId, int currentPage) {
        List<Question> questionList = questionService.getLatestQuestions(userId, currentPage);
        JSONArray jsonArray = new JSONArray();
        int i = 0;
        for (Question question : questionList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", i++);
            jsonObject.put("questionId", question.getId()); //问题博客id
            jsonObject.put("questionTitle", question.getTitle());   //问题博客标题
            jsonObject.put("questionDescription", question.getDescription());   //问题博客摘要
            jsonObject.put("questionCreatedDate", question.getCreatedDate());   //问题博客最新创建时间
            jsonObject.put("questionUserId", question.getUserId()); //问题博客提出人，所属用户
            jsonObject.put("questionCommentCount", question.getCommentCount()); //问题博客评论数

            User user = userService.getUser(question.getUserId());
            jsonObject.put("userId", user.getId());
            jsonObject.put("userName", user.getName());
            jsonObject.put("headUrl", user.getHeadUrl());

            Long followCount = followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId());
            jsonObject.put("followCount", followCount);

            //用于判断当前登录用户是否已关注了该问题
            Subject subject = SecurityUtils.getSubject();
            jsonObject.put("followed", followService.isFollower(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_QUESTION, question.getId()));

            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
