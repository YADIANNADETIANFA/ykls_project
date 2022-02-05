package com.kai.webby.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kai.webby.async.EventModel;
import com.kai.webby.async.EventType;
import com.kai.webby.model.Comment;
import com.kai.webby.model.EntityType;
import com.kai.webby.model.User;
import com.kai.webby.service.CommentService;
import com.kai.webby.service.LikeService;
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

@Controller
public class LikeControllerWebby {

    private static final Logger logger = Logger.getLogger(LikeControllerWebby.class);

    @Autowired
    LikeService likeService;

    @Autowired
    CommentService commentService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping(path = {"/likeWebby"}, method = {RequestMethod.GET})
    @ResponseBody
    public String like(@RequestParam(value="commentId") int commentId) {

        Subject subject = SecurityUtils.getSubject();

        try {
            Comment comment = commentService.getCommentById(commentId);

            //判断当前用户是否已经点过赞
            int hasLike = likeService.getLikeStatus(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_COMMENT, commentId);
            if(1 == hasLike){
                ResultTemplate resultTemplate = ResultTemplate.fail("my402", "has liked before", null);
                return JSON.toJSONString(resultTemplate);
            }

            //点赞，并获取当前仍点赞的人数
            long likeCount = likeService.like(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_COMMENT,commentId);

            //MQ消息发送    不影响主流程
            try {
                rabbitTemplate.convertAndSend(ConstValue.ConstValue_YKLS_TOPIC, "like", new EventModel(EventType.LIKE)
                        .setActorId(((User)subject.getPrincipal()).getId())
                        .setEntityId(commentId)
                        .setEntityType(EntityType.ENTITY_COMMENT)
                        .setEntityOwnerId(comment.getUserId())
                        .setExtraMessages("questionId", String.valueOf(comment.getEntityId())));
            } catch (AmqpException amqp) {
                logger.error(amqp.getMessage());
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("likeCount", likeCount);
            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/likeWebby success", jsonObject.toJSONString());
            return JSON.toJSONString(resultTemplate);
        } catch (Exception e) {
            logger.error("/likeWebby异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }


    @RequestMapping(path = {"/dislikeWebby"}, method = {RequestMethod.GET})
    @ResponseBody
    public String dislike(@RequestParam(value = "commentId") int commentId){

        Subject subject = SecurityUtils.getSubject();

        try {
            //判断当前用户是否已经点过踩
            int hasDisLike = likeService.getLikeStatus(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_COMMENT, commentId);
            if(-1 == hasDisLike){
                ResultTemplate resultTemplate = ResultTemplate.fail("my402", "has dislike before", null);
                return JSON.toJSONString(resultTemplate);
            }

            //点踩，并获取当前已点踩的人数
            long likeCount = likeService.disLike(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_COMMENT, commentId);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("likeCount", likeCount);
            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/dislikeWebby success", jsonObject.toJSONString());
            return JSON.toJSONString(resultTemplate);
        } catch (Exception e) {
            logger.error("/dislikeWebby异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }
}
