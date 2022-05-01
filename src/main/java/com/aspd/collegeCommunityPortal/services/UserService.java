package com.aspd.collegeCommunityPortal.services;

import com.aspd.collegeCommunityPortal.beans.request.*;
import com.aspd.collegeCommunityPortal.beans.response.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends UserDetailsService {
    UserResponseView getUser(String universityId);

    ImageResponseList getUserImages(UserImageRequest request);

    DocumentResponseList getUserDocuments(UserDocumentRequest request);

    PostResponseViewList getUserPost(PostRequest postRequest);

    Integer updateProfileImage(MultipartFile profileImage);

    UserResponseView updateUser(Integer userId, UserUpdateRequest request);

    UserDashboardResponse getUserDashboard();

}
