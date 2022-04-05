package com.aspd.collegeCommunityPortal.services;

import com.aspd.collegeCommunityPortal.beans.request.*;
import com.aspd.collegeCommunityPortal.beans.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    PostResponseViewList getAllPost(PostRequest postRequest);

    PostResponseView createPost(CreatePostRequest createPostRequest);

    PostSearchResponseViewList searchPost(PostSearchRequest request);

    DeleteResponseView deletePost(int postId);

    PostResponseView getPost(int postId);

    CommentResponseView newComment(CommentRequest request);

    CommentResponseViewList getPostComments(int postId, PostCommentFetchRequest request);

    LikePostResponse likePost(int postId, int userId);

    DislikePostResponse dislikePost(int postId, int userId);

    List<Integer> uploadDocuments(List<MultipartFile> files);

    List<Integer> uploadImages(List<MultipartFile> files);

    byte[] downloadImage(Integer imageId) throws IOException;

    byte[] downloadDocument(Integer documentId) throws IOException;
}
