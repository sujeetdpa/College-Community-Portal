package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.beans.request.UserDocumentRequest;
import com.aspd.collegeCommunityPortal.beans.request.UserImageRequest;
import com.aspd.collegeCommunityPortal.beans.response.UserDocumentResponse;
import com.aspd.collegeCommunityPortal.beans.response.UserImageResponse;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseView;
import com.aspd.collegeCommunityPortal.model.Document;
import com.aspd.collegeCommunityPortal.model.Image;
import com.aspd.collegeCommunityPortal.model.User;
import com.aspd.collegeCommunityPortal.model.UserPrincipal;
import com.aspd.collegeCommunityPortal.repositories.DocumentRepository;
import com.aspd.collegeCommunityPortal.repositories.ImageRepository;
import com.aspd.collegeCommunityPortal.repositories.RoleRepository;
import com.aspd.collegeCommunityPortal.repositories.UserRepository;
import com.aspd.collegeCommunityPortal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private DocumentRepository documentRepository;

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

}
