package com.aspd.collegeCommunityPortal.controller;

import com.aspd.collegeCommunityPortal.beans.request.AddUserRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserRequest;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseView;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseViewList;
import com.aspd.collegeCommunityPortal.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/users")
    public ResponseEntity<UserResponseViewList> getAllUser(@RequestBody UserRequest request){
        UserResponseViewList responseViewList=adminService.getAllUser(request);
        return new ResponseEntity<>(responseViewList, HttpStatus.OK);
    }
    @PostMapping("/add/user")
    public ResponseEntity<UserResponseView> addUser(@RequestBody AddUserRequest request){
        UserResponseView userResponseView=adminService.addUser(request);
        return new ResponseEntity<>(userResponseView,HttpStatus.OK);
    }

}
