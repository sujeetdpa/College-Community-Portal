package com.aspd.collegeCommunityPortal.services;


import com.aspd.collegeCommunityPortal.beans.request.AuthenticationRequest;
import com.aspd.collegeCommunityPortal.beans.request.ForgotPasswordRequest;
import com.aspd.collegeCommunityPortal.beans.request.RegisterRequest;
import com.aspd.collegeCommunityPortal.beans.request.UpdatePasswordRequest;
import com.aspd.collegeCommunityPortal.beans.response.AuthenticationResponse;
import com.aspd.collegeCommunityPortal.beans.response.SignUpResponse;

import java.net.UnknownHostException;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest request);

    SignUpResponse signUp(RegisterRequest request);

    String updatePassword(UpdatePasswordRequest request);

    String forgotPassword(ForgotPasswordRequest request);

    String activateAccount(String token) throws UnknownHostException;
}
