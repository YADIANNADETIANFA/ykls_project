package com.kai.webby.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id; //自增id
    private int userId; //评论内容
    private int entityId;   //评论人，所属用户
    private String entityType;  //所评论问题的自增id
    private String content; //对问题进行评论，固定QUESTION
    private Date createdDate;   //最近更新时间
    private int status; //状态
}
