package com.aspd.collegeCommunityPortal.services;

import com.aspd.collegeCommunityPortal.data.request.CreatePostRequest;
import com.aspd.collegeCommunityPortal.data.request.PostRequest;
import com.aspd.collegeCommunityPortal.data.response.DeleteResponseView;
import com.aspd.collegeCommunityPortal.data.response.PostResponseView;
import com.aspd.collegeCommunityPortal.data.response.PostResponseViewList;
import com.aspd.collegeCommunityPortal.data.response.PostSearchResponseViewList;
import org.springframework.stereotype.Service;

@Service
public interface PostService {
    PostResponseViewList getAllPost(PostRequest postRequest);

    PostResponseView createPost(CreatePostRequest createPostRequest);

    PostSearchResponseViewList searchPost(String title);

    DeleteResponseView deletePost(int postId);
}
