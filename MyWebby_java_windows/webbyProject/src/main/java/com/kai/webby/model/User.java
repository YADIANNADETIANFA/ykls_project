package com.kai.webby.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;     //自增id
    private String name;    //昵称
    private String password;    //密码
    private String salt;    //盐
    private String headUrl; //头像
    private String qq;  //qq
    private String role;    //身份角色
    private String permission;  //权限
    private String birth;   //生日
    private String sex; //性别    F：男     M：女
    private String type;    //可能喜欢
    private String signed;    //签名
}
