package com.ego.mail;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class EgoMailSender {

    //发送方邮箱账号
    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    public void send(String to,String orderId){
        MimeMessage message =  javaMailSender.createMimeMessage();
        //工具类 让MimeMessage使用更方便
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,false,"utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("易购");  //主题
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate("egoMail.ftl");
            Map<String,Object> map = new HashMap<>();
            map.put("orderId",orderId);
            String page = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            helper.setText(page,true); //内容
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }
}
