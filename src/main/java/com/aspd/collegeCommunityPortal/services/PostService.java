package com.aspd.collegeCommunityPortal.services;

import com.aspd.collegeCommunityPortal.beans.request.*;
import com.aspd.collegeCommunityPortal.beans.response.*;

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
}
