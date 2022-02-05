package com.kai.webby.async;

//异步事件类型
public enum EventType {

    LIKE(0),    //对评论点赞
    COMMENT(1), //新增评论
    LOGIN(2),   //登录事件  暂未使用
    MAIL(3),    //发送邮件事件    暂未使用
    FOLLOW_USER(4),  //关注某用户
    FOLLOW_QUESTION(5), //关注某问题博客
    UNFOLLOW_USER(6),    //取关某用户    暂未使用
    UNFOLLOW_QUESTION(7),   //取关某问题博客   暂未使用
    ADD_QUESTION(8);    //新增问题博客

    private final int value;
    EventType(int value){
        this.value = value;
    }
    public int getValue(){
        return value;
    }
}
