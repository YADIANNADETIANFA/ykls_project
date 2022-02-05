package com.kai.webby.shiro;

import com.kai.webby.model.User;
import com.kai.webby.service.UserService;
import com.kai.webby.util.JwtToken;
import com.kai.webby.util.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自定义Realm
 * */
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    /**
     * 限定这个Realm只能处理JwtToken（不加的话会报错）
     * */
    @Override
    public boolean supports(AuthenticationToken token){
        return token instanceof JwtToken;
    }

    /**
     * 授权
     * */
    //TODO: 补全授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        //获取当前登录对象
        Subject subject = SecurityUtils.getSubject();
        User currentUser = (User) subject.getPrincipal();

        //设置当前用户的角色
        info.addRole(currentUser.getRole());

        return info;
    }

    /**
     * 认证
     * */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {

        //JwtToken中重写了这个方法
        String token = (String) auth.getCredentials();

        //获取userId
        String userId = JwtUtil.getUserId(token);

        if(null == userId){
            throw new UnknownAccountException();
        }

        //根据userId，查询Mysql，获取完整正确的用户信息
        User user = userService.getUser(Integer.parseInt(userId));

        //用户不存在
        if(null == user){
            throw new UnknownAccountException();
        }

        //密码错误（token:前台传过来的token， userId:token中自包含的用户编号，
        // user.getPassword(): Mysql内存储的，该userId的真正密码 + salt + MD5 + 1024Hash后的结果，即user表内的password字段）
        if(!JwtUtil.verifyToken(token, userId, user.getPassword())){
            throw new IncorrectCredentialsException();
        }

        //token过期
        if(JwtUtil.isExpire(token)){
            throw new ExpiredCredentialsException();
        }

        //认证完毕且通过   后续使用subject，不再使用hostHolder
        //hostHolder.setUser(user);

        return new SimpleAuthenticationInfo(user, token, getName());
    }
}
















