package com.kai.webby.util;


import org.apache.shiro.authc.AuthenticationToken;

/**
 * shiro原应使用自身的UsernamePasswordToken类token，但我们这里不使用
 * 我们目前使用的token是自己JwtUtil所生成的token
 * 为了shiro能使用我们当前使用的token，这里做一下处理
 * getPrincipal()与getCredentials()直接返回token，之后交给JwtUtil去解析处理
 * */
public class JwtToken implements AuthenticationToken {

    private String token;

    public JwtToken(String token){
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return this.token;
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }
}
