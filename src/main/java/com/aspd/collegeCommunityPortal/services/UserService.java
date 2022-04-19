package com.aspd.collegeCommunityPortal.services;

import com.aspd.collegeCommunityPortal.beans.request.PostRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserDocumentRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserImageRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserUpdateRequest;
import com.aspd.collegeCommunityPortal.beans.response.PostResponseViewList;
import com.aspd.collegeCommunityPortal.beans.response.UserDocumentResponse;
import com.aspd.collegeCommunityPortal.beans.response.UserImageResponse;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseView;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService extends UserDetailsService {
    UserResponseView getUser(String universityId);

    UserImageResponse getUserImages(UserImageRequest request);

    UserDocumentResponse getUserDocuments(UserDocumentRequest request);

    PostResponseViewList getUserPost(PostRequest postRequest);

    Integer updateProfileImage(MultipartFile profileImage);

    UserResponseView updateUser(Integer userId, UserUpdateRequest request);
}
