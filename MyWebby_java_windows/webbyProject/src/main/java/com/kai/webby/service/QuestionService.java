package com.kai.webby.service;

import com.kai.webby.dao.QuestionDAO;
import com.kai.webby.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService {

    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;

    public List<Question> getLatestQuestions(int userId, int currentPage)
    {
        int limitFirst = (currentPage - 1) * 10;    //起始位置
        int limitSec = currentPage * 10;    //偏移量

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("limitFirst", limitFirst);
        map.put("limitSec", limitSec);
        return questionDAO.selectLastestQuestions(map);
    }

    public Question getById(int id)
    {
        return questionDAO.getById(id);
    }

    public void addQuestion(Question question) throws Exception {

        //转义HTML关键字，防止注入攻击
        //当前仅针对标题和摘要
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setDescription(HtmlUtils.htmlEscape(question.getDescription()));

        //敏感词过滤
        //当前仅针对标题和摘要
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setDescription(sensitiveService.filter(question.getDescription()));

        //sql已将自增长id回写到question中，线程安全
        if (questionDAO.addQuestion(question) <= 0) {
            throw new Exception("addQuestion error.");
        }
    }

    public void deleteQuestion(int id) throws Exception {
        if (questionDAO.deleteQuestion(id) <= 0) {
            throw new Exception("deleteQuestion error.");
        }
    }

    public void updateCommentCount(int id, int count) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("commentCount", count);
        if (questionDAO.updateCommentCount(map) <= 0) {
            throw new Exception("updateCommentCount error.");
        }
    }

    public int getQuestionsCount() {
        return questionDAO.getQuestionsCount();
    }

    public int getQuestionsCountByUserId(int userId) {
        return questionDAO.getQuestionsCountByUserId(userId);
    }

}
