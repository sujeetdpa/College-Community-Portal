package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.model.Comment;
import com.aspd.collegeCommunityPortal.model.Post;
import com.aspd.collegeCommunityPortal.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EmailServiceImpl implements EmailService {

    public static final String FROM_EMAIL="sujeetdpa@gmail.com";
    public static final String SUBJECT_PREFIX="CCP Admin- ";

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    @Async
    public void sendForgotPasswordEmail(String firstName,String email,String password) {
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
    }

    @Override
    @Async
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
    @Async
    public void sendActivationLinkEmail(String firstName, String email,String link) {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM_EMAIL);
        simpleMailMessage.setSubject(SUBJECT_PREFIX+"Activate account");
        String body="Hello "+firstName+",\n"+"Your account has been successfully created on College Community Portal. Click on the below link to activate your account."+"\n"+
                "Username: "+email+"\n"+
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
    @Async
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
    @Override
    @Async
    public void sendUnlockedAccountEmail(String firstName, String email) {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM_EMAIL);
        simpleMailMessage.setSubject(SUBJECT_PREFIX+"Account Unlocked");
        String body="Hello "+firstName+",\n"+"Your account has been unblocked by the administrator. You can access your account using your previous credentials."+"\n"+
                "Note: Please avoid malpractices on the application otherwise your account will be blocked permanently."+"\n\n\n"+
                "Thanks & Regards"+"\n"+
                "Admin"+"\n"+
                "CollegeCommunityPortal";
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
    }
    @Override
    @Async
    public void sendRegistrationEmail(String firstName,String email,String password){
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM_EMAIL);
        simpleMailMessage.setSubject(SUBJECT_PREFIX+"Account Creation");
        String body="Congratulations "+firstName+",\n"+"Your account has been created successfully and your login credentials are :-"+"\n"+
                "Username: "+email+"\n"+
                "Password: "+password+"\n"+
                "Note: Make sure to activate your account via the activation link sent to you before login."+"\n\n\n"+
                "Thanks & Regards"+"\n"+
                "Admin"+"\n"+
                "College Community Portal";
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
    }
    @Override
    @Async
    public void sendPostRemovedEmail(String firstName, String email, Post post){
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM_EMAIL);
        simpleMailMessage.setSubject(SUBJECT_PREFIX+"Post removed");
        String body="Hey "+firstName+",\n"+"Your post with the following details have been removed by the administrator due to improper content."+"\n"+
                "Username: "+email+"\n"+
                "Post ID: "+post.getId()+"\n"+
                "Post Title: "+post.getTitle()+"\n"+
                "Post Creation Date and Time: "+post.getCreationDate().toLocalDate()+" AT "+post.getCreationDate().toLocalTime()+"\n"+
                "Note: If this is a fault please contact administrator"+"\n\n\n"+
                "Thanks & Regards"+"\n"+
                "Admin"+"\n"+
                "College Community Portal";
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
    }
    @Override
    @Async
    public void sendCommentRemovedEmail(String firstName, String email, Comment comment){
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM_EMAIL);
        simpleMailMessage.setSubject(SUBJECT_PREFIX+"Post removed");
        String body="Hey "+firstName+",\n"+"Your comment with the following details have been removed by the administrator due to improper content."+"\n"+
                "Username: "+email+"\n"+
                "Comment ID: "+comment.getId()+"\n"+
                "Comment Description: "+comment.getTitle()+"\n"+
                "Comment Creation Date and Time: "+comment.getCommentDate().toLocalDate()+" AT "+comment.getCommentDate().toLocalTime()+"\n"+
                "Note: If this is a fault please contact administrator"+"\n\n\n"+
                "Thanks & Regards"+"\n"+
                "Admin"+"\n"+
                "College Community Portal";
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
    }

    @Override
    @Async
    public void sendRoleChangeEmail(String firstName, String email, String roles) {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM_EMAIL);
        simpleMailMessage.setSubject(SUBJECT_PREFIX+"Role Changed");
        String body="Hello "+firstName+",\n"+"Your account role has been changed to following: "+"\n"+
                "Username:"+email+"\n"+
                "Roles: "+roles+"\n"+
                "Hence, your permissions to access the application has been also modified."+"\n\n\n"+
                "Thanks & Regards"+"\n"+
                "Admin"+"\n"+
                "CollegeCommunityPortal";
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
    }
}
