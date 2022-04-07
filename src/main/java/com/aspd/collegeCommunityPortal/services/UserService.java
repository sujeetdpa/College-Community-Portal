package com.aspd.collegeCommunityPortal.services;

import com.aspd.collegeCommunityPortal.beans.response.UserResponseView;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

public interface UserService extends UserDetailsService {
    UserResponseView getUser();
}
