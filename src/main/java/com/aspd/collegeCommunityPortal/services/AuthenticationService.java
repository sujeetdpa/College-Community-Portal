package com.aspd.collegeCommunityPortal.services;


import com.aspd.collegeCommunityPortal.beans.request.AuthenticationRequest;
import com.aspd.collegeCommunityPortal.beans.request.RegisterRequest;
import com.aspd.collegeCommunityPortal.beans.response.AuthenticationResponse;
import com.aspd.collegeCommunityPortal.beans.response.SignUpResponse;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest request);

    SignUpResponse signUp(RegisterRequest request);
}
