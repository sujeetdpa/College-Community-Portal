package com.aspd.collegeCommunityPortal.services;

import com.aspd.collegeCommunityPortal.data.request.PostRequest;
import com.aspd.collegeCommunityPortal.data.response.PostResponseViewList;
import org.springframework.stereotype.Service;

@Service
public interface PostService {
    PostResponseViewList getAllPost(PostRequest postRequest);
}
