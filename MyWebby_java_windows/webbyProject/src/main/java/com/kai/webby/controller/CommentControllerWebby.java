package com.kai.webby.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kai.webby.async.EventModel;
import com.kai.webby.async.EventType;
import com.kai.webby.model.Comment;
import com.kai.webby.model.EntityType;
import com.kai.webby.model.User;
import com.kai.webby.service.CommentService;
import com.kai.webby.service.QuestionService;
import com.kai.webby.util.ConstValue;
import com.kai.webby.util.DbSrcTrsUtil;
import com.kai.webby.util.ResultTemplate;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class CommentControllerWebby {
    private static final Logger logger = Logger.getLogger(CommentControllerWebby.class);

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    DbSrcTrsUtil dbSrcTrsUtil;

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @RequestMapping(path = {"/addCommentWebby"},method = {RequestMethod.POST})
    @ResponseBody
    public String addComment(@RequestBody(required = true) String strJson){

        JSONObject jsonObject = new JSONObject();
        Subject subject = SecurityUtils.getSubject();

        JSONObject jsonObjectRec = JSON.parseObject(strJson);
        int questionId = jsonObjectRec.getIntValue("questionId");
        String content = jsonObjectRec.getString("content");

        Comment comment = new Comment();
        comment.setContent(content).setUserId(((User)subject.getPrincipal()).getId())
                .setCreatedDate(new Date()).setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId).setStatus(0);

        int commentTotalCount = 0;
        TransactionStatus trs = null;
        try {
            trs = dbSrcTrsUtil.openTrs();
            //新增评论
            commentService.addComment(comment);

            //获取最新评论数并更新数据库
            commentTotalCount = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(), commentTotalCount);

            dbSrcTrsUtil.commitTrs(trs);
        } catch (Exception e) {
            if (null != trs) {
                dbSrcTrsUtil.rollbackTrs(trs);
            }
            logger.error("增加评论失败：" + e.getMessage());
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }

        //MQ消息发送    不影响主流程
        try {
            rabbitTemplate.convertAndSend(ConstValue.ConstValue_YKLS_TOPIC, "comment", new EventModel(EventType.COMMENT)
                    .setActorId(comment.getUserId())
                    .setEntityId(questionId)
                    .setExtraMessages("comment", comment)
                    .setExtraMessages("commentTotalCount", commentTotalCount));
        } catch (AmqpException amqp) {
            logger.error(amqp.getMessage());
        }

        ResultTemplate resultTemplate = ResultTemplate.succ("my200", "增加评论成功", null);
        return JSON.toJSONString(resultTemplate);
    }
}
