package com.aspd.collegeCommunityPortal.controller;

import com.aspd.collegeCommunityPortal.beans.request.AuthenticationRequest;
import com.aspd.collegeCommunityPortal.beans.request.ForgotPasswordRequest;
import com.aspd.collegeCommunityPortal.beans.request.RegisterRequest;
import com.aspd.collegeCommunityPortal.beans.request.UpdatePasswordRequest;
import com.aspd.collegeCommunityPortal.beans.response.AuthenticationResponse;
import com.aspd.collegeCommunityPortal.beans.response.SignUpResponse;
import com.aspd.collegeCommunityPortal.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request){
        AuthenticationResponse response=authenticationService.login(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody RegisterRequest request){
        SignUpResponse response=authenticationService.signUp(request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PostMapping("/update/password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequest request){
        String response=authenticationService.updatePassword(request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PostMapping("/forgot/password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request){
        String message=authenticationService.forgotPassword(request);
        return new ResponseEntity<>(message,HttpStatus.OK);
    }
    @GetMapping("/activate/account")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token){
        String message=authenticationService.activateAccount(token);
        return new ResponseEntity<>(message,HttpStatus.OK);
    }
}
