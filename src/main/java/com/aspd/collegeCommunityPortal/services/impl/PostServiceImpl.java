package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.beans.request.CreatePostRequest;
import com.aspd.collegeCommunityPortal.beans.request.PostRequest;
import com.aspd.collegeCommunityPortal.beans.response.DeleteResponseView;
import com.aspd.collegeCommunityPortal.beans.response.PostResponseView;
import com.aspd.collegeCommunityPortal.beans.response.PostResponseViewList;
import com.aspd.collegeCommunityPortal.beans.response.PostSearchResponseViewList;
import com.aspd.collegeCommunityPortal.config.BucketName;
import com.aspd.collegeCommunityPortal.model.Document;
import com.aspd.collegeCommunityPortal.model.Image;
import com.aspd.collegeCommunityPortal.model.Post;
import com.aspd.collegeCommunityPortal.repositories.*;
import com.aspd.collegeCommunityPortal.services.AmazonS3Service;
import com.aspd.collegeCommunityPortal.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BucketName bucketName;
    @Autowired
    private AmazonS3Service amazonS3Service;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private ImageRepository imageRepository;

    @Override
    public PostResponseViewList getAllPost(PostRequest postRequest) {
        Map<Integer,Integer> postLikeCount=null;
        Map<Integer,Integer> postCommentCount=null;
        PostResponseViewList postResponseViewList=new PostResponseViewList();
        Pageable pageable= PageRequest.of(Optional.ofNullable(postRequest.getPageNo()).orElse(0),Optional.ofNullable(postRequest.getMaxPostRequest()).orElse(15), Sort.by(Optional.ofNullable(postRequest.getSortBy()).orElse("creationDate")));
        Page<Post> postPage = postRepository.findAll(pageable);


        if(!postPage.isEmpty()){
            //Extracting post ids;
            List<Integer> postIds = postPage.get().map(Post::getId).collect(Collectors.toList());
            //Counting likes of post
            Optional<Map<Integer, Integer>> postsLikeCount = reviewRepository.getPostsLikeCount(postIds);
            if(postsLikeCount.isPresent()){
                postLikeCount=postsLikeCount.get();
            }
            //Counting comments of posts
            Optional<Map<Integer, Integer>> postsCommentCount = commentRepository.getPostsCommentCount(postIds);
            if (postsCommentCount.isPresent()){
                postCommentCount=postsCommentCount.get();
            }
            List<PostResponseView> postResponseViews=new ArrayList<>();
            for(Post post:postPage){
                PostResponseView postResponseView=new PostResponseView();
                postResponseView.setId(post.getId());
                postResponseView.setTitle(post.getTitle());
                postResponseView.setCreationDate(post.getCreationDate());
                postResponseView.setDescription(post.getDescription());
                Optional.ofNullable(post.getUser().getFirstName().concat(" ").concat(post.getUser().getLastName())).ifPresent(postResponseView::setUser);
                Optional.ofNullable(post.getUser().getId()).ifPresent(postResponseView::setUserId);
                Optional.ofNullable(postLikeCount.get(post.getId())).ifPresent(postResponseView::setNoOfLikes);
                Optional.ofNullable(postCommentCount.get(post.getId())).ifPresent(postResponseView::setNoOfComments);
                // Add images and attachments related to posts
                postResponseViews.add(postResponseView);
            }

            postResponseViewList.setPostResponseViews(postResponseViews);
            postResponseViewList.setTotalNoOfPost(postPage.getNumberOfElements());
            postResponseViewList.setPageNo(postPage.getNumber());
            postResponseViewList.setTotalPages(postPage.getTotalPages());

        }
        else {
            //Exception Handeling
        }
        return postResponseViewList;
    }

    @Override
    public PostResponseView createPost(CreatePostRequest createPostRequest) {
        Post post=new Post();
        post.setTitle(Optional.ofNullable(createPostRequest.getTitle()).orElseThrow(() -> new RuntimeException("Title cannot be null")));
        post.setDescription(Optional.ofNullable(createPostRequest.getDescription()).orElseThrow(()->new RuntimeException("Description cannot be null")));
        Optional.ofNullable(LocalDateTime.now()).ifPresent(post::setCreationDate);

        Post savedPost = postRepository.save(post);
        //Extract user from JWT header and fill it in post

        //Upload images  to amazon S3
        if(createPostRequest.getImages().isPresent() && !createPostRequest.getImages().get().isEmpty()){
               List<Image> images=new ArrayList<>();
               createPostRequest.getImages().get().forEach(image->{
                   Image image1=new Image();
                   String filename=image.getName().concat(UUID.randomUUID().toString());
                   image1.setImageName(filename);
                   image1.setUser(null); //set user after extracting from JWT or Database;
                   image1.setPost(savedPost);
                   String path= bucketName.getCcpBucketName().concat("/").concat("username").concat("/images");
                   image1.setPath(path);
                   Map<String,String> metadata=new HashMap<>();
                   metadata.put("Content-Type", image.getContentType());
                   metadata.put("Content-Length", String.valueOf(image.getSize()));
                   try {
                       amazonS3Service.uploadFile(path,filename,Optional.of(metadata),image.getInputStream());
                       images.add(image1);
                   }
                   catch (IOException e){
                       throw new IllegalStateException("Failed to upload image",e);  //TODO implement custom exception
                   }

               });
               imageRepository.saveAll(images);
        }
        if (createPostRequest.getDocuments().isPresent() && !createPostRequest.getDocuments().get().isEmpty()){
            List<Document> documents= new ArrayList<>();
            createPostRequest.getDocuments().get().forEach(file -> {
                String filename=file.getName().concat(UUID.randomUUID().toString());
                String path=bucketName.getCcpBucketName().concat("/username").concat("/documents");
                Document document=new Document();
                document.setPost(savedPost);
                document.setDocumentName(filename);
                document.setUser(null); //TODO add user
                document.setPath(path);
                Map<String,String> metadata=new HashMap<>();
                metadata.put("Content-Type", file.getContentType());
                metadata.put("Content-Length", String.valueOf(file.getSize()));
                try {
                    amazonS3Service.uploadFile(path,filename,Optional.of(metadata),file.getInputStream());
                    documents.add(document);
                }
                catch (IOException e){
                    throw new IllegalStateException("Failed to upload image",e);  //TODO implement custom exception
                }
            });
            documentRepository.saveAll(documents);
        }

        PostResponseView postResponseView=new PostResponseView();
        postResponseView.setId(savedPost.getId());
        postResponseView.setTitle(savedPost.getTitle());
        postResponseView.setDescription(savedPost.getDescription());
        postResponseView.setCreationDate(savedPost.getCreationDate());
        postResponseView.setUser(savedPost.getUser().getFirstName().concat(" ".concat(savedPost.getUser().getLastName())));
        postResponseView.setUserId(savedPost.getUser().getId());

        return postResponseView;
    }

    @Override
    public PostSearchResponseViewList searchPost(String title) {
        return null;
    }

    @Override
    public DeleteResponseView deletePost(int postId) {
        return null;
    }
}
