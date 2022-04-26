package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.beans.request.AddAdminRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserRequest;
import com.aspd.collegeCommunityPortal.beans.response.AdminDashboardResponse;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseView;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseViewList;
import com.aspd.collegeCommunityPortal.model.ReviewType;
import com.aspd.collegeCommunityPortal.model.Role;
import com.aspd.collegeCommunityPortal.model.User;
import com.aspd.collegeCommunityPortal.repositories.*;
import com.aspd.collegeCommunityPortal.services.AdminService;
import com.aspd.collegeCommunityPortal.util.TimeUtil;
import com.aspd.collegeCommunityPortal.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TimeUtil timeUtil;
    @Autowired
    private UserUtil userUtil;
    @Override
    public UserResponseViewList getAllUser(UserRequest request) {
        Pageable pageable= PageRequest.of(Optional.ofNullable(request.getPageNo()).orElse(0),Optional.ofNullable(request.getMaxItem()).orElse(10));
        Page<User> userPage=userRepository.findAll(pageable);
        if (userPage.isEmpty() || userPage.getContent().isEmpty()){
            throw new IllegalStateException("Users not found");
        }
        List<UserResponseView> userResponseViews=new ArrayList<>();
        userPage.forEach(user -> {
            UserResponseView view=new UserResponseView();
            Optional.ofNullable(user.getId()).ifPresent(view::setId);
            Optional.ofNullable(user.getFirstName()).ifPresent(view::setFirstName);
            Optional.ofNullable(user.getLastName()).ifPresent(view::setLastName);
            Optional.ofNullable(user.getFullName()).ifPresent(view::setFullName);
            Optional.ofNullable(user.getUsername()).ifPresent(view::setUsername);
            Optional.ofNullable(user.getDob()).ifPresent(view::setDob);
            Optional.ofNullable(user.getLastLoginTimestamp()).map(timeUtil::getLastLoginTimestamp).ifPresent(view::setLastLoginTimestamp);
            Optional.ofNullable(user.getMobileNo()).ifPresent(view::setMobileNo);
            Optional.ofNullable(user.getUniversityId()).ifPresent(view::setUniversityId);
            Optional.ofNullable(user.getGender().toString()).ifPresent(view::setGender);
            Optional.ofNullable(user.getUserCreationTimestamp()).map(timeUtil::getUserJoinDate).ifPresent(view::setUserCreationTimestamp);
            Optional.ofNullable(user.getProfileImageId()).ifPresent(view::setProfileImageId);
            Optional.ofNullable(user.getRoles().stream().map(Role::getName).collect(Collectors.toList())).ifPresent(view::setRole);
            Optional.ofNullable(user.getIsActive()).ifPresent(view::setIsActive);
            Optional.ofNullable(user.getIsNotLocked()).ifPresent(view::setIsNotLocked);
            Optional.ofNullable(user.getEmail()).ifPresent(view::setEmail);
            userResponseViews.add(view);
        });
        UserResponseViewList viewList=new UserResponseViewList();
        Optional.ofNullable(userResponseViews).ifPresent(viewList::setUserResponseViews);
        Optional.ofNullable(userPage.getTotalPages()).ifPresent(viewList::setTotalPages);
        Optional.ofNullable(userPage.getNumberOfElements()).ifPresent(viewList::setTotalNumberOfItems);
        Optional.ofNullable(userPage.getNumber()).ifPresent(viewList::setPageNo);
        Optional.ofNullable(userPage.getSize()).ifPresent(viewList::setMaxItems);
        return viewList;
    }

    @Override
    public UserResponseView addAdmin(AddAdminRequest request) {
        User user=new User();
        if (!userUtil.validateUsername(request.getUsername())){
            throw new IllegalStateException("Invalid username");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new IllegalStateException("Username already taken");
        }
        user.setFirstName(Optional.ofNullable(request.getFirstName()).orElseThrow(() -> new IllegalStateException("First Name cannot be empty")));
        user.setLastName(Optional.ofNullable(request.getLastName()).orElseThrow(() -> new IllegalStateException("Last Name cannot be empty")));
        user.setUsername(Optional.ofNullable(request.getUsername()).orElseThrow(() -> new IllegalStateException("Username cannot be empty")));
        user.setRoles(Optional.ofNullable(request.getRoles()).map(roleRepository::findAllById).orElseThrow(() -> new IllegalStateException("Select at least one role")));
        UUID password = UUID.randomUUID();
        user.setPassword(passwordEncoder.encode(password.toString()));
        user.setIsActive(true);
        user.setIsNotLocked(true);
        user.setUserCreationTimestamp(LocalDateTime.now());
        user.setUniversityId(userUtil.getUniversityId(request.getUsername()));
        // Send email to the username
        userRepository.save(user);
        UserResponseView view=new UserResponseView();
        Optional.ofNullable(user.getId()).ifPresent(view::setId);
        Optional.ofNullable(user.getFirstName()).ifPresent(view::setFirstName);
        Optional.ofNullable(user.getLastName()).ifPresent(view::setLastName);
        Optional.ofNullable(user.getUsername()).ifPresent(view::setUsername);
        return view;
    }

    @Override
    public Boolean toggleAccountLock(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new IllegalStateException("User not found");
        }
        User user=optionalUser.get();
        if (user.getIsNotLocked()) {
            user.setIsNotLocked(false);
            //TODO send mail for account locked
        }
        else {
            user.setIsNotLocked(true);
            //TODO send mail for account unlocked
        }
        user=userRepository.save(user);
