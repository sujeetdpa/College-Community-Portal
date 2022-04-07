package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.beans.response.UserResponseView;
import com.aspd.collegeCommunityPortal.model.User;
import com.aspd.collegeCommunityPortal.model.UserPrincipal;
import com.aspd.collegeCommunityPortal.repositories.RoleRepository;
import com.aspd.collegeCommunityPortal.repositories.UserRepository;
import com.aspd.collegeCommunityPortal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found with with username:" + username));
        user.setLastLoginTimestamp(user.getCurrentLoginTimeStamp());
        user.setCurrentLoginTimeStamp(LocalDateTime.now());
        userRepository.save(user);
        return new UserPrincipal(user);
    }

    @Override
    public UserResponseView getUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal!=null){
            UserResponseView view=new UserResponseView();
            User user=userPrincipal.getUser();
            Optional.ofNullable(user.getId()).ifPresent(view::setId);
            Optional.ofNullable(user.getFullName()).ifPresent(view::setFullName);
            Optional.ofNullable(user.getUsername()).ifPresent(view::setUsername);
            Optional.ofNullable(user.getDob()).ifPresent(view::setDob);
            Optional.ofNullable(user.getLastLoginTimestamp()).ifPresent(view::setLastLoginTimestamp);
            Optional.ofNullable(user.getMobileNo()).ifPresent(view::setMobileNo);
            Optional.ofNullable(user.getUniversityId()).ifPresent(view::setUniversityId);
            return view;
        }
        return null;
    }
}
