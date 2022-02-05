package com.kai.webby.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * jwt工具，用来生成、校验token以及提取token中的信息
 * */
@Slf4j  //lmbok，用于log日志的
public class JwtUtil {

    //指定一个token的过期时间    --非rememberMe
    private static final long EXPIRE_TIME_NOT_REMEMBERME = 1000 * 60 * 60;    //1h
    //指定一个token的过期时间    --rememberMe
    private static final long EXPIRE_TIME_REMEMBERME = 1000 * 60 * 60 * 24 * 7;   //7天

    /**
     * 生成token
     * @param userId 用户编号
     * @param secret password + salt + MD5 + 1024Hash = secret
     * @param isRememberMe 前台页面是否勾选了“记住我”选项
     * @return token
     * */
    public static String getJwtToken(String userId, String secret, boolean isRememberMe){

        Date date = null;
        if (isRememberMe) {
            date = new Date(System.currentTimeMillis() + EXPIRE_TIME_REMEMBERME);
        } else {
            date = new Date(System.currentTimeMillis() + EXPIRE_TIME_NOT_REMEMBERME);
        }

        Algorithm algorithm = Algorithm.HMAC256(secret);

        //附带userId信息的token
        return JWT.create()
                .withClaim("userId", userId)    //Jwt内自包含的信息
                .withExpiresAt(date)    //token过期时间
                .sign(algorithm);   //签名算法
    }

    /**
     * token校验
     * @param token 待校验的token
     * @param userId 用户编号
     * @param secret password + salt + MD5 + 1024Hash = secret
     * @return 校验结果
     * */
    public static boolean verifyToken(String token, String userId, String secret){

        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).withClaim("userId", userId).build();
            //校验token
            DecodedJWT jwt = verifier.verify(token);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * 从token中获取到userId信息
     * @param token token
     * @return userId
     * */
    public static String getUserId(String token){

        try{
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asString();
        }catch(JWTDecodeException e){
            return null;
        }
    }

    /**
     * 判断token是否过期
     * @param token token
     * @return token是否过期
     * */
    public static boolean isExpire(String token){

        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().getTime() < System.currentTimeMillis();
    }
}
