//        UserResponseView view=new UserResponseView();
//        Optional.ofNullable(user.getId()).ifPresent(view::setId);
//        Optional.ofNullable(user.getFirstName()).ifPresent(view::setFirstName);
//        Optional.ofNullable(user.getLastName()).ifPresent(view::setLastName);
//        Optional.ofNullable(user.getFullName()).ifPresent(view::setFullName);
//        Optional.ofNullable(user.getUsername()).ifPresent(view::setUsername);
//        Optional.ofNullable(user.getDob()).ifPresent(view::setDob);
//        Optional.ofNullable(user.getLastLoginTimestamp()).map(timeUtil::getLastLoginTimestamp).ifPresent(view::setLastLoginTimestamp);
//        Optional.ofNullable(user.getMobileNo()).ifPresent(view::setMobileNo);
//        Optional.ofNullable(user.getUniversityId()).ifPresent(view::setUniversityId);
//        Optional.ofNullable(user.getGender().toString()).ifPresent(view::setGender);
//        Optional.ofNullable(user.getUserCreationTimestamp()).map(timeUtil::getUserJoinDate).ifPresent(view::setUserCreationTimestamp);
//        Optional.ofNullable(user.getProfileImageId()).ifPresent(view::setProfileImageId);
//        Optional.ofNullable(user.getRoles().stream().map(Role::getName).collect(Collectors.toList())).ifPresent(view::setRole);
//        Optional.ofNullable(user.getIsActive()).ifPresent(view::setIsActive);
//        Optional.ofNullable(user.getIsNotLocked()).ifPresent(view::setIsNotLocked);
//        Optional.ofNullable(user.getEmail()).ifPresent(view::setEmail);
        return user.getIsNotLocked();
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public AdminDashboardResponse getDashboard() {
        AdminDashboardResponse response=new AdminDashboardResponse();
        response.setNumberOfPosts(postRepository.count());
        response.setNumberOfDeletedPost(postRepository.countDeletedPost());
        response.setNumberOfDeletedComment(commentRepository.countDeletedComment());
        response.setNumberOfUsers(userRepository.count());
        response.setNumberOfAdmins(userRepository.countByRole(roleRepository.findByName("ADMIN_ROLE").get()));
        response.setNumberOfComments(commentRepository.count());
        response.setNumberOfLikes(reviewRepository.countByReviewType(ReviewType.LIKE));
        response.setNumberOfDislikes(reviewRepository.countByReviewType(ReviewType.DISLIKE));
        response.setNumberOfImages(imageRepository.count());
        response.setNumberOfDocuments(documentRepository.count());
        return response;
    }

}
