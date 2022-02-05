package com.kai.webby.util;

import lombok.Data;

@Data   //lombok    https://www.jianshu.com/p/c1ee7e4247bf
public class ResultTemplate {

    /**
     * my200：各种操作成功
     * my400: 登陆注册过程出现问题
     * my401: token验证存在问题
     * my402: 服务器异常，请联系管理员
     * my403: 用户操作不当
     * */
    private String code;    //我们自定义的状态码，非HTTP状态码
    private String msg;     //当前处理状态
    private Object data;    //可能存在的响应数据

    public static ResultTemplate succ(String code, String msg, Object data){

        ResultTemplate resultTemplate = new ResultTemplate();
        resultTemplate.setCode(code);
        resultTemplate.setMsg(msg);
        resultTemplate.setData(data);
        return resultTemplate;
    }

    public static ResultTemplate fail(String code, String msg, Object data){

        ResultTemplate resultTemplate = new ResultTemplate();
        resultTemplate.setCode(code);
        resultTemplate.setMsg(msg);
        resultTemplate.setData(data);
        return resultTemplate;
    }

}
