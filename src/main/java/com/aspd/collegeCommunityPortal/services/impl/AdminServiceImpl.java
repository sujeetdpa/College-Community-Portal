package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.beans.request.AddAdminRequest;
import com.aspd.collegeCommunityPortal.beans.request.PostCommentFetchRequest;
import com.aspd.collegeCommunityPortal.beans.request.PostRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserRequest;
import com.aspd.collegeCommunityPortal.beans.response.*;
import com.aspd.collegeCommunityPortal.model.*;
import com.aspd.collegeCommunityPortal.repositories.*;
import com.aspd.collegeCommunityPortal.services.AdminService;
import com.aspd.collegeCommunityPortal.services.EmailService;
import com.aspd.collegeCommunityPortal.util.TimeUtil;
import com.aspd.collegeCommunityPortal.util.UserUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private EmailService emailService;

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
        System.out.println(Optional.ofNullable(request.getRoles()).map(roleRepository::findAllById));
        user.setFirstName(Optional.ofNullable(request.getFirstName()).orElseThrow(() -> new IllegalStateException("First Name cannot be empty")));
        user.setLastName(Optional.ofNullable(request.getLastName()).orElseThrow(() -> new IllegalStateException("Last Name cannot be empty")));
        user.setUsername(Optional.ofNullable(request.getUsername()).orElseThrow(() -> new IllegalStateException("Username cannot be empty")));
        user.setRoles(Optional.ofNullable(request.getRoles()).map(roleRepository::findAllById).orElseThrow(() -> new IllegalStateException("Select at least one role")));
        Optional.ofNullable(request.getGender()).map(Gender::valueOf).ifPresent(user::setGender);
        String password = RandomStringUtils.random(10);
        user.setPassword(passwordEncoder.encode(password));
        user.setIsActive(true);
        user.setIsNotLocked(true);
        user.setUserCreationTimestamp(LocalDateTime.now());
        user.setUniversityId(userUtil.getUniversityId(request.getUsername()));
        userRepository.save(user);
        emailService.sendRegistrationEmail(user.getFirstName(),user.getUsername(),password);

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
            emailService.sendLockedAccountEmail(user.getFirstName(),user.getUsername());
        }
        else {
            user.setIsNotLocked(true);
            emailService.sendUnlockedAccountEmail(user.getFirstName(),user.getUsername());
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
        response.setNumberOfAdmins(userRepository.countByRole(roleRepository.findByName("ROLE_ADMIN").get()));
        response.setNumberOfComments(commentRepository.count());
        response.setNumberOfLikes(reviewRepository.countByReviewType(ReviewType.LIKE));
        response.setNumberOfDislikes(reviewRepository.countByReviewType(ReviewType.DISLIKE));
        response.setNumberOfImages(imageRepository.count());
        response.setNumberOfDocuments(documentRepository.count());
        return response;
    }

    @Override
    public PostResponseViewList getDeletedPost(PostRequest request) {
        Pageable pageable=PageRequest.of(Optional.ofNullable(request.getPageNo()).orElse(0),Optional.ofNullable(request.getMaxItem()).orElse(10), Sort.by(Sort.Direction.DESC,Optional.ofNullable(request.getSortBy()).orElse("creationDate")));
        Page<Post> postPage=null;
        postPage=postRepository.findAllDeletedPost(pageable);
        if (postPage.isEmpty()){
            throw new IllegalStateException("No deleted post found");
        }
        PostResponseViewList postResponseViewList=new PostResponseViewList();
        List<PostResponseView> postResponseViews=new ArrayList<>();
        for(Post post:postPage){
            PostResponseView postResponseView=new PostResponseView();
            postResponseView.setId(post.getId());
            postResponseView.setTitle(post.getTitle());
            postResponseView.setCreationDate(timeUtil.getCreationTimestamp(post.getCreationDate()));
            postResponseView.setDescription(post.getDescription());
            Optional.ofNullable(post.getUser().getFullName()).ifPresent(postResponseView::setFullName);
            Optional.ofNullable(post.getUser().getId()).ifPresent(postResponseView::setUserId);
            Optional.ofNullable(post.getUser().getUniversityId()).ifPresent(postResponseView::setUniversityId);
            Optional.ofNullable(post.getUser().getProfileImageId()).ifPresent(postResponseView::setProfileImageId);
            Optional.ofNullable(reviewRepository.getPostReviewCount(post,ReviewType.LIKE)).ifPresent(postResponseView::setNoOfLikes);
            Optional.ofNullable(commentRepository.getPostCommentCount(post)).ifPresent(postResponseView::setNoOfComments);
            Optional.ofNullable(imageRepository.findImageByPost(post)).map(images -> images.stream().map(Image::getId).collect(Collectors.toList())).ifPresent(postResponseView::setImageIds);
            Optional.ofNullable(documentRepository.findByPost(post)).map(documents -> documents.stream().map(Document::getId).collect(Collectors.toList())).ifPresent(postResponseView::setDocumentIds);
            postResponseViews.add(postResponseView);
        }
        postResponseViewList.setPostResponseViews(postResponseViews);
        postResponseViewList.setTotalNumberOfItems(postPage.getTotalElements());
        postResponseViewList.setPageNo(postPage.getNumber());
        postResponseViewList.setTotalPages(postPage.getTotalPages());
        postResponseViewList.setMaxItems(postPage.getSize());
        return postResponseViewList;
    }

    @Override
    public CommentResponseViewList getDeletedComment(PostCommentFetchRequest request) {
        Pageable pageable=PageRequest.of(Optional.ofNullable(request.getPageNo()).orElse(0),Optional.ofNullable(request.getMaxItem()).orElse(10),Sort.by(Sort.Direction.DESC,"commentDate"));
        Page<Comment> commentPage=commentRepository.findAllDeletedComment(pageable);
        if (commentPage.isEmpty()){
            throw new IllegalStateException("No Deleted Comment found");
        }
        List<CommentResponseView> viewList=new ArrayList<>();
        for (Comment comment:commentPage){
            CommentResponseView view=new CommentResponseView();
            Optional.ofNullable(comment.getTitle()).ifPresent(view::setTitle);
            Optional.ofNullable(comment.getDescription()).ifPresent(view::setDescription);
            Optional.ofNullable(comment.getId()).ifPresent(view::setId);
            Optional.ofNullable(timeUtil.getCreationTimestamp(comment.getCommentDate())).ifPresent(view::setCommentDate);
            Optional.ofNullable(comment.getPost().getId()).ifPresent(view::setPostId);
            Optional.ofNullable(comment.getUser().getId()).ifPresent(view::setUserId);
            Optional.ofNullable(comment.getUser().getUniversityId()).ifPresent(view::setUniversityId);
            Optional.ofNullable(comment.getUser().getProfileImageId()).ifPresent(view::setProfileImageId);
            Optional.ofNullable(comment.getUser().getFullName()).ifPresent(view::setFullName);
            viewList.add(view);
        }
        CommentResponseViewList responseViewList=new CommentResponseViewList();
        Optional.ofNullable(commentPage.getTotalPages()).ifPresent(responseViewList::setTotalPages);
        Optional.ofNullable(commentPage.getTotalElements()).ifPresent(responseViewList::setTotalNumberOfItems);
        Optional.ofNullable(commentPage.getNumber()).ifPresent(responseViewList::setPageNo);
        Optional.ofNullable(commentPage.getSize()).ifPresent(responseViewList::setMaxItems);
        Optional.ofNullable(viewList).ifPresent(responseViewList::setCommentResponseViews);
        return responseViewList;
    }


}
