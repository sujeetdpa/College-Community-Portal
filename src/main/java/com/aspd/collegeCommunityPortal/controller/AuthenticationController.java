package com.aspd.collegeCommunityPortal.controller;

import com.aspd.collegeCommunityPortal.beans.request.AuthenticationRequest;
import com.aspd.collegeCommunityPortal.beans.request.RegisterRequest;
import com.aspd.collegeCommunityPortal.beans.response.AuthenticationResponse;
import com.aspd.collegeCommunityPortal.beans.response.SignUpResponse;
import com.aspd.collegeCommunityPortal.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/auth/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request){
        AuthenticationResponse response=authenticationService.login(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/auth/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody RegisterRequest request){
        SignUpResponse response=authenticationService.signUp(request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
