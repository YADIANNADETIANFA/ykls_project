package com.kai.webby.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kai.webby.dao.UserDAO;
import com.kai.webby.model.User;
import com.kai.webby.util.PassWordUtil;
import com.kai.webby.util.ResultTemplate;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private static final Logger logger = Logger.getLogger(UserService.class);

    @Autowired
    UserDAO userDAO;

    public User getUser(int id)
    {
        return userDAO.selectById(id);
    }

    public User selectByName(String name) {
        return userDAO.selectByName(name);
    }

    public ResultTemplate register(JSONObject jsonObjectRec) throws Exception {

        String userName = jsonObjectRec.getString("userName");  //注册昵称
        String password = jsonObjectRec.getString("password");  //注册密码
        String qq = jsonObjectRec.getString("qq");  //注册人qq
        String sex = jsonObjectRec.getString("sex");    //性别    F:男     M：女
        String birth = jsonObjectRec.getString("birth");    //生日
        JSONArray type = jsonObjectRec.getJSONArray("type");  //感兴趣的内容
        String signed = jsonObjectRec.getString("signed");  //个性签名
        String headImgUrl = jsonObjectRec.getString("headImgUrl");    //头像

        if(StringUtils.isBlank(password)){
            return ResultTemplate.fail("my400", "密码不能为空", null);
        }
        if (password.equals(userName)) {
            return ResultTemplate.fail("my400", "密码不能与昵称相同", null);
        }
        if (password.equals(qq)) {
            return ResultTemplate.fail("my400", "密码不能与qq相同", null);
        }

        boolean isPwdAcceptable = PassWordUtil.isAcceptablePwd(password);
        if (!isPwdAcceptable) {
           return ResultTemplate.fail("my400", "密码需包含数字、大小写字母、特殊字符", null);
        }

        User user = userDAO.selectByName(userName);
        if (null != user) {
            return ResultTemplate.fail("my400", "该昵称已经被注册", null);
        }

        if (StringUtils.isBlank(headImgUrl)) {
            return ResultTemplate.fail("my400", "注册时请上传头像图片", null);
        }

        StringBuilder typeBuilder = new StringBuilder();
        for (int i = 0; i < type.size(); ++i) {
            typeBuilder.append(type.getString(i));
            typeBuilder.append(",");
        }
        String strType = typeBuilder.deleteCharAt(typeBuilder.length() - 1).toString();

        user = new User();
        user.setName(userName);
        String salt = UUID.randomUUID().toString().substring(0, 5);
        user.setSalt(salt);
        user.setHeadUrl(headImgUrl);
        user.setPassword((new Md5Hash(password, salt, 1024)).toHex());
        user.setQq(qq);
        user.setBirth(birth);
        user.setSex(sex);
        user.setType(strType);
        user.setSigned(signed);

        if (userDAO.addUser(user) <= 0) {
            throw new Exception("register error.");
        }
        return ResultTemplate.succ("my200", "注册成功~", null);
    }

    public User getUserInfoByName(String userName){
        return userDAO.selectByName(userName);
    }
}
