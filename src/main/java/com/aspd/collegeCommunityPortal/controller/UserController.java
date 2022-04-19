package com.aspd.collegeCommunityPortal.controller;

import com.aspd.collegeCommunityPortal.beans.request.PostRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserDocumentRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserImageRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserUpdateRequest;
import com.aspd.collegeCommunityPortal.beans.response.PostResponseViewList;
import com.aspd.collegeCommunityPortal.beans.response.UserDocumentResponse;
import com.aspd.collegeCommunityPortal.beans.response.UserImageResponse;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public String test(){
        return "hello";
    }

    @GetMapping("/{universityId}")
    public ResponseEntity<UserResponseView> getUser(@PathVariable("universityId") String universityId){
        UserResponseView responseView=userService.getUser(universityId);
        return new ResponseEntity<>(responseView, HttpStatus.OK);
    }

    @PostMapping("/images")
    public ResponseEntity<UserImageResponse> getUserImages(@RequestBody UserImageRequest request){
        UserImageResponse response=userService.getUserImages(request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PostMapping("/documents")
    public ResponseEntity<UserDocumentResponse> getUserDocuments(@RequestBody UserDocumentRequest request){
        UserDocumentResponse response=userService.getUserDocuments(request);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PostMapping("/post")
    public ResponseEntity<PostResponseViewList> getUserPost(@RequestBody PostRequest postRequest){
        PostResponseViewList responseViewList=userService.getUserPost(postRequest);
        return new ResponseEntity<>(responseViewList,HttpStatus.OK);
    }
    @PostMapping("/update/profileImage")
    public ResponseEntity<Integer> updateProfileImage(@RequestParam("profileImage") MultipartFile profileImage){
        Integer profileImageId=userService.updateProfileImage(profileImage);
        return new ResponseEntity<>(profileImageId,HttpStatus.OK);
    }

    @PostMapping("/update/{userId}")
    public ResponseEntity<UserResponseView> updateUser(@PathVariable("userId") Integer userId,@RequestBody UserUpdateRequest request){
        UserResponseView responseView=userService.updateUser(userId,request);
        return new ResponseEntity<>(responseView,HttpStatus.OK);
    }
}
