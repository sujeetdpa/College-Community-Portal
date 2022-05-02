package com.aspd.collegeCommunityPortal.controller;

import com.aspd.collegeCommunityPortal.beans.request.*;
import com.aspd.collegeCommunityPortal.beans.response.*;
import com.aspd.collegeCommunityPortal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;


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
    public ResponseEntity<ImageResponseList> getUserImages(@RequestBody UserImageRequest request){
        ImageResponseList responseList=userService.getUserImages(request);
        return new ResponseEntity<>(responseList,HttpStatus.OK);
    }
    @PostMapping("/documents")
    public ResponseEntity<DocumentResponseList> getUserDocuments(@RequestBody UserDocumentRequest request){
        DocumentResponseList response=userService.getUserDocuments(request);
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
    public ResponseEntity<UserResponseView> updateUser(@PathVariable("userId") Integer userId,@RequestBody @Valid UserUpdateRequest request){
        UserResponseView responseView=userService.updateUser(userId,request);
        return new ResponseEntity<>(responseView,HttpStatus.OK);
    }

    @GetMapping("/data/dashboard")
    public ResponseEntity<UserDashboardResponse> getUserDashboard(){
        UserDashboardResponse response=userService.getUserDashboard();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
