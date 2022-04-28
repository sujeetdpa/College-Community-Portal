package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.model.Comment;
import com.aspd.collegeCommunityPortal.model.Post;
import com.aspd.collegeCommunityPortal.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EmailServiceImpl implements EmailService {

    public static final String FROM_EMAIL="sujeetdpa@gmail.com";
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
    @Override
    public void sendUnlockedAccountEmail(String firstName, String email) {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM_EMAIL);
        simpleMailMessage.setSubject(SUBJECT_PREFIX+"Account Unlocked");
        String body="Hello "+firstName+",\n"+"Your account has been unblocked by the administrator. You can access your account using your previous credentials."+"\n"+
                "<b>Note: </b> Please avoid malpractices on the application otherwise your account will be blocked permanently."+"\n\n\n"+
                "Thanks & Regards"+"\n"+
                "Admin"+"\n"+
                "CollegeCommunityPortal";
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
    }
    @Override
    public void sendRegistrationEmail(String firstName,String email,String password){
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM_EMAIL);
        String link="";
        simpleMailMessage.setSubject(SUBJECT_PREFIX+"Account Creation");
        String body="Congratulations "+firstName+",\n"+"Your account has been created successfully and your login credentials are :-"+"\n"+
                "Username: "+email+
                "Password: "+password+
                "<b>Note: </b> Please update your profile details in account section"+"\n\n\n"+
                "Thanks & Regards"+"\n"+
                "Admin"+"\n"+
                "College Community Portal";
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
    }
    public void sendPostRemovedEmail(String firstName, String email, Post post){
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM_EMAIL);
        String link="";
        simpleMailMessage.setSubject(SUBJECT_PREFIX+"Post removed");
        String body="Hey "+firstName+",\n"+"Your post with the following details have been removed by the administrator due to improper content."+"\n"+
                "Username: "+email+
                "Post ID: "+post.getId()+
                "Post Title: "+post.getTitle()+"\n"+
                "Post Creation Date and Time: "+post.getCreationDate().toLocalDate()+" AT "+post.getCreationDate().toLocalTime()+"\n"+
                "<b>Note: </b> If this is a fault please contact administrator"+"\n\n\n"+
                "Thanks & Regards"+"\n"+
                "Admin"+"\n"+
                "College Community Portal";
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
    }
    public void sendCommentRemovedEmail(String firstName, String email, Comment comment){
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM_EMAIL);
        String link="";
        simpleMailMessage.setSubject(SUBJECT_PREFIX+"Post removed");
        String body="Hey "+firstName+",\n"+"Your comment with the following details have been removed by the administrator due to improper content."+"\n"+
                "Username: "+email+
                "Comment ID: "+comment.getId()+
                "Comment Description: "+comment.getTitle()+"\n"+
                "Comment Creation Date and Time: "+comment.getCommentDate().toLocalDate()+" AT "+comment.getCommentDate().toLocalTime()+"\n"+
                "<b>Note: </b> If this is a fault please contact administrator"+"\n\n\n"+
                "Thanks & Regards"+"\n"+
                "Admin"+"\n"+
                "College Community Portal";
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public void emailTest(String firstName,String email) {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom(FROM_EMAIL);
        simpleMailMessage.setSubject(SUBJECT_PREFIX+"Test email");
        String body="Hello "+firstName+",\n"+"This is a test email";
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSentDate(new Date());
        javaMailSender.send(simpleMailMessage);
    }


}
