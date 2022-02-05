package com.kai.webby.async;

import com.alibaba.fastjson.JSONObject;
import com.kai.webby.model.*;
import com.kai.webby.service.*;
import com.kai.webby.util.ConstValue;
import com.kai.webby.util.RedisKeyUtil;
import org.apache.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RabbitMQCustomer {
    private static final Logger logger = Logger.getLogger(RabbitMQCustomer.class);

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FeedService feedService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    FollowService followService;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue, //临时队列
                    exchange = @Exchange(type = "topic", name = ConstValue.ConstValue_YKLS_TOPIC),
                    key = {"like"}
            )
    })
    public void likeReceive(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(ConstValue.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(eventModel.getActorId());
        message.setContent("用户" + user.getName() + "赞了你的评论，http://127.0.0.1:8080/question/" + eventModel.getExtraMessages("questionId"));
        messageService.addMessage(message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue, //临时队列
                    exchange = @Exchange(type = "topic", name = ConstValue.ConstValue_YKLS_TOPIC),
                    key = {"followUser"}
            )
    })
    public void followUserReceive(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(ConstValue.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(eventModel.getActorId());
        message.setContent("用户" + user.getName() + "关注了你，http://127.0.0.1:8080/user/" + eventModel.getActorId());
        messageService.addMessage(message);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue, //临时队列
                    exchange = @Exchange(type = "topic", name = ConstValue.ConstValue_YKLS_TOPIC),
                    key = {"followQuestion"}
            )
    })
    public void followQuestionReceive(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(ConstValue.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(eventModel.getActorId());
        message.setContent("用户" + user.getName() + "关注了你的问题博客，http://127.0.0.1:8080/question/" + eventModel.getEntityId());
        messageService.addMessage(message);
    }

    /*
    * SearchESController目前只支持了  searchSelect: 1：搜用户   2：搜帖子
    * 后续按个人偏好搜索、按评论搜索，后续有机会再实现了
    * */
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue, //临时队列
                    exchange = @Exchange(type = "topic", name = ConstValue.ConstValue_YKLS_TOPIC),
                    key = {"comment"}
            )
    })
    public void commentReceive(EventModel eventModel) {

        //新评论放入ES   更新问题博客的最新评论数
        Comment comment = (Comment)eventModel.getExtraMessages("comment");
        int commentTotalCount = (int)eventModel.getExtraMessages("commentTotalCount");
        IndexRequest indexRequestComment = new IndexRequest("testcomment");
        UpdateRequest updateRequestQuestion = new UpdateRequest("testquestion", String.valueOf(comment.getEntityId()));

        indexRequestComment.id(String.valueOf(comment.getId()));
        indexRequestComment.timeout(TimeValue.timeValueSeconds(3));
        Map<String, Object> jsonMapComment = new HashMap<>();
        jsonMapComment.put("content", comment.getContent());
        jsonMapComment.put("user_id", comment.getUserId());
        jsonMapComment.put("entity_id", comment.getEntityId());
        jsonMapComment.put("entity_type", comment.getEntityType());
        jsonMapComment.put("created_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(comment.getCreatedDate()));
        jsonMapComment.put("status", comment.getStatus());
        indexRequestComment.source(jsonMapComment);

        updateRequestQuestion.timeout(TimeValue.timeValueSeconds(3));
        Map<String, Object> jsonMapQuestion = new HashMap<>();
        jsonMapQuestion.put("comment_count", commentTotalCount);
        updateRequestQuestion.doc(jsonMapQuestion);

        try {
            restHighLevelClient.index(indexRequestComment, RequestOptions.DEFAULT);
            restHighLevelClient.update(updateRequestQuestion, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("RabbitMQCustomer commentReceive error. comment: " + comment);
            logger.error(e.getMessage());
            logger.error(e.fillInStackTrace());
        }


        //构造一个新鲜事
        Feed feed = new Feed();
        feed.setCreatedDate(new Date()).setType(eventModel.getType().getValue())
                .setUserId(eventModel.getActorId()).setData(buildFeedData(eventModel));
        if(feed.getData() == null){
            return;
        }
        feedService.addFeed(feed);

        //获取所有的粉丝
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER,eventModel.getActorId(),Integer.MAX_VALUE);
        //系统队列（包含所有的新鲜事，未登录状态查看推状态新鲜事的时候，查询到的就是系统队列的新鲜事）
        for(int follower : followers){
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            stringRedisTemplate.opsForList().leftPush(timelineKey, String.valueOf(feed.getId()));
        }
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue, //临时队列
                    exchange = @Exchange(type = "topic", name = ConstValue.ConstValue_YKLS_TOPIC),
                    key = {"addQuestion"}
            )
    })
    public void addQuestionReceive(EventModel eventModel) {

        //新增问题博客放入ES
        Question question = (Question)eventModel.getExtraMessages("question");
        IndexRequest indexRequest = new IndexRequest("testquestion");
        indexRequest.id(String.valueOf(question.getId()));
        indexRequest.timeout(TimeValue.timeValueSeconds(3));

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("title", question.getTitle());
        jsonMap.put("description", question.getDescription());
        jsonMap.put("user_id", question.getUserId());
        jsonMap.put("created_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(question.getCreatedDate()));
        jsonMap.put("comment_count", question.getCommentCount());
        jsonMap.put("status", question.getStatus());
        jsonMap.put("content", question.getContent());
        indexRequest.source(jsonMap);
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("RabbitMQCustomer addQuestionReceive error. question: " + question);
            logger.error(e.getMessage());
            logger.error(e.fillInStackTrace());
        }

    }

    private String buildFeedData(EventModel model){
        Map<String,String> map = new HashMap<>();
        //触发用户是通用的
        User actor = userService.getUser(model.getActorId());
        if(actor == null){
            return null;
        }
        map.put("userId",String.valueOf(actor.getId()));
        map.put("userHead",actor.getHeadUrl());
        map.put("userName",actor.getName());

        if(model.getType() == EventType.COMMENT || (model.getType() == EventType.FOLLOW_QUESTION)){
            Question question = questionService.getById(model.getEntityId());
            if(question == null){
                return null;
            }
            map.put("questionId",String.valueOf(question.getId()));
            map.put("questionTitle",question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }
}
