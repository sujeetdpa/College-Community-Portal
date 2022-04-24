package com.aspd.collegeCommunityPortal.services;

import com.aspd.collegeCommunityPortal.beans.request.AddAdminRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserRequest;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseView;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseViewList;
import com.aspd.collegeCommunityPortal.model.Role;

import java.util.List;

public interface AdminService {
    UserResponseViewList getAllUser(UserRequest request);

    UserResponseView addAdmin(AddAdminRequest request);

    Boolean toggleAccountLock(Integer userId);

    List<Role> getRoles();
}
