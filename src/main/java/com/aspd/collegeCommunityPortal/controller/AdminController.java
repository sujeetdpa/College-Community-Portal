package com.aspd.collegeCommunityPortal.controller;

import com.aspd.collegeCommunityPortal.beans.request.AddAdminRequest;
import com.aspd.collegeCommunityPortal.beans.request.PostCommentFetchRequest;
import com.aspd.collegeCommunityPortal.beans.request.PostRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserRequest;
import com.aspd.collegeCommunityPortal.beans.response.*;
import com.aspd.collegeCommunityPortal.model.Role;
import com.aspd.collegeCommunityPortal.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
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
    @PostMapping("/blockUser/{userId}")
    public ResponseEntity<Boolean> toggleAccountLock(@PathVariable("userId") Integer userId){
        Boolean responseView=adminService.toggleAccountLock(userId);
        return new ResponseEntity<>(responseView,HttpStatus.OK);
    }
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getRoles(){
        List<Role> roles=adminService.getRoles();
        return new ResponseEntity<>(roles,HttpStatus.OK);
    }
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard(){
        AdminDashboardResponse response=adminService.getDashboard();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PostMapping("/deletedPosts")
    public ResponseEntity<PostResponseViewList> getDeletedPost(@RequestBody PostRequest request){
        PostResponseViewList responseViewList=adminService.getDeletedPost(request);
        return new ResponseEntity<>(responseViewList,HttpStatus.OK);
    }
    @PostMapping("/deletedComments")
    public ResponseEntity<CommentResponseViewList> getDeletedComment(@RequestBody PostCommentFetchRequest request){
        CommentResponseViewList responseViewList=adminService.getDeletedComment(request);
        return new ResponseEntity<>(responseViewList,HttpStatus.OK);
    }

}
