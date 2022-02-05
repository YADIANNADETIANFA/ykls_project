package com.kai.webby.util;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
//import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

@Service
public class MailSender implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;

/*    @Autowired
    private VelocityEngine velocityEngine;*/

    // Todo:现在都是用freemark，velocity太老了，后续再做这一块功能
    public boolean sendWithHTMLTemplate(String to, String subject, String template, Map<String,Object> model){
/*        try{
            String nick = MimeUtility.encodeText("ykls系统邮件"); //不写catch的时候，这里会提示报错？？
            InternetAddress from = new InternetAddress(nick + "<871***69@qq.com>");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            String result = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,template,"UTF-8",model);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(result,true);
            mailSender.send(mimeMessage);
            return true;
        } catch(Exception e){
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }*/
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception{
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("871***69@qq.com");
        mailSender.setPassword("xwuwtsiwkptvbbgg");//目前使用POP3/SMTP协议 qq邮箱提供的授权码
        mailSender.setHost("smtp.qq.com");
        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf-8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable",true);
        mailSender.setJavaMailProperties(javaMailProperties);
    }

}
