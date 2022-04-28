package com.aspd.collegeCommunityPortal.services;


import com.aspd.collegeCommunityPortal.model.Comment;
import com.aspd.collegeCommunityPortal.model.Post;

public interface EmailService {
    void sendForgotPasswordEmail(String firstName,String email,String password);
    void sendPasswordChangeEmail(String firstName,String email,String password);
    void sendActivationLinkEmail(String firstName, String email,String link);
    void sendLockedAccountEmail(String firstName,String email);
    void sendUnlockedAccountEmail(String firstName, String email);
    void sendRegistrationEmail(String firstName,String email,String password);
    void sendPostRemovedEmail(String firstName, String email, Post post);
    void sendCommentRemovedEmail(String firstName, String email, Comment comment);
}

