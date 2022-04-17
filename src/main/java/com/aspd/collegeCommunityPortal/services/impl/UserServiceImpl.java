package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.beans.request.PostRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserDocumentRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserImageRequest;
import com.aspd.collegeCommunityPortal.beans.response.*;
import com.aspd.collegeCommunityPortal.config.BucketName;
import com.aspd.collegeCommunityPortal.model.*;
import com.aspd.collegeCommunityPortal.repositories.*;
import com.aspd.collegeCommunityPortal.services.AmazonS3Service;
import com.aspd.collegeCommunityPortal.services.LocalStorageService;
import com.aspd.collegeCommunityPortal.services.UserService;
import org.apache.http.entity.ContentType;
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

    private final List<String> imageExtensions= Arrays.asList(ContentType.IMAGE_GIF.getMimeType(),ContentType.IMAGE_JPEG.getMimeType(),ContentType.IMAGE_PNG.getMimeType(),ContentType.IMAGE_BMP.getMimeType());

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            throw new UsernameNotFoundException("User not found with username: "+username);
        }
        User user = optionalUser.get();
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

    @Override
    public UserImageResponse getUserImages(UserImageRequest request) {
        Pageable pageable= PageRequest.of(Optional.ofNullable(request.getPageNo()).orElse(0),Optional.ofNullable(request.getMaxImage()).orElse(10));
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal !=null){
            Page<Image> userImages = imageRepository.findImageByUser(userPrincipal.getUser(), pageable);
            if (userImages==null || userImages.isEmpty()){
                throw new IllegalStateException("No images found.");
            }
            UserImageResponse userImageResponse=new UserImageResponse();
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
    public UserDocumentResponse getUserDocuments(UserDocumentRequest request) {
        Pageable pageable= PageRequest.of(Optional.ofNullable(request.getPageNo()).orElse(0),Optional.ofNullable(request.getMaxImage()).orElse(10));
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal!=null){
            Page<Document> userDocuments = documentRepository.findByUser(userPrincipal.getUser(), pageable);
            if (userDocuments==null || userDocuments.isEmpty()){
                throw new IllegalStateException("No documents found");
            }
            UserDocumentResponse userDocumentResponse=new UserDocumentResponse();
            Optional.ofNullable(userDocuments.stream().map(Document::getId).collect(Collectors.toList())).ifPresent(userDocumentResponse::setDocumentIds);
            Optional.ofNullable(userDocuments.getTotalPages()).ifPresent(userDocumentResponse::setTotalPages);
            Optional.ofNullable(userDocuments.getTotalElements()).ifPresent(userDocumentResponse::setTotalNumberOfItems);
            Optional.ofNullable(userDocuments.getSize()).ifPresent(userDocumentResponse::setMaxItems);
            Optional.ofNullable(userDocuments.getNumber()).ifPresent(userDocumentResponse::setPageNo);
            return userDocumentResponse;
        }
        return null;
    }

    @Override
    public PostResponseViewList getUserPost(PostRequest postRequest) {
        Pageable pageable= PageRequest.of(Optional.ofNullable(postRequest.getPageNo()).orElse(0),Optional.ofNullable(postRequest.getMaxItem()).orElse(15), Sort.by(Sort.Direction.DESC,Optional.ofNullable(postRequest.getSortBy()).orElse("creationDate")));
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userPrincipal!=null){
            Page<Post> userPosts = postRepository.findPostByUser(userPrincipal.getUser(), pageable);
            if (userPosts ==null || userPosts.isEmpty()){
                throw new IllegalStateException("You haven't posted anything yet.");
            }
            PostResponseViewList postResponseViewList=new PostResponseViewList();
            List<PostResponseView> postResponseViews=new ArrayList<>();
            for(Post post:userPosts){
                if (post.getIsDeleted()==null || post.getIsDeleted()){
                    continue;
                }
                PostResponseView postResponseView=new PostResponseView();
                postResponseView.setId(post.getId());
                postResponseView.setTitle(post.getTitle());
                postResponseView.setCreationDate(post.getCreationDate());
                postResponseView.setDescription(post.getDescription());
                Optional.ofNullable(post.getUser().getFullName()).ifPresent(postResponseView::setFullName);
                Optional.ofNullable(post.getUser().getId()).ifPresent(postResponseView::setUserId);
                Optional.ofNullable(reviewRepository.getPostReviewCount(post,ReviewType.LIKE)).ifPresent(postResponseView::setNoOfLikes);
                Optional.ofNullable(commentRepository.getPostCommentCount(post)).ifPresent(postResponseView::setNoOfComments);
                Optional.ofNullable(imageRepository.findImageByPost(post)).map(images -> images.stream().map(Image::getId).collect(Collectors.toList())).ifPresent(postResponseView::setImageIds);
                Optional.ofNullable(documentRepository.findByPost(post)).map(documents -> documents.stream().map(Document::getId).collect(Collectors.toList())).ifPresent(postResponseView::setDocumentIds);
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
        if(profileImage.isEmpty()){
           throw new IllegalStateException("File cannot be empty");
        }
        if (!imageExtensions.contains(profileImage.getContentType())){
            throw new IllegalStateException("File format supported are: "+imageExtensions);
        }
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user=userPrincipal.getUser();
        Image image=new Image();
        String path=bucketName.getCcpBucketName().concat("/").concat(userPrincipal.getUsername()).concat("/profileImages");
        String filename=LocalDateTime.now().toString().concat("_").concat(profileImage.getOriginalFilename());
        image.setImageName(filename);
        image.setUser(user);
        image.setPath(path);
        image.setUploadDate(LocalDateTime.now());
        Map<String,String> metadata=new HashMap<>();
        metadata.put("Content-Type", profileImage.getContentType());
        metadata.put("Content-Length", String.valueOf(profileImage.getSize()));
        try {
            Boolean uploaded = localStorageService.uploadFile(path, filename, Optional.ofNullable(metadata), profileImage.getInputStream());
            Image savedImage = imageRepository.save(image);
            user.setProfileImageId(savedImage.getId());
            userRepository.save(user);
            return savedImage.getId();
        }catch (IOException e) {
            throw new IllegalStateException("Error in uploading images");
        }
    }


}
