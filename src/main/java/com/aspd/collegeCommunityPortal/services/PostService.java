package com.aspd.collegeCommunityPortal.services;

import com.aspd.collegeCommunityPortal.beans.request.CreatePostRequest;
import com.aspd.collegeCommunityPortal.beans.request.PostRequest;
import com.aspd.collegeCommunityPortal.beans.request.PostSearchRequest;
import com.aspd.collegeCommunityPortal.beans.response.DeleteResponseView;
import com.aspd.collegeCommunityPortal.beans.response.PostResponseView;
import com.aspd.collegeCommunityPortal.beans.response.PostResponseViewList;
import com.aspd.collegeCommunityPortal.beans.response.PostSearchResponseViewList;

public interface PostService {
    PostResponseViewList getAllPost(PostRequest postRequest);

    PostResponseView createPost(CreatePostRequest createPostRequest);

    PostSearchResponseViewList searchPost(PostSearchRequest request);

    DeleteResponseView deletePost(int postId);
}
