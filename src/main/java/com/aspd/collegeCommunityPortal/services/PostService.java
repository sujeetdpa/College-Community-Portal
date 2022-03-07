package com.aspd.collegeCommunityPortal.services;

import com.aspd.collegeCommunityPortal.beans.request.CommentRequest;
import com.aspd.collegeCommunityPortal.beans.request.CreatePostRequest;
import com.aspd.collegeCommunityPortal.beans.request.PostRequest;
import com.aspd.collegeCommunityPortal.beans.request.PostSearchRequest;
import com.aspd.collegeCommunityPortal.beans.response.*;

public interface PostService {
    PostResponseViewList getAllPost(PostRequest postRequest);

    PostResponseView createPost(CreatePostRequest createPostRequest);

    PostSearchResponseViewList searchPost(PostSearchRequest request);

    DeleteResponseView deletePost(int postId);

    PostResponseView getPost(int postId);

    CommentResponseView newComment(CommentRequest request);

    CommentResponseViewList getPostComments(int postId,int pageNo);
}
