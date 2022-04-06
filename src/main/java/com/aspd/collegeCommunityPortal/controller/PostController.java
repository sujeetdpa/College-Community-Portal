package com.aspd.collegeCommunityPortal.controller;

import com.aspd.collegeCommunityPortal.beans.request.*;
import com.aspd.collegeCommunityPortal.beans.response.*;
import com.aspd.collegeCommunityPortal.services.LocalStorageService;
import com.aspd.collegeCommunityPortal.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {
    @Autowired
    PostService postService;
    @Autowired
    LocalStorageService localStorageService;

    @GetMapping("/all")
    public ResponseEntity<PostResponseViewList> getAllPost(@RequestBody PostRequest postRequest){
        PostResponseViewList postResponseViewList=postService.getAllPost(postRequest);
        return new ResponseEntity<>(postResponseViewList, HttpStatus.OK);
    }
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseView> getPost(@PathVariable("postId") int postId){
        PostResponseView responseView=postService.getPost(postId);
        return new ResponseEntity<>(responseView,HttpStatus.OK);
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
    public ResponseEntity<PostSearchResponseViewList> searchPost(@RequestBody PostSearchRequest request){
        PostSearchResponseViewList postSearchResponseViewList=postService.searchPost(request);
        return new ResponseEntity<>(postSearchResponseViewList,HttpStatus.OK);
    }

    @PostMapping("/comment/new")
    public ResponseEntity<CommentResponseView> newComment(@RequestBody CommentRequest request){
        CommentResponseView responseView=postService.newComment(request);
        return new ResponseEntity<>(responseView,HttpStatus.OK);
    }
    @GetMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseViewList> getPostComments(@PathVariable("postId") int postId, @RequestBody PostCommentFetchRequest request){
        CommentResponseViewList viewList=postService.getPostComments(postId,request);
        return new ResponseEntity<>(viewList,HttpStatus.OK);
    }
    @PostMapping("/{postId}/like/{userId}")
    public ResponseEntity<LikePostResponse> likePost(@PathVariable("postId") int postId,@PathVariable("userId") int userId){
        LikePostResponse response=postService.likePost(postId,userId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PostMapping("/{postId}/dislike/{userId}")
    public ResponseEntity<DislikePostResponse> dislikePost(@PathVariable("postId") int postId,@PathVariable("userId") int userId){
        DislikePostResponse response=postService.dislikePost(postId,userId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/local/storage/upload/image")
    public ResponseEntity<List<Integer>> uploadImage(@RequestParam("images") List<MultipartFile> files) throws IOException {
        List<Integer> imageIds = postService.uploadImages(files);
        return new ResponseEntity<>(imageIds,HttpStatus.OK);
    }
    @PostMapping("/local/storage/upload/document")
    public ResponseEntity<List<Integer>> uploadDoc(@RequestParam("documents") List<MultipartFile> files) throws IOException {
        List<Integer> documentIds = postService.uploadDocuments(files);
        return new ResponseEntity<>(documentIds,HttpStatus.OK);
    }

    @GetMapping(value = "/local/storage/download/image/{imageId}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable("imageId") Integer imageId) throws IOException {
        return new ResponseEntity<>(postService.downloadImage(imageId),HttpStatus.OK);
    }
    @GetMapping(value = "/local/storage/download/document/{documentId}",produces = "application/pdf")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable("documentId") Integer documentId) throws IOException {
        return new ResponseEntity<>(postService.downloadDocument(documentId),HttpStatus.OK);
    }
}
