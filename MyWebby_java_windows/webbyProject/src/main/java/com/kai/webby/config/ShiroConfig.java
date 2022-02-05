package com.kai.webby.config;

import com.kai.webby.shiro.MyRealm;
import com.kai.webby.util.JwtFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

@Configuration
//todo: 这里用Slf4j记录日志，是有什么特殊原因么？能改成log4j么？
@Slf4j
public class ShiroConfig {

    /**
     * 由Spring管理Shiro的生命周期
     * */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启对Shiro的注解支持
     * */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        //强制使用cglib，防止重复代理和可能引起代理出错的问题
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    /**
     * 创建ShiroFilter（用于拦截所有请求，对受限资源进行shiro的认证和授权判断）
     * Shiro提供了丰富的过滤器（annon等），不过在这里就需要加入我们自己定义的JwtFilter了
     * */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);

        //添加自己的过滤器并取名jwt
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", new JwtFilter());
        shiroFilterFactoryBean.setFilters(filterMap);

        //配置系统的受限资源以及对应的过滤器
        //使用LinkedHashMap是为了保持顺序，Filter的配置顺序不能随便打乱，过滤器是按照我们配置的顺序来执行的。范围大的过滤器要放在后面，
        // /**这条如果放在前面，那么一来就匹配上了，就不会继续再往后走了。
        Map<String, String> ruleMap = new HashMap<>();
        ruleMap.put("/loginWebby", "anon");    //登录路径，注册路径都需要放行，不进行拦截
        ruleMap.put("/registerWebby", "anon");
        ruleMap.put("/upHeadImg","anon");
        ruleMap.put("/img/headImgs/**","anon");
        ruleMap.put("/img/mdImgs/**","anon");
        ruleMap.put("/**", "jwt");  // /**一般放在最下，表示对所有资源起作用，使用JwtFilter
        shiroFilterFactoryBean.setFilterChainDefinitionMap(ruleMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 创建安全管理器（会自动设置到SecurityUtils中设置这个安全管理器）
     * SecurityUtils可以用来获取subject对象
     * 另外，getShiroFilterFactoryBean方法中通过将 DefaultWebSecurityManager设置为参数，会自动到工厂中找已经创建好的 DefaultWebSecurityManager
     * */
    @Bean("securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(MyRealm realm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        //给安全管理器设置realm
        securityManager.setRealm(realm);

        //关闭shiro的session（无状态的方式使用shiro）
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);

        return securityManager;
    }

    //创建自定义Realm，注入到spring容器中
    @Bean
    public MyRealm getRealm(){
        MyRealm realm = new MyRealm();
        return realm;
    }
}
