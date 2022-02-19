package com.aspd.collegeCommunityPortal.resources;

import com.aspd.collegeCommunityPortal.data.request.NewPostRequest;
import com.aspd.collegeCommunityPortal.data.response.DeleteResponseView;
import com.aspd.collegeCommunityPortal.data.response.PostResponseView;
import com.aspd.collegeCommunityPortal.data.response.PostResponseViewList;
import com.aspd.collegeCommunityPortal.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {
    @Autowired
    PostService postService;

    @GetMapping("/all")
    public ResponseEntity<PostResponseViewList> getPosts(){
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
    @PostMapping("/new")
    public ResponseEntity<PostResponseView> createPost(@RequestBody NewPostRequest newPostRequest){
        return new ResponseEntity<>(null,HttpStatus.OK);
    }
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<DeleteResponseView> deletePost(@PathVariable("postId") int postId){
        return new ResponseEntity<>(null,HttpStatus.OK);
    }

}
