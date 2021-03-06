package com.kai.webby.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kai.webby.async.EventModel;
import com.kai.webby.async.EventType;
import com.kai.webby.model.*;
import com.kai.webby.service.*;
import com.kai.webby.util.ConstValue;
import com.kai.webby.util.DbSrcTrsUtil;
import com.kai.webby.util.ResultTemplate;
import com.kai.webby.util.StringUtil;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class QuestionControllerWebby {
    private static final Logger logger = Logger.getLogger(HomeControllerWebby.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    DbSrcTrsUtil dbSrcTrsUtil;

    //????????????????????????????????????????????????????????????
    @RequestMapping(path = {"/questionReviewWebby/{qid}"}, method = {RequestMethod.GET})
    @ResponseBody
    public String questionReview(@PathVariable("qid") int qid) {

        Subject subject = SecurityUtils.getSubject();

        try {
            JSONObject jsonObject = new JSONObject();

            //????????????
            Question question = questionService.getById(qid);
            //??????????????????url??????????????????????????????????????????????????????
            if (((User)subject.getPrincipal()).getId() != question.getUserId()) {
                ResultTemplate resultTemplate = ResultTemplate.succ("my403", "/questionReviewWebby/{qid} error", null);
                return JSON.toJSONString(resultTemplate);
            }
            jsonObject.put("question", question);

            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/questionReviewWebby/{qid} success", jsonObject.toJSONString());
            return JSON.toJSONString(resultTemplate);

        } catch (Exception e) {
            logger.error("/questionReviewWebby/{qid}??????" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "????????????????????????????????????");
            return JSON.toJSONString(jsonObject);
        }
    }

    @RequestMapping(path = {"/questionWebby/{qid}"}, method = {RequestMethod.GET})
    @ResponseBody
    public String questionDetail(@PathVariable("qid") int qid, @RequestParam(value = "currentPage") int currentPage) {

        Subject subject = SecurityUtils.getSubject();

        try {
            JSONObject jsonObject = new JSONObject();

            //????????????
            Question question = questionService.getById(qid);
            jsonObject.put("question", question);

            //?????????????????????
            User user = userService.getUser(question.getUserId());
            jsonObject.put("questionOwner", user);

            //????????????????????????????????????
            int totalCommentCount = commentService.getCommentCount(qid, EntityType.ENTITY_QUESTION);
            jsonObject.put("totalCommentCount", totalCommentCount);

            //??????????????????
            List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION, currentPage);
            JSONArray jsonArrayComments = new JSONArray();
            for (Comment comment : commentList) {
                JSONObject jsonObjectComment = new JSONObject();
                jsonObjectComment.put("comment", comment);
                //liked????????????????????????????????????????????????????????????1???????????????0??????????????????-1????????????
                if (subject.getPrincipal() == null) {
                    jsonObjectComment.put("liked", 0);
                } else {
                    jsonObjectComment.put("liked", likeService.getLikeStatus(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_COMMENT,comment.getId()));
                }
                jsonObjectComment.put("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT,comment.getId()));
                jsonObjectComment.put("user", userService.getUser(comment.getUserId()));
                jsonArrayComments.add(jsonObjectComment);
            }
            jsonObject.put("comments", jsonArrayComments);

            //??????followUsers??????
            List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION,qid,20);
            JSONArray jsonArrayFollowUsers = new JSONArray();
            for (Integer userId : users) {
                JSONObject jsonObjectFollowUser = new JSONObject();
                User userFollow = userService.getUser(userId);
                if (userFollow == null) {
                    continue;
                }
                jsonObjectFollowUser.put("name", userFollow.getName());
                jsonObjectFollowUser.put("headUrl", userFollow.getHeadUrl());
                jsonObjectFollowUser.put("id", userFollow.getId());
                jsonArrayFollowUsers.add(jsonObjectFollowUser);
            }
            jsonObject.put("followUsers", jsonArrayFollowUsers);

            //??????????????????????????????????????????
            if (subject.getPrincipal() != null) {
                jsonObject.put("followed", followService.isFollower(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_QUESTION,qid));
            } else {
                jsonObject.put("followed", false);
            }

            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/questionWebby/{qid} success", jsonObject.toJSONString());
            return JSON.toJSONString(resultTemplate);
        } catch (Exception e) {
            logger.error("/questionWebby/{qid}??????" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "????????????????????????????????????");
            return JSON.toJSONString(jsonObject);
        }
    }

    //???????????????????????????
    @RequestMapping(path = {"/questionWebby/delete"}, method = {RequestMethod.GET})
    @ResponseBody
    public String deleteQuestion(@RequestParam(value = "questionId", required = true) int questionId) {

        Subject subject = SecurityUtils.getSubject();

        //????????????
        Question question = questionService.getById(questionId);
        //??????????????????url??????????????????????????????????????????????????????
        if (((User)subject.getPrincipal()).getId() != question.getUserId()) {
            ResultTemplate resultTemplate = ResultTemplate.succ("my403", "/questionWebby/delete error", null);
            return JSON.toJSONString(resultTemplate);
        }

        TransactionStatus trs = null;
        try {
            trs = dbSrcTrsUtil.openTrs();

            //ES????????????????????????
            UpdateRequest updateRequest = new UpdateRequest("testquestion", String.valueOf(questionId));
            updateRequest.timeout(TimeValue.timeValueSeconds(3));
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", 1);
            updateRequest.doc(jsonMap);
            try{
                restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                logger.error("QuestionControllerWebby deleteQuestion error. questionId: " + questionId);
                logger.error(e.getMessage());
                logger.error(e.fillInStackTrace());
                throw e;
            }

            //mysql????????????????????????
            questionService.deleteQuestion(questionId);

            dbSrcTrsUtil.commitTrs(trs);
        } catch (Exception e) {
            if (null != trs) {
                dbSrcTrsUtil.rollbackTrs(trs);
            }
            logger.error("/questionWebby/delete??????" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "????????????????????????????????????");
            return JSON.toJSONString(jsonObject);
        }
        ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/questionWebby/delete success", null);
        return JSON.toJSONString(resultTemplate);
    }

    //???????????????????????????
    @RequestMapping(path = {"/questionWebby/add"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@Validated @RequestBody Question question,
                              @RequestParam(value = "operation", required = true, defaultValue = "add") String operation,
                              @RequestParam(value = "questionId", required = false) int questionId){

        Subject subject = SecurityUtils.getSubject();

        TransactionStatus trs = null;
        try {
            trs = dbSrcTrsUtil.openTrs();

            //??????????????????????????????????????????????????????
            if ("edit".equals(operation) && questionId != -1) {

                //ES???????????????????????????
                UpdateRequest updateRequest = new UpdateRequest("testquestion", String.valueOf(questionId));
                updateRequest.timeout(TimeValue.timeValueSeconds(3));
                Map<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("status", 1);
                updateRequest.doc(jsonMap);
                try{
                    restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
                } catch (IOException e) {
                    logger.error("QuestionControllerWebby addQuestion edit error. questionId: " + questionId);
                    logger.error(e.getMessage());
                    logger.error(e.fillInStackTrace());
                    throw e;
                }
                //mysql???????????????????????????
                questionService.deleteQuestion(questionId);
            }

            //??????????????????
            question.setUserId(((User)subject.getPrincipal()).getId()); //????????????????????????
            question.setCreatedDate(new Date());    //??????????????????
            question.setStatus(0);  //??????????????? 0-??????    1-?????????
            question.setCommentCount(0);    //?????????
            questionService.addQuestion(question);

            dbSrcTrsUtil.commitTrs(trs);
        } catch (Exception e) {
            if (null != trs) {
                dbSrcTrsUtil.rollbackTrs(trs);
            }
            logger.error("/questionWebby/add??????" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "????????????????????????????????????");
            return JSON.toJSONString(jsonObject);
        }

        //MQ????????????    ??????????????????
        try {
            rabbitTemplate.convertAndSend(ConstValue.ConstValue_YKLS_TOPIC, "addQuestion", new EventModel(EventType.ADD_QUESTION)
                    .setActorId(question.getUserId())
                    .setEntityId(question.getId())
                    .setExtraMessages("question", question));
        } catch (AmqpException amqpException) {
            logger.error(amqpException.getMessage());
        }

        ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/questionWebby/add success", null);
        return JSON.toJSONString(resultTemplate);
    }

    //??????md??????????????????
    @RequestMapping(path={"/upMdImg"}, method = {RequestMethod.POST})
    @ResponseBody
    public String upMdImg(@RequestParam("image") MultipartFile file) {
        try {

            byte[] bytes = file.getBytes();
            String originalName = file.getOriginalFilename();
            if (StringUtil.isEmpty(originalName)) {
                throw new Exception("file name is empty, please send another file for your md question.");
            }
            //??????originalName?????????substring??????????????????????????????????????????
            originalName = "forSpace" + originalName;

            String imageFileName = UUID.randomUUID().toString().substring(0, 5) +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) +
                    originalName.substring(originalName.length() - 6);

            // linux and windows
            String filePath = File.separator + "home" + File.separator + "ykls22" + File.separator +
                    "myWebbyJava" + File.separator + "webbyImages" + File.separator +
                    "mdImgs" + File.separator + imageFileName;
            Path path = Paths.get(filePath);
            Files.write(path, bytes);

            // Path path = Paths.get("D:\\myProject\\Webby\\MyWebby_java_windows\\webbyImages\\mdImgs\\" + imageFileName);
            // Files.write(path, bytes);

            JSONObject jsonObject = new JSONObject();

            //linux and windows
            jsonObject.put("mdImgUrl", "http://192.168.1.10:8082/img/mdImgs/" + imageFileName);

            // aliyun
            // jsonObject.put("mdImgUrl", "http://120.*.*.30:8082/img/mdImgs/" + imageFileName);

            // jsonObject.put("mdImgUrl", "http://127.0.0.1:8082/img/mdImgs/" + imageFileName);

            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "md??????????????????", jsonObject.toJSONString());
            return JSON.toJSONString(resultTemplate);
        } catch(Exception e) {
            logger.error("md??????????????????" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "????????????????????????????????????");
            return JSON.toJSONString(jsonObject);
        }
    }

    //?????????????????????md??????
    @RequestMapping(path={"/deleteMdImg"}, method = {RequestMethod.POST})
    @ResponseBody
    public String deleteMdImg(@RequestBody(required = true) String strJson) {
        try {
            ResultTemplate resultTemplate = null;

            JSONObject jsonObjectRec = JSONObject.parseObject(strJson);
            String strDeleteImgUrl = jsonObjectRec.getString("deleteImgUrl");

            if (StringUtil.isEmpty(strDeleteImgUrl)) {
                resultTemplate = ResultTemplate.fail("my402", "???????????????md??????", null);
                return JSON.toJSONString(resultTemplate);
            }

            // linux and windows
            String fileName = strDeleteImgUrl.replaceAll("http://192.168.1.10:8082/img/mdImgs/", "");

            // aliyun
            // String fileName = strDeleteImgUrl.replaceAll("http://120.*.*.30:8082/img/mdImgs/", "");

            String filePath = File.separator + "home" + File.separator + "ykls22" + File.separator +
                    "myWebbyJava" + File.separator + "webbyImages" + File.separator +
                    "mdImgs" + File.separator + fileName;
            Path path = Paths.get(filePath);

            // String fileName = strDeleteImgUrl.replaceAll("http://127.0.0.1:8082/img/mdImgs/", "");
            // Path path = Paths.get("D:\\myProject\\Webby\\MyWebby_java_windows\\webbyImages\\mdImgs\\" + fileName);

            boolean isDeleted = Files.deleteIfExists(path);
            if (!isDeleted) {
                resultTemplate = ResultTemplate.fail("my402", "???????????????md??????", null);
                return JSON.toJSONString(resultTemplate);
            }

            resultTemplate = ResultTemplate.succ("my200", "md??????????????????", null);
            return JSON.toJSONString(resultTemplate);

        } catch(IOException e) {
            logger.error("md??????????????????" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "????????????????????????????????????");
            return JSON.toJSONString(jsonObject);
        }
    }
}
