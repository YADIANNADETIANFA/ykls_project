package com.kai.webby.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kai.webby.controller.HomeControllerWebby;
import com.kai.webby.model.EntityType;
import com.kai.webby.model.Question;
import com.kai.webby.model.User;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SearchESService {
    private static final Logger logger = Logger.getLogger(SearchESService.class);

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FollowService followService;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public int searchQuestionsByUser(String searchWords, int searchPage, JSONArray jsonArray) {

        int countRes = 0;
        User user = userService.selectByName(searchWords);
        if (null != user) {
            List<Question> questionList = questionService.getLatestQuestions(user.getId(), searchPage);
            int i = 0;
            for (Question question : questionList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", i++);
                jsonObject.put("questionId", question.getId()); //????????????id
                jsonObject.put("questionTitle", question.getTitle());   //??????????????????
                jsonObject.put("questionDescription", question.getDescription());   //??????????????????
                jsonObject.put("questionCreatedDate", question.getCreatedDate());   //??????????????????????????????
                jsonObject.put("questionUserId", question.getUserId()); //????????????????????????????????????
                jsonObject.put("questionCommentCount", question.getCommentCount()); //?????????????????????

                jsonObject.put("userId", user.getId());
                jsonObject.put("userName", user.getName());
                jsonObject.put("headUrl", user.getHeadUrl());

                Long followCount = followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId());
                jsonObject.put("followCount", followCount);

                //?????????????????????????????????????????????????????????
                Subject subject = SecurityUtils.getSubject();
                jsonObject.put("followed", followService.isFollower(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_QUESTION, question.getId()));

                jsonArray.add(jsonObject);
            }

            countRes = questionService.getQuestionsCountByUserId(user.getId());
        }
        return countRes;
    }

    public int searchQuestionsByQuestion(String searchWords, int searchPage, JSONArray jsonArray) {

        int countRes = 0;

        SearchRequest searchRequest = new SearchRequest("testquestion");

        //??????????????????
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //?????????????????????????????????????????????????????????
        boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchWords, "title", "description", "content"));
        boolQueryBuilder.filter(QueryBuilders.termQuery("status", 0));

        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(10 * (searchPage - 1));
        //????????????10???
        searchSourceBuilder.size(10);

        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            int i = 0;
            for (SearchHit documentFields : searchResponse.getHits().getHits()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", i++);
                jsonObject.put("questionId", documentFields.getId()); //????????????id
                jsonObject.put("questionTitle", documentFields.getSourceAsMap().get("title"));   //??????????????????
                jsonObject.put("questionDescription", documentFields.getSourceAsMap().get("description"));   //??????????????????
                jsonObject.put("questionCreatedDate", documentFields.getSourceAsMap().get("created_date"));   //??????????????????????????????
                jsonObject.put("questionUserId", documentFields.getSourceAsMap().get("user_id")); //????????????????????????????????????
                jsonObject.put("questionCommentCount", documentFields.getSourceAsMap().get("comment_count")); //?????????????????????

                User questionUser = userService.getUser((int)(documentFields.getSourceAsMap().get("user_id")));
                jsonObject.put("userId", questionUser.getId());
                jsonObject.put("userName", questionUser.getName());
                jsonObject.put("headUrl", questionUser.getHeadUrl());

                Long followCount = followService.getFollowerCount(EntityType.ENTITY_QUESTION, Integer.parseInt(documentFields.getId()));
                jsonObject.put("followCount", followCount);

                //?????????????????????????????????????????????????????????
                Subject subject = SecurityUtils.getSubject();
                jsonObject.put("followed", followService.isFollower(((User)subject.getPrincipal()).getId(), EntityType.ENTITY_QUESTION, Integer.parseInt(documentFields.getId())));

                jsonArray.add(jsonObject);
            }

            //??????????????????????????????
            countRes = (int)searchResponse.getHits().getTotalHits().value;
        } catch (IOException e) {
            logger.error("SearchESService searchQuestionsByQuestion error. searchWords: " + searchWords);
            logger.error(e.getMessage());
            logger.error(e.fillInStackTrace());
        }

        return countRes;
    }

}







