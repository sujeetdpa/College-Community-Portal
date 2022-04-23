package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EmailServiceImpl implements EmailService {

    public static final String FROM_EMAIL="admin@ccp.com";
    public static final String SUBJECT_PREFIX="CCP Admin- ";

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public boolean sendForgotPasswordEmail(String firstName,String email,String password) {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();

        simpleMailMessage.setFrom(FROM_EMAIL);
        simpleMailMessage.setSubject(SUBJECT_PREFIX+"New Password");
        String body="Hello "+firstName+",\n"+"Your new login credentials for College Community Portal is :-"+"\n"+
                "Username: "+email+"\n"+
                "Password: "+password+"\n\n\n"+
                "Thanks & Regards"+"\n"+
                "Admin"+"\n"+
                "CollegeCommunityPortal";
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
        return true;
    }

    @Override
    public void sendPasswordChangeEmail(String firstName, String email, String password) {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM_EMAIL);
        simpleMailMessage.setSubject(SUBJECT_PREFIX+"Password change");
        String body="Hello "+firstName+",\n"+"Your password have been changed. Your new login credentials for College Community Portal is :-"+"\n"+
                "Username: "+email+"\n"+
                "Password: "+password+"\n\n\n"+
                "Thanks & Regards"+"\n"+
                "Admin"+"\n"+
                "CollegeCommunityPortal";
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public void sendActivateAccountEmail(String firstName,String email) {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM_EMAIL);
        String link="";
        simpleMailMessage.setSubject(SUBJECT_PREFIX+"Activate account");
        String body="Congratulations "+firstName+",\n"+"Your account successfully created on College Community Portal. Click on the below link to activate your account."+"\n"+
                "Link: "+link+"\n\n\n"+
                "Thanks & Regards"+"\n"+
                "Admin"+"\n"+
                "CollegeCommunityPortal";
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public void sendLockedAccountEmail(String firstName, String email) {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM_EMAIL);
        simpleMailMessage.setSubject(SUBJECT_PREFIX+"Account blocked");
        String body="Hello "+firstName+",\n"+"Your account has been blocked due to unusual/improper activity on the application."+"\n"+
                "If this is a fault please contact administrator to unblock your account"+"\n\n\n"+
                "Thanks & Regards"+"\n"+
                "Admin"+"\n"+
                "CollegeCommunityPortal";
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
    }

}
