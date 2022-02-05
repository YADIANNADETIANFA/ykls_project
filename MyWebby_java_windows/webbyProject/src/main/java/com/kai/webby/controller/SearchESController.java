package com.kai.webby.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kai.webby.service.*;
import com.kai.webby.util.ResultTemplate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSONObject;

@Controller
public class SearchESController {
    private static final Logger logger = Logger.getLogger(SearchESController.class);

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @Autowired
    SearchESService searchesService;

    /*
     *   后续优化的空间：
     * 1、未利用用户注册时的标签
     * */
    //searchSelect: 1：搜用户   2：搜帖子
    //后续最好利用上个人偏好标签
    @RequestMapping(path={"/searchWebby"}, method = {RequestMethod.GET})
    @ResponseBody
    public String searchWebby(@RequestParam(value = "searchWords", required = false) String searchWords,
                              @RequestParam(value = "searchSelect", required = false) String searchSelect,
                              @RequestParam(value = "searchPage", required = false) int searchPage) {

        try {
            com.alibaba.fastjson.JSONObject jsonObject = new JSONObject();

            JSONArray jsonArray = new JSONArray();
            int searchCount = 0;

            if ("1".equals(searchSelect)) {
                searchCount = searchesService.searchQuestionsByUser(searchWords, searchPage, jsonArray);
            } else if ("2".equals(searchSelect)) {
                searchCount = searchesService.searchQuestionsByQuestion(searchWords, searchPage, jsonArray);
            }

            jsonObject.put("searchESQuestions", jsonArray.toJSONString());
            jsonObject.put("searchESCount", searchCount);

            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/searchWebby success", jsonObject.toJSONString());
            return JSON.toJSONString(resultTemplate);

        } catch (Exception e) {
            logger.error("/searchWebby异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }
}
