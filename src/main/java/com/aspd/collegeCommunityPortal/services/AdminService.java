package com.aspd.collegeCommunityPortal.services;

import com.aspd.collegeCommunityPortal.beans.request.AddAdminRequest;
import com.aspd.collegeCommunityPortal.beans.request.PostCommentFetchRequest;
import com.aspd.collegeCommunityPortal.beans.request.PostRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserRequest;
import com.aspd.collegeCommunityPortal.beans.response.*;
import com.aspd.collegeCommunityPortal.model.Role;

import java.util.List;

public interface AdminService {
    UserResponseViewList getAllUser(UserRequest request);

    UserResponseView addAdmin(AddAdminRequest request);

    Boolean toggleAccountLock(Integer userId);

    List<Role> getRoles();

    AdminDashboardResponse getDashboard();

    PostResponseViewList getDeletedPost(PostRequest request);

    CommentResponseViewList getDeletedComment(PostCommentFetchRequest request);

    UserResponseView toggleUserRole(Integer userId);

    DeleteResponseView deleteUser(Integer userId);
}
