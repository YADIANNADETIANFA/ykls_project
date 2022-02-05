package com.kai.webby.service;

import com.kai.webby.dao.MessageDAO;
import com.kai.webby.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    @Autowired
    MessageDAO messageDAO;

    @Autowired
    SensitiveService sensitiveService;

    public int addMessage(Message message){
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDAO.addMessage(message) > 0 ? message.getId() : 0;
    }

    public List<Message> getConversationDetail(String conversationId, int offset, int limit){
        Map<String, Object> map = new HashMap<>();
        map.put("conversationId", conversationId);
        map.put("offset", offset);
        map.put("limit", limit);
        return messageDAO.getConversationDetail(map);
    }

    public List<Message> getConversationList(int userId, int currentPage){
        int limitFirst = (currentPage - 1) * 10;    //起始位置
        int limitSec = currentPage * 10;    //偏移量

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("limitFirst", limitFirst);
        map.put("limitSec", limitSec);
        return messageDAO.getConversationList(map);
    }

    public int getConversationUnreadCount(int userId, String conversationId){
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("conversationId", conversationId);
        return messageDAO.getConversationUnreadCount(map);
    }

    public int getConversationListCount(int localUserId) {
        return messageDAO.getConversationListCount(localUserId);
    }

    public int getConversationDetailCount(String conversationId) {
        return messageDAO.getConversationDetailCount(conversationId);
    }
}
