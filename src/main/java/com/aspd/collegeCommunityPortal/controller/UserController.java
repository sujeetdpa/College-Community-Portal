package com.aspd.collegeCommunityPortal.controller;

import com.aspd.collegeCommunityPortal.config.BucketName;
import com.aspd.collegeCommunityPortal.config.JWTConfig;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {
    @GetMapping("/test")
    public String test(){
        return "hello";
    }
}
