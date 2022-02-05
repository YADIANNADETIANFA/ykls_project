package com.kai.webby.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kai.webby.model.Message;
import com.kai.webby.model.User;
import com.kai.webby.service.MessageService;
import com.kai.webby.service.UserService;
import com.kai.webby.util.ResultTemplate;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
public class MessageControllerWebby {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    private static final Logger logger = Logger.getLogger(MessageControllerWebby.class);

    /*
    * 展示私信列表，不是展示某一段对话的所有对话
    * 展示某一段对话的所有对话是 /msgWebby/detail
    * */
    @RequestMapping(path = {"/msgWebby/list"}, method = {RequestMethod.GET})
    @ResponseBody
    public String getConversationList(@RequestParam(value = "currentPage") int currentPage) {

        Subject subject = SecurityUtils.getSubject();

        try {
            int localUserId = ((User)subject.getPrincipal()).getId();
            List<Message> conversationList = messageService.getConversationList(localUserId, currentPage);
            JSONArray jsonArray = new JSONArray();
            for (Message message : conversationList) {
                JSONObject jsonObjectMsg = new JSONObject();
                jsonObjectMsg.put("message", message);
                //头像展示的时候，展示对话过程中，另一个人的头像
                int friendId = message.getFromId() == localUserId ? message.getToId() : message.getFromId();
                jsonObjectMsg.put("user", userService.getUser(friendId));
                jsonObjectMsg.put("unread", messageService.getConversationUnreadCount(localUserId, message.getConversationId()));
                jsonArray.add(jsonObjectMsg);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("conversations", jsonArray);

            int conversationCount = messageService.getConversationListCount(localUserId);
            jsonObject.put("conversationCount", conversationCount);

            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/msgWebby/list success", jsonObject.toJSONString());
            return JSON.toJSONString(resultTemplate);
        } catch (Exception e) {
            logger.error("/msgWebby/list异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }

    @RequestMapping(path = {"/msgWebby/detail"}, method = {RequestMethod.GET})
    @ResponseBody
    public String getConversationDetail(@RequestParam(value = "conversationId") String conversationId,
                                        @RequestParam(value = "beginIndex") int beginIndex){
        try {
            JSONArray jsonArray = new JSONArray();
            List<Message> messageList = messageService.getConversationDetail(conversationId, beginIndex, 20);
            for (Message message : messageList) {
                JSONObject jsonObjectMsg = new JSONObject();
                jsonObjectMsg.put("message", message);
                //每段对话中，每条消息的发送者，用于头像展示
                jsonObjectMsg.put("user", userService.getUser(message.getFromId()));
                jsonArray.add(jsonObjectMsg);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("conversation", jsonArray);

            int totalNum = messageService.getConversationDetailCount(conversationId);
            jsonObject.put("totalNum", totalNum);

            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/msgWebby/detail success", jsonObject.toJSONString());
            return JSON.toJSONString(resultTemplate);
        } catch (Exception e) {
            logger.error("/msgWebby/detail异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }

    @RequestMapping(path = {"/msgWebby/addMessage"},method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestBody(required = true) String strJson){

        Subject subject = SecurityUtils.getSubject();

        try {
            JSONObject jsonObjectRec = JSON.parseObject(strJson);
            String toName = jsonObjectRec.getString("toName");
            String content = jsonObjectRec.getString("content");

            User user = userService.selectByName(toName);
            if (null == user) {
                ResultTemplate resultTemplate = ResultTemplate.fail("my403", "target user not exist", null);
                return JSON.toJSONString(resultTemplate);
            }

            Message message = new Message();
            message.setCreatedDate(new Date());
            message.setFromId(((User)subject.getPrincipal()).getId());
            message.setToId(user.getId());
            message.setContent(content);
            messageService.addMessage(message);

            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "/msgWebby/addMessage success", null);
            return JSON.toJSONString(resultTemplate);
        } catch(Exception e) {
            logger.error("/msgWebby/addMessage异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }
}
