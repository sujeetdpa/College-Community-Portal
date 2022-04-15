package com.aspd.collegeCommunityPortal.services;

import com.aspd.collegeCommunityPortal.beans.request.PostRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserDocumentRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserImageRequest;
import com.aspd.collegeCommunityPortal.beans.response.PostResponseViewList;
import com.aspd.collegeCommunityPortal.beans.response.UserDocumentResponse;
import com.aspd.collegeCommunityPortal.beans.response.UserImageResponse;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseView;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

public interface UserService extends UserDetailsService {
    UserResponseView getUser();

    UserImageResponse getUserImages(UserImageRequest request);

    UserDocumentResponse getUserDocuments(UserDocumentRequest request);

    PostResponseViewList getUserPost(PostRequest postRequest);
}
