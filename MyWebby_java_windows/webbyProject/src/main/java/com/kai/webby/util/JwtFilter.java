package com.kai.webby.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * jwt过滤器，作为shiro的过滤器，对请求进行拦截并处理
 * */
//TODO: 如果不好使，可以试试vueblog那个
@Slf4j
@Component
public class JwtFilter extends BasicHttpAuthenticationFilter {

    /**
     * 进行token的验证
     * */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception{

        //在请求头中获取token
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("Authorization");   //前端命名Authorization

        //token不存在
        if(null == token || "".equals(token) || "null".equals(token)){
            ResultTemplate resultTemplate = ResultTemplate.fail("my401", "无token，访问失败，请先登录", null);
            tokenFail(response, resultTemplate);
            return false;
        }

        //token存在，进行验证
        JwtToken jwtToken = new JwtToken(token);
        try{
            //通过subject，提交给MyRealm进行登陆验证与授权
            SecurityUtils.getSubject().login(jwtToken);
            return true;
        }catch (ExpiredCredentialsException e){
            //TODO:前台记得把token删了。。虽然直接覆盖也可以
            ResultTemplate resultTemplate = ResultTemplate.fail("my401", "token过期，请重新登录", null);
            tokenFail(response, resultTemplate);
            e.printStackTrace();
            return false;
        }catch (ShiroException e){
            //其他情况抛出的异常统一处理
            ResultTemplate resultTemplate = ResultTemplate.fail("my401", "token异常，无效token", null);
            tokenFail(response, resultTemplate);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * executeLogin      return false后，调用此方法
     * 如果不重写该方法，executeLogin return false后，会弹出一个未知的登陆页面，点击取消后才会走后续流程
     * 重写后，Vue将在then(res=>)中接到响应，状态码200；不重写，Vue将在catch(=>)中接到响应，状态码401
     * */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response)throws Exception{
        return false;
    }

    /**
     * token验证失败，json形式返回结果
     * */
    private void tokenFail(ServletResponse response, ResultTemplate resultTemplate) throws IOException{

        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        String jsonRes = JSON.toJSONString(resultTemplate);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        httpServletResponse.getOutputStream().write(jsonRes.getBytes());
    }

    /**
     * 过滤器拦截请求的入口方法
     * */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue){

        try{
            //token验证
            return executeLogin(request, response);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 处理跨域
     * */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception{

        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(org.springframework.http.HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
