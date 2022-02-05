package com.kai.webby.dao;

import com.kai.webby.model.Question;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Mapper
public interface QuestionDAO {

    int addQuestion(Question question);

    int deleteQuestion(@Param("id") int id);

    List<Question> selectLastestQuestions(Map<String, Object> map);

    List<Question> selectQuestions(@Param("userId") int userId);

    Question getById(@Param("id") int id);

    int updateCommentCount(Map<String, Object> map);

    int getQuestionsCount();

    int getQuestionsCountByUserId(@Param("userId") int userId);

}
