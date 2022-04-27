package com.aspd.collegeCommunityPortal.services;


public interface EmailService {
    boolean sendForgotPasswordEmail(String firstName,String email,String password);
    void sendPasswordChangeEmail(String firstName,String email,String password);
    void sendActivateAccountEmail(String firstName,String email);
    void sendLockedAccountEmail(String firstName,String email);
    public void sendUnlockedAccountEmail(String firstName, String email);
    void emailTest(String firstName,String email);

    public void sendRegistrationEmail(String firstName,String email,String password);
}

