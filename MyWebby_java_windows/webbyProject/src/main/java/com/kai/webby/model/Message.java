package com.kai.webby.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int fromId;
    private int toId;
    private String content;
    private int hasRead;
    private String conversationId;
    private Date createdDate;

    public String getConversationId() {
        if (fromId < toId) {
            return String.format("%d_%d",fromId,toId);
        } else {
            return String.format("%d_%d",toId,fromId);
        }
    }

}
