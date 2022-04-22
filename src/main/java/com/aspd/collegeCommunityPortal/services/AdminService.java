package com.aspd.collegeCommunityPortal.services;

import com.aspd.collegeCommunityPortal.beans.request.AddUserRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserRequest;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseView;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseViewList;

public interface AdminService {
    UserResponseViewList getAllUser(UserRequest request);

    UserResponseView addUser(AddUserRequest request);
}
