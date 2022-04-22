package com.aspd.collegeCommunityPortal.services;


public interface EmailService {
    boolean sendForgotPasswordEmail(String firstName,String email,String password);
    void sendPasswordChangeEmail(String firstName,String email,String password);
    void sendActivateAccountEmail(String firstName,String email);
    void sendBlockedAccountEmail(String firstName,String email);
}

