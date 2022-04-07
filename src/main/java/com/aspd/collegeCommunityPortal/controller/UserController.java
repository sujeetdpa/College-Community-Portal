package com.aspd.collegeCommunityPortal.controller;

import com.aspd.collegeCommunityPortal.beans.response.UserResponseView;
import com.aspd.collegeCommunityPortal.config.BucketName;
import com.aspd.collegeCommunityPortal.config.JWTConfig;
import com.aspd.collegeCommunityPortal.model.User;
import com.aspd.collegeCommunityPortal.model.UserPrincipal;
import com.aspd.collegeCommunityPortal.services.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public String test(){
        return "hello";
    }

    @GetMapping
    public ResponseEntity<UserResponseView> getUser(){
        UserResponseView responseView=userService.getUser();
        return new ResponseEntity<>(responseView, HttpStatus.OK);
    }
}
