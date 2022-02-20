package com.aspd.collegeCommunityPortal.resources;

import com.aspd.collegeCommunityPortal.data.request.CreatePostRequest;
import com.aspd.collegeCommunityPortal.data.request.PostRequest;
import com.aspd.collegeCommunityPortal.data.response.DeleteResponseView;
import com.aspd.collegeCommunityPortal.data.response.PostResponseView;
import com.aspd.collegeCommunityPortal.data.response.PostResponseViewList;
import com.aspd.collegeCommunityPortal.data.response.PostSearchResponseViewList;
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
    public ResponseEntity<PostResponseViewList> getAllPost(@RequestBody PostRequest postRequest){
        PostResponseViewList postResponseViewList=postService.getAllPost(postRequest);
        return new ResponseEntity<>(postResponseViewList, HttpStatus.OK);
    }
    @PostMapping("/new")
    public ResponseEntity<PostResponseView> createPost(@RequestBody CreatePostRequest createPostRequest){
        PostResponseView postResponseView=postService.createPost(createPostRequest);
        return new ResponseEntity<>(postResponseView,HttpStatus.OK);
    }
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<DeleteResponseView> deletePost(@PathVariable("postId") int postId){
        DeleteResponseView deleteResponseView=postService.deletePost(postId);
        return new ResponseEntity<>(null,HttpStatus.OK);
    }
    @GetMapping("/search")
    public ResponseEntity<PostSearchResponseViewList> searchPost(@RequestParam("title") String title){
        PostSearchResponseViewList postSearchResponseViewList=postService.searchPost(title);
        return new ResponseEntity<>(postSearchResponseViewList,HttpStatus.OK);
    }

}
