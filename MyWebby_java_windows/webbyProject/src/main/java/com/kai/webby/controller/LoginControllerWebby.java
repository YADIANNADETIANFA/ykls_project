package com.kai.webby.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kai.webby.model.User;
import com.kai.webby.service.UserService;
import com.kai.webby.util.JwtToken;
import com.kai.webby.util.JwtUtil;
import com.kai.webby.util.ResultTemplate;
import com.kai.webby.util.StringUtil;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Controller
public class LoginControllerWebby {
    private static final Logger logger = Logger.getLogger(LoginControllerWebby.class);

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/registerWebby"}, method = {RequestMethod.POST})
    @ResponseBody
    public String register(@RequestBody(required = false) String strJson) {

        try {
            JSONObject jsonObjectRec = JSON.parseObject(strJson);
            ResultTemplate resultTemplate = userService.register(jsonObjectRec);
            return JSON.toJSONString(resultTemplate);
        } catch (Exception e) {
            logger.error("注册异常" + e.getMessage());
            logger.error("param strJson: " + strJson);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }

    //上传头像图片到服务器
    @RequestMapping(path={"/upHeadImg"}, method = {RequestMethod.POST})
    @ResponseBody
    public String upHeadImg(@RequestParam("file") MultipartFile file) {

        try {
            byte[] bytes = file.getBytes();
            String originalName = file.getOriginalFilename();
            if (StringUtil.isEmpty(originalName)) {
                throw new Exception("file name is empty, please send another file for header.");
            }
            //防止originalName过短时substring时越界，统一在前面加一个前缀
            originalName = "forSpace" + originalName;

            String imageFileName = UUID.randomUUID().toString().substring(0, 5) +
                                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) +
                                    originalName.substring(originalName.length() - 6);

            // linux and windows
            String filePath = File.separator + "home" + File.separator + "ykls22" + File.separator +
                    "myWebbyJava" + File.separator + "webbyImages" + File.separator +
                    "headImgs" + File.separator + imageFileName;
            Path path = Paths.get(filePath);
            Files.write(path, bytes);

            // Path path = Paths.get("D:\\myProject\\Webby\\MyWebby_java_windows\\webbyImages\\headImgs\\" + imageFileName);
            // Files.write(path, bytes);

            JSONObject jsonObject = new JSONObject();

            // linux and windows
            jsonObject.put("headImgUrl", "http://192.168.1.10:8082/img/headImgs/" + imageFileName);

            // aliyun
            // jsonObject.put("headImgUrl", "http://120.*.*.30:8082/img/headImgs/" + imageFileName);

            // jsonObject.put("headImgUrl", "http://127.0.0.1:8082/img/headImgs/" + imageFileName);

            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "头像上传成功", jsonObject.toJSONString());
            return JSON.toJSONString(resultTemplate);
        } catch(Exception e){
            logger.error("头像上传异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }

    @RequestMapping(path={"/loginWebby"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String login(@RequestBody(required = false) String strJson,
                        @RequestParam(value = "rememberMe", required = false, defaultValue = "false") boolean rememberMe){
        try {
            JSONObject jsonObjectRec = JSON.parseObject(strJson);
            String userName = jsonObjectRec.getString("userName");
            String password = jsonObjectRec.getString("password");

            ResultTemplate resultTemplate = null;

            //尝试获取正确的用户信息
            User user = userService.getUserInfoByName(userName);
            if (null == user) {
                resultTemplate = ResultTemplate.fail("my400", "无效用户，用户不存在", null);
                return JSON.toJSONString(resultTemplate);
            }

            //输入的password + salt + 1024Hash，结果作为生成token的密钥
            //明文密码：password     数据库存储的加密密码：password + salt + 1024Hash，调用的是shiro框架所提供的Md5Hash方法
            //Jwt的第三部分，即签名，需要一个秘钥，这里偷懒，用加密密码做了这个秘钥（反正秘钥也绝不可以对外给出）。     签名的作用是可识别Jwt的内容是否被篡改。   验签Jwt的时候用的也是该秘钥，详见JwtUtil类的verifyToken方法
            String salt = user.getSalt();
            Md5Hash md5Hash = new Md5Hash(password, salt, 1024);
            //生成token字符串
            String token = JwtUtil.getJwtToken(String.valueOf(user.getId()), md5Hash.toHex(), rememberMe);
            //转换成jwtToken，才可被shiro识别
            JwtToken jwtToken = new JwtToken(token);
            /*
            * 拿到subject对象
            * 注意，在web环境中，只要ShiroConfig类中创建了安全管理器securityManager，则这里会自动给shiro的安全工具类SecurityUtils中，注入安全管理器
            * */
            Subject subject = SecurityUtils.getSubject();

            //进行认证
            try {
                subject.login(jwtToken);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token", token);
                User userSend = (User)subject.getPrincipal();
                userSend.setPassword("hidden");
                userSend.setSalt("hidden");
                jsonObject.put("user", userSend);
                resultTemplate = ResultTemplate.succ("my200", "登陆成功", jsonObject.toJSONString());
                return JSON.toJSONString(resultTemplate);
            }catch (UnknownAccountException e){
                e.printStackTrace();
                resultTemplate = ResultTemplate.fail("my400", "无效用户，用户不存在", null);
                return JSON.toJSONString(resultTemplate);
            }catch (IncorrectCredentialsException e){
                e.printStackTrace();
                resultTemplate = ResultTemplate.fail("my400", "用户名或密码错误", null);
                return JSON.toJSONString(resultTemplate);
            }catch (ExpiredCredentialsException e){
                e.printStackTrace();
                resultTemplate = ResultTemplate.fail("my400", "token过期，请重新登录", null);
                return JSON.toJSONString(resultTemplate);
            }
        } catch (Exception e) {
            logger.error("登录异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }

    @RequestMapping(path={"/logoutWebby"},method = {RequestMethod.GET})
    @ResponseBody
    public String logout(){

        SecurityUtils.getSubject().logout();
        try {
            ResultTemplate resultTemplate = ResultTemplate.succ("my200", "已成功退出", null);
            return JSON.toJSONString(resultTemplate);
        } catch(Exception e) {
            logger.error("退出登录异常" + e.getMessage());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "my402");
            jsonObject.put("msg", "服务器异常，请联系管理员");
            return JSON.toJSONString(jsonObject);
        }
    }

}

/*
* 关于浏览器POST/GET请求的报错问题：
* 视频中他是从@RequestMapping(path={"/reglogin"},method = {RequestMethod.GET})进去的页面，然后再进行注册和登陆功能，此时与login.html中的表单有关了，
* 而表单中method="post"，走的是POST功能。
* 另外，浏览器根据url访问服务器，走get或者post请求方法，我们手工暂时控制不了，这是根据url的性质来的
*你单独访问@RequestMapping(path = {"/reg/"}, method = {RequestMethod.POST})浏览器会报错：405，GET不被允许。因为你url的性质决定你浏览器使用的是GET方法，而你的后台只能POST。所以，按照他视频中的来没有问题，开发者模式也能看到浏览器先GET后POST；但是你自己直接访问就会405
* 如果你改为@RequestMapping(path = {"/reg/"}, method = {RequestMethod.GET})，你可以直接访问，但是此时如果你走他视频中的步骤，先@RequestMapping(path={"/reglogin"},method = {RequestMethod.GET})再登陆和注册，就会浏览器报错：405，POST不被允许，因为你此时是不满足login.html中form表单的method="post"的。
* */