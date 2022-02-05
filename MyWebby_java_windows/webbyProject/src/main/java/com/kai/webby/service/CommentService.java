package com.kai.webby.service;

import com.kai.webby.dao.CommentDAO;
import com.kai.webby.model.Comment;
import com.kai.webby.util.DbSrcTrsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;

    @Autowired
    SensitiveService sensitiveService;

    public List<Comment> getCommentsByEntity(int entityId, String entityType, int currentPage){
        int limitFirst = (currentPage - 1) * 10;   //起始位置
        int limitSec = currentPage * 10;  //偏移量

        Map<String, Object> map = new HashMap<>();
        map.put("entityId", entityId);
        map.put("entityType", entityType);
        map.put("limitFirst", limitFirst);
        map.put("limitSec", limitSec);
        return commentDAO.selectCommentByEntity(map);
    }

    public void addComment(Comment comment) throws Exception {
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));

        if (commentDAO.addComment(comment) <= 0) {
            throw new Exception("addComment error.");
        }
    }

    public int getCommentCount(int entityId, String entityType) {
        Map<String, Object> map = new HashMap<>();
        map.put("entityId", entityId);
        map.put("entityType", entityType);
        return commentDAO.getCommentCount(map);
    }

    public int getUserCommentCount(int userId){ return commentDAO.getUserCommentCount(userId);}

    public Comment getCommentById(int id){ return commentDAO.getCommentById(id);}
}
