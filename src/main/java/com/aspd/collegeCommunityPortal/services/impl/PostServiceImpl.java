package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.repositories.PostRepository;
import com.aspd.collegeCommunityPortal.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;

}
