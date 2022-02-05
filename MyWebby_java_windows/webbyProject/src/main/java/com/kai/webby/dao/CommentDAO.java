package com.kai.webby.dao;

import com.kai.webby.model.Comment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface CommentDAO {

    int addComment(Comment comment);

    Comment getCommentById(@Param("id") int id);

    List<Comment> selectCommentByEntity(Map<String, Object> map);

    int getCommentCount(Map<String, Object> map);

    int getUserCommentCount(@Param("userId") int userId);
}
