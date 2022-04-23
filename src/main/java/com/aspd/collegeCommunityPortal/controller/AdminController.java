package com.aspd.collegeCommunityPortal.controller;

import com.aspd.collegeCommunityPortal.beans.request.AddAdminRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserRequest;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseView;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseViewList;
import com.aspd.collegeCommunityPortal.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/add")
    public ResponseEntity<UserResponseView> addAdmin(@RequestBody AddAdminRequest request){
        UserResponseView userResponseView=adminService.addAdmin(request);
        return new ResponseEntity<>(userResponseView,HttpStatus.OK);
    }
    @PostMapping("/lockUser/{userId}")
    public ResponseEntity<UserResponseView> toggleAccountLock(@PathVariable("userId") Integer userId){
        UserResponseView responseView=adminService.toggleAccountLock(userId);
        return new ResponseEntity<>(responseView,HttpStatus.OK);
    }
}
