package com.kai.webby.async;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class EventModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private EventType type; //异步事件的类型
    private int actorId;    //异步事件的发起人（例如当前登录用户等）
    private String entityType; //entityType与entityId共同指出异步事件所指向的对象
    private int entityId;
    private int entityOwnerId;  //被异步事件影响到的人（例如点赞时，被赞的人）
    private Map<String, Object> extraMessages = new HashMap<>();  //异步事件的额外信息

    public EventModel(EventType type){
        this.type = type;
    }

    public EventModel setExtraMessages(String key, Object obj) {
        extraMessages.put(key, obj);
        return this;
    }

    public Object getExtraMessages(String key) {
        return extraMessages.get(key);
    }

}
