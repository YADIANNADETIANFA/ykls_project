package com.kai.webby.dao;

import com.kai.webby.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Mapper
@Component
public interface MessageDAO {

    int addMessage(Message message);

    List<Message> getConversationDetail(Map<String, Object> map);

    List<Message> getConversationList(Map<String, Object> map);

    int getConversationUnreadCount(Map<String, Object> map);

    //一共有多少组对话
    int getConversationListCount(@Param("localUserId") int localUserId);

    //每组对话里，一共有多少次讲话
    int getConversationDetailCount(@Param("conversationId") String conversationId);
}
