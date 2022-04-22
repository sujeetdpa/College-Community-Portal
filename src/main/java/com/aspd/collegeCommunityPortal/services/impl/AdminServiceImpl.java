package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.beans.request.AddUserRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserRequest;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseView;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseViewList;
import com.aspd.collegeCommunityPortal.services.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Override
    public UserResponseViewList getAllUser(UserRequest request) {
        return null;
    }

    @Override
    public UserResponseView addUser(AddUserRequest request) {
        return null;
    }
}
