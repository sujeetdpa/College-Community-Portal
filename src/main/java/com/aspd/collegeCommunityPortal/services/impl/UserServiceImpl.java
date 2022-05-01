package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.beans.request.*;
import com.aspd.collegeCommunityPortal.beans.response.*;
import com.aspd.collegeCommunityPortal.config.BucketName;
import com.aspd.collegeCommunityPortal.model.*;
import com.aspd.collegeCommunityPortal.repositories.*;
import com.aspd.collegeCommunityPortal.services.AmazonS3Service;
import com.aspd.collegeCommunityPortal.services.LocalStorageService;
import com.aspd.collegeCommunityPortal.services.UserService;
import com.aspd.collegeCommunityPortal.util.TimeUtil;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final List<String> imageExtensions = Arrays.asList(ContentType.IMAGE_GIF.getMimeType(), ContentType.IMAGE_JPEG.getMimeType(), ContentType.IMAGE_PNG.getMimeType(), ContentType.IMAGE_BMP.getMimeType());

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LocalStorageService localStorageService;
    @Autowired
    private AmazonS3Service amazonS3Service;
    @Autowired
    private BucketName bucketName;
    @Autowired
    private TimeUtil timeUtil;

    @Override
    public UserDetails loadUserByUsername(String username){
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        User user = optionalUser.get();
        user.setLastLoginTimestamp(user.getCurrentLoginTimeStamp());
        user.setCurrentLoginTimeStamp(LocalDateTime.now());
        userRepository.save(user);
        return new UserPrincipal(user);
    }

    @Override
    public UserResponseView getUser(String universityId) {
        Optional<User> optionalUser = userRepository.findByUniversityId(universityId);
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("User not found with id: " + universityId);
        }
        UserResponseView view = new UserResponseView();
        User user = optionalUser.get();
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
        return view;
    }

    @Override
    public UserImageResponse getUserImages(UserImageRequest request) {
        Pageable pageable = PageRequest.of(Optional.ofNullable(request.getPageNo()).orElse(0), Optional.ofNullable(request.getMaxItems()).orElse(10));
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal != null) {
            Page<Image> userImages = imageRepository.findImageByUser(userPrincipal.getUser(), pageable);
            if (userImages == null || userImages.isEmpty()) {
                throw new IllegalStateException("No images found.");
            }
            UserImageResponse userImageResponse = new UserImageResponse();
            Optional.ofNullable(userImages.stream().map(Image::getId).collect(Collectors.toList())).ifPresent(userImageResponse::setImageIds);
            Optional.ofNullable(userImages.getTotalPages()).ifPresent(userImageResponse::setTotalPages);
            Optional.ofNullable(userImages.getTotalElements()).ifPresent(userImageResponse::setTotalNumberOfItems);
            Optional.ofNullable(userImages.getSize()).ifPresent(userImageResponse::setMaxItems);
            Optional.ofNullable(userImages.getNumber()).ifPresent(userImageResponse::setPageNo);
            return userImageResponse;
        }
        return null;
    }

    @Override
    public UserDocumentResponseList getUserDocuments(UserDocumentRequest request) {
        Pageable pageable = PageRequest.of(Optional.ofNullable(request.getPageNo()).orElse(0), Optional.ofNullable(request.getMaxItems()).orElse(10));
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal != null) {
            Page<Document> userDocuments = documentRepository.findByUser(userPrincipal.getUser(), pageable);
            if (userDocuments == null || userDocuments.isEmpty()) {
                throw new IllegalStateException("No documents found");
            }
            List<UserDocumentResponse> documentResponses=new ArrayList<>();
            userDocuments.forEach(document -> {
                UserDocumentResponse response=new UserDocumentResponse();
                Optional.ofNullable(document.getId()).ifPresent(response::setId);
                Optional.ofNullable(document.getDocumentName()).ifPresent(response::setFileName);
                Optional.ofNullable(document.getUploadDate()).map(timeUtil::getCreationTimestamp).ifPresent(response::setUploadDate);
                documentResponses.add(response);
            });
            UserDocumentResponseList responseList = new UserDocumentResponseList();
            responseList.setUserDocumentResponses(documentResponses);
            Optional.ofNullable(userDocuments.getTotalPages()).ifPresent(responseList::setTotalPages);
            Optional.ofNullable(userDocuments.getTotalElements()).ifPresent(responseList::setTotalNumberOfItems);
            Optional.ofNullable(userDocuments.getSize()).ifPresent(responseList::setMaxItems);
            Optional.ofNullable(userDocuments.getNumber()).ifPresent(responseList::setPageNo);
            return responseList;
        }
        return null;
    }

    @Override
    public PostResponseViewList getUserPost(PostRequest postRequest) {
        Pageable pageable = PageRequest.of(Optional.ofNullable(postRequest.getPageNo()).orElse(0), Optional.ofNullable(postRequest.getMaxItem()).orElse(15), Sort.by(Sort.Direction.DESC, Optional.ofNullable(postRequest.getSortBy()).orElse("creationDate")));
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal != null) {
            Page<Post> userPosts = postRepository.findPostByUser(userPrincipal.getUser(), pageable);
            if (userPosts == null || userPosts.isEmpty()) {
                throw new IllegalStateException("You haven't posted anything yet.");
            }
            PostResponseViewList postResponseViewList = new PostResponseViewList();
            List<PostResponseView> postResponseViews = new ArrayList<>();
            for (Post post : userPosts) {
                PostResponseView postResponseView = new PostResponseView();
                postResponseView.setId(post.getId());
                postResponseView.setTitle(post.getTitle());
                postResponseView.setCreationDate(timeUtil.getCreationTimestamp(post.getCreationDate()));
                postResponseView.setDescription(post.getDescription());
                postResponseView.setProfileImageId(post.getUser().getProfileImageId());
                Optional.ofNullable(post.getUser().getFullName()).ifPresent(postResponseView::setFullName);
                Optional.ofNullable(post.getUser().getId()).ifPresent(postResponseView::setUserId);
                Optional.ofNullable(reviewRepository.getPostReviewCount(post, ReviewType.LIKE)).ifPresent(postResponseView::setNoOfLikes);
                Optional.ofNullable(commentRepository.getPostCommentCount(post)).ifPresent(postResponseView::setNoOfComments);
                Optional.ofNullable(imageRepository.findImageByPost(post)).map(images -> images.stream().map(Image::getId).collect(Collectors.toList())).ifPresent(postResponseView::setImageIds);
                List<Document> documents = documentRepository.findByPost(post);
                List<UserDocumentResponse> documentResponses = new ArrayList<>();
                if(!documents.isEmpty()) {

                    documents.forEach(document -> {
                        UserDocumentResponse response = new UserDocumentResponse();
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
            postResponseViewList.setTotalNumberOfItems(userPosts.getTotalElements());
            postResponseViewList.setPageNo(userPosts.getNumber());
            postResponseViewList.setTotalPages(userPosts.getTotalPages());
            postResponseViewList.setMaxItems(userPosts.getSize());
            return postResponseViewList;
        }
        return null;
    }

    @Override
    public Integer updateProfileImage(MultipartFile profileImage) {
        if (profileImage.isEmpty()) {
            throw new IllegalStateException("File cannot be empty");
        }
        if (!imageExtensions.contains(profileImage.getContentType())) {
            throw new IllegalStateException("File format supported are: " + imageExtensions);
        }
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userPrincipal.getUser();
        Image image = new Image();
        String path = bucketName.getCcpBucketName().concat("/").concat(userPrincipal.getUsername()).concat("/profileImages");
        String filename = UUID.randomUUID().toString().concat("_").concat(profileImage.getOriginalFilename());
        image.setImageName(filename);
        image.setUser(user);
        image.setPath(path);
        image.setUploadDate(LocalDateTime.now());
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", profileImage.getContentType());
        metadata.put("Content-Length", String.valueOf(profileImage.getSize()));
        try {
//            Boolean uploaded = localStorageService.uploadFile(path, filename, Optional.ofNullable(metadata), profileImage.getInputStream());
            amazonS3Service.uploadFile(path,filename,Optional.ofNullable(metadata),profileImage.getInputStream());
            Image savedImage = imageRepository.save(image);
            user.setProfileImageId(savedImage.getId());
            userRepository.save(user);
            return savedImage.getId();
        } catch (IOException e) {
            throw new IllegalStateException("Error in uploading images");
        }
    }

    @Override
    public UserResponseView updateUser(Integer userId, UserUpdateRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (Optional.ofNullable(request).isEmpty()) {
            throw new IllegalStateException("Invalid data");
        }
        User user = userPrincipal.getUser();
        if (!user.getId().equals(userId)) {
            throw new IllegalStateException("You don't have the required permissions");
        }
        Optional.ofNullable(request.getMobileNo()).ifPresent(user::setMobileNo);
        Optional.ofNullable(request.getDob()).ifPresent(user::setDob);
        Optional.ofNullable(request.getGender()).map(Gender::valueOf).ifPresent(user::setGender);
        Optional.ofNullable(request.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(request.getLastName()).ifPresent(user::setLastName);
        User updatedUser = userRepository.save(user);

        UserResponseView view = new UserResponseView();
        Optional.ofNullable(updatedUser.getId()).ifPresent(view::setId);
        Optional.ofNullable(updatedUser.getFirstName()).ifPresent(view::setFirstName);
        Optional.ofNullable(updatedUser.getLastName()).ifPresent(view::setLastName);
        Optional.ofNullable(updatedUser.getFullName()).ifPresent(view::setFullName);
        Optional.ofNullable(updatedUser.getUsername()).ifPresent(view::setUsername);
        Optional.ofNullable(updatedUser.getDob()).ifPresent(view::setDob);
        Optional.ofNullable(updatedUser.getLastLoginTimestamp()).map(timeUtil::getLastLoginTimestamp).ifPresent(view::setLastLoginTimestamp);
        Optional.ofNullable(updatedUser.getMobileNo()).ifPresent(view::setMobileNo);
        Optional.ofNullable(updatedUser.getUniversityId()).ifPresent(view::setUniversityId);
        Optional.ofNullable(updatedUser.getGender().toString()).ifPresent(view::setGender);
        Optional.ofNullable(updatedUser.getUserCreationTimestamp()).map(timeUtil::getUserJoinDate).ifPresent(view::setUserCreationTimestamp);
        return view;
    }

    @Override
    public UserDashboardResponse getUserDashboard() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user=userPrincipal.getUser();
        UserDashboardResponse response=new UserDashboardResponse();
        Page<Post> posts = postRepository.findPostByUser(user, Pageable.unpaged());
        if (!posts.isEmpty()){
            Optional.ofNullable(posts.getContent()).map(posts1 -> reviewRepository.countByPosts(posts1,ReviewType.LIKE)).ifPresent(response::setNumberOfLikesAchieved);
            Optional.ofNullable(posts.getContent()).map(posts1 -> reviewRepository.countByPosts(posts1,ReviewType.DISLIKE)).ifPresent(response::setNumberOfDislikesAchieved);
            Optional.ofNullable(posts.getContent()).map(posts1 -> commentRepository.countByPosts(posts1)).ifPresent(response::setNumberOfCommentsAchieved);
        }
        Optional.ofNullable(user).map(user1 -> reviewRepository.countByUser(user1,ReviewType.LIKE)).ifPresent(response::setNumberOfLikesMade);
        Optional.ofNullable(user).map(user1 -> reviewRepository.countByUser(user,ReviewType.DISLIKE)).ifPresent(response::setNumberOfDislikedMade);
        Optional.ofNullable(user).map(commentRepository::countByUser).ifPresent(response::setNumberOfCommentsMade);
        Optional.ofNullable(user).map(postRepository::countByUser).ifPresent(response::setNumberOfPosts);
        Optional.ofNullable(user).map(imageRepository::countByUser).ifPresent(response::setNumberOfImages);
        Optional.ofNullable(user).map(documentRepository::countByUser).ifPresent(response::setNumberOfDocuments);
        return response;
    }

}
