package com.kai.webby.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;     //自增id

    @NotBlank(message = "标题不能为空")
    private String title;   //标题

    @NotBlank(message = "摘要不能为空")
    private String description;     //摘要

    private int userId;     //提出人，所属用户

    private Date createdDate;   //最新修改时间

    private int commentCount;   //评论数

    private int status; //0-正常  1-已删除

    @NotBlank(message = "md内容不能为空")
    private String content; //md内容

}
