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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Value("${server.domain}")
    private String serverDomain;

    @LocalServerPort
    private int port;

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
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TimeUtil timeUtil;
    @Autowired
    private UserUtil userUtil;

    @Override
    public UserResponseViewList getAllUser(UserRequest request) {
        Pageable pageable = PageRequest.of(Optional.ofNullable(request.getPageNo()).orElse(0), Optional.ofNullable(request.getMaxItem()).orElse(10));
        Page<User> userPage = userRepository.findAll(pageable);
        if (userPage.isEmpty() || userPage.getContent().isEmpty()) {
            throw new IllegalStateException("Users not found");
        }
        List<UserResponseView> userResponseViews = new ArrayList<>();
        userPage.forEach(user -> {
            UserResponseView view = new UserResponseView();
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
        UserResponseViewList viewList = new UserResponseViewList();
        Optional.ofNullable(userResponseViews).ifPresent(viewList::setUserResponseViews);
        Optional.ofNullable(userPage.getTotalPages()).ifPresent(viewList::setTotalPages);
        Optional.ofNullable(userPage.getNumberOfElements()).ifPresent(viewList::setTotalNumberOfItems);
        Optional.ofNullable(userPage.getNumber()).ifPresent(viewList::setPageNo);
        Optional.ofNullable(userPage.getSize()).ifPresent(viewList::setMaxItems);
        return viewList;
    }

    @Override
    public UserResponseView addAdmin(AddAdminRequest request) {
        User user = new User();
        if (!userUtil.validateUsername(request.getUsername())) {
            throw new IllegalStateException("Invalid username");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already taken");
        }
        System.out.println(Optional.ofNullable(request.getRoles()).map(roleRepository::findAllById));
        user.setFirstName(request.getFirstName());
        user.setDob(request.getDob());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setUniversityId(userUtil.getUniversityId(request.getUsername()));
        user.setRoles(Optional.ofNullable(request.getRoles()).map(roleRepository::findAllById).orElseThrow(() -> new IllegalStateException("Select at least one role")));
        Optional.ofNullable(request.getGender()).map(Gender::valueOf).ifPresent(user::setGender);
        String password = RandomStringUtils.randomAlphanumeric(10);
        user.setPassword(passwordEncoder.encode(password));
        user.setIsActive(false);
        user.setIsNotLocked(true);
        user.setUserCreationTimestamp(LocalDateTime.now());
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new IllegalStateException("Failed to register please try again");
        }

        User savedUser = userRepository.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setCreatedAt(LocalDateTime.now());
        String token = UUID.randomUUID().toString();
        confirmationToken.setToken(token);
        confirmationToken.setExpiresAt(LocalDateTime.now().plusDays(1));
        confirmationToken.setUser(savedUser);
        confirmationTokenRepository.save(confirmationToken);

        String link = "http://" + ip + ":" + port + "/auth/activate/account?token=" + token;
        emailService.sendActivationLinkEmail(savedUser.getFullName(), savedUser.getUsername(), link,timeUtil.getLastLoginTimestamp(confirmationToken.getExpiresAt()));
        emailService.sendRegistrationEmail(savedUser.getFirstName(), savedUser.getUsername(), password);

        UserResponseView view = new UserResponseView();
        Optional.ofNullable(user.getId()).ifPresent(view::setId);
        Optional.ofNullable(user.getFirstName()).ifPresent(view::setFirstName);
        Optional.ofNullable(user.getLastName()).ifPresent(view::setLastName);
        Optional.ofNullable(user.getUsername()).ifPresent(view::setUsername);
        return view;
    }

    @Override
    public UserResponseView toggleAccountLock(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        User user = optionalUser.get();
        if (user.getIsNotLocked()) {
            user.setIsNotLocked(false);
            emailService.sendLockedAccountEmail(user.getFirstName(), user.getUsername());
        } else {
            user.setIsNotLocked(true);
            emailService.sendUnlockedAccountEmail(user.getFirstName(), user.getUsername());
        }
        user = userRepository.save(user);
        UserResponseView view = new UserResponseView();
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
        return view;
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public AdminDashboardResponse getDashboard() {
        AdminDashboardResponse response = new AdminDashboardResponse();
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
        Pageable pageable = PageRequest.of(Optional.ofNullable(request.getPageNo()).orElse(0), Optional.ofNullable(request.getMaxItem()).orElse(10), Sort.by(Sort.Direction.DESC, Optional.ofNullable(request.getSortBy()).orElse("deleteTimestamp")));
        Page<Post> postPage = null;
        postPage = postRepository.findAllDeletedPost(pageable);
        if (postPage.isEmpty()) {
            throw new IllegalStateException("No posts deleted yet.");
        }
        PostResponseViewList postResponseViewList = new PostResponseViewList();
        List<PostResponseView> postResponseViews = new ArrayList<>();
        for (Post post : postPage) {
            PostResponseView postResponseView = new PostResponseView();
            postResponseView.setId(post.getId());
            postResponseView.setTitle(post.getTitle());
            postResponseView.setCreationDate(timeUtil.getCreationTimestamp(post.getCreationDate()));
            postResponseView.setDescription(post.getDescription());
            Optional.ofNullable(post.getUser().getFullName()).ifPresent(postResponseView::setFullName);
            Optional.ofNullable(post.getUser().getId()).ifPresent(postResponseView::setUserId);
            Optional.ofNullable(post.getUser().getUniversityId()).ifPresent(postResponseView::setUniversityId);
            Optional.ofNullable(post.getUser().getProfileImageId()).ifPresent(postResponseView::setProfileImageId);
            Optional.ofNullable(post.getDeleteTimestamp()).map(timeUtil::getCreationTimestamp).ifPresent(postResponseView::setDeleteDate);
            Optional.ofNullable(reviewRepository.getPostReviewCount(post, ReviewType.LIKE)).ifPresent(postResponseView::setNoOfLikes);
            Optional.ofNullable(commentRepository.getPostCommentCount(post)).ifPresent(postResponseView::setNoOfComments);

            List<Image> images = imageRepository.findImageByPost(post);
            List<ImageResponse> imageResponses = new ArrayList<>();
            if (!images.isEmpty()) {
                images.forEach(image -> {
                    ImageResponse imageResponse = new ImageResponse();
                    Optional.ofNullable(image.getId()).ifPresent(imageResponse::setId);
                    Optional.ofNullable(image.getImageName()).ifPresent(imageResponse::setImageName);
                    Optional.ofNullable(image.getUploadDate()).map(timeUtil::getCreationTimestamp).ifPresent(imageResponse::setUploadDate);
                    imageResponses.add(imageResponse);
                });
            }
            Optional.ofNullable(imageResponses).ifPresent(postResponseView::setImageResponses);

            List<Document> documents = documentRepository.findByPost(post);
            List<DocumentResponse> documentResponses = new ArrayList<>();
            if (!documents.isEmpty()) {

                documents.forEach(document -> {
                    DocumentResponse response = new DocumentResponse();
                    Optional.ofNullable(document.getId()).ifPresent(response::setId);
                    Optional.ofNullable(document.getDocumentName()).ifPresent(response::setFileName);
                    Optional.ofNullable(document.getUploadDate()).map(timeUtil::getCreationTimestamp).ifPresent(response::setUploadDate);
                    documentResponses.add(response);
                });

            }
            Optional.ofNullable(documentResponses).ifPresent(postResponseView::setDocumentResponses);
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
        Pageable pageable = PageRequest.of(Optional.ofNullable(request.getPageNo()).orElse(0), Optional.ofNullable(request.getMaxItem()).orElse(10), Sort.by(Sort.Direction.DESC, "deleteTimestamp"));
        Page<Comment> commentPage = commentRepository.findAllDeletedComment(pageable);
        if (commentPage.isEmpty()) {
            throw new IllegalStateException("No Comments Deleted yet.");
        }
        List<CommentResponseView> viewList = new ArrayList<>();
        for (Comment comment : commentPage) {
            CommentResponseView view = new CommentResponseView();
            Optional.ofNullable(comment.getTitle()).ifPresent(view::setTitle);
            Optional.ofNullable(comment.getDescription()).ifPresent(view::setDescription);
            Optional.ofNullable(comment.getId()).ifPresent(view::setId);
            Optional.ofNullable(timeUtil.getCreationTimestamp(comment.getCommentDate())).ifPresent(view::setCommentDate);
            Optional.ofNullable(comment.getDeleteTimestamp()).map(timeUtil::getCreationTimestamp).ifPresent(view::setDeleteDate);
            Optional.ofNullable(comment.getPost().getId()).ifPresent(view::setPostId);
            Optional.ofNullable(comment.getUser().getId()).ifPresent(view::setUserId);
            Optional.ofNullable(comment.getUser().getUniversityId()).ifPresent(view::setUniversityId);
            Optional.ofNullable(comment.getUser().getProfileImageId()).ifPresent(view::setProfileImageId);
            Optional.ofNullable(comment.getUser().getFullName()).ifPresent(view::setFullName);
            viewList.add(view);
        }
        CommentResponseViewList responseViewList = new CommentResponseViewList();
        Optional.ofNullable(commentPage.getTotalPages()).ifPresent(responseViewList::setTotalPages);
        Optional.ofNullable(commentPage.getTotalElements()).ifPresent(responseViewList::setTotalNumberOfItems);
        Optional.ofNullable(commentPage.getNumber()).ifPresent(responseViewList::setPageNo);
        Optional.ofNullable(commentPage.getSize()).ifPresent(responseViewList::setMaxItems);
        Optional.ofNullable(viewList).ifPresent(responseViewList::setCommentResponseViews);
        return responseViewList;
    }

    @Override
    public UserResponseView toggleUserRole(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("User not Found");
        }
        Optional<Role> role_user = roleRepository.findByName("ROLE_USER");
        Optional<Role> role_admin = roleRepository.findByName("ROLE_ADMIN");
        User user = optionalUser.get();
        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        if (roles.contains("ROLE_ADMIN")) {
            user.getRoles().remove(role_admin.get());
            user.getRoles().add(role_user.get());
        } else {
            user.getRoles().remove(role_user.get());
            user.getRoles().add(role_admin.get());
        }
        user = userRepository.save(user);
        List<String> updatedRoles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        UserResponseView view = new UserResponseView();
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

        emailService.sendRoleChangeEmail(user.getFirstName(), user.getUsername(), updatedRoles.toString());
        return view;
    }

    @Override
    @Transactional
    public DeleteResponseView deleteUser(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("User not Found");
        }
        User user = optionalUser.get();
        postRepository.deleteByUser(user);
        confirmationTokenRepository.deleteByUser(user);
        commentRepository.deleteByUser(user);
        documentRepository.deleteByUser(user);
        imageRepository.deleteByUser(user);
        reviewRepository.deleteByUser(user);
        userRepository.delete(user);
        DeleteResponseView view = new DeleteResponseView();
        view.setMessage("User with username: " + user.getUsername() + " is deleted successfully");
        return view;
    }

    @Override
    public String sendActivationLink(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()){
            throw new IllegalStateException("User not found");
        }
        User user =optionalUser.get();
        if (user.getIsActive()){
            throw new IllegalStateException("User Account already activated");
        }
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new IllegalStateException("Failed to send activation link please try again");
        }

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setCreatedAt(LocalDateTime.now());
        confirmationToken.setExpiresAt(LocalDateTime.now().plusDays(1));
        String token = UUID.randomUUID().toString();
        confirmationToken.setToken(token);
        confirmationToken.setUser(user);
        confirmationTokenRepository.save(confirmationToken);

        String link = "http://" + ip + ":" + port + "/auth/activate/account?token=" + token;
        emailService.sendActivationLinkEmail(user.getFullName(), user.getUsername(), link,timeUtil.getLastLoginTimestamp(confirmationToken.getExpiresAt()));
        return "Activation link sent to : ".toUpperCase()+user.getUsername();
    }


}
