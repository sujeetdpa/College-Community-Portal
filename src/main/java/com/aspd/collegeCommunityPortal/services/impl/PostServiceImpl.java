package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.beans.request.*;
import com.aspd.collegeCommunityPortal.beans.response.*;
import com.aspd.collegeCommunityPortal.config.BucketName;
import com.aspd.collegeCommunityPortal.model.*;
import com.aspd.collegeCommunityPortal.repositories.*;
import com.aspd.collegeCommunityPortal.services.AmazonS3Service;
import com.aspd.collegeCommunityPortal.services.PostService;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostServiceImpl implements PostService {

    private final List<String> imageExtensions=Arrays.asList(ContentType.IMAGE_GIF.getMimeType(),ContentType.IMAGE_JPEG.getMimeType(),ContentType.IMAGE_PNG.getMimeType(),ContentType.IMAGE_BMP.getMimeType());
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

            List<PostResponseView> postResponseViews=new ArrayList<>();
            for(Post post:postPage){
                PostResponseView postResponseView=new PostResponseView();
                postResponseView.setId(post.getId());
                postResponseView.setTitle(post.getTitle());
                postResponseView.setCreationDate(post.getCreationDate());
                postResponseView.setDescription(post.getDescription());
                Optional.ofNullable(post.getUser().getFullName()).ifPresent(postResponseView::setFullName);
                Optional.ofNullable(post.getUser().getId()).ifPresent(postResponseView::setUserId);
                Optional.ofNullable(reviewRepository.getPostReviewCount(post,ReviewType.LIKE)).ifPresent(postResponseView::setNoOfLikes);
                Optional.ofNullable(commentRepository.getPostCommentCount(post)).ifPresent(postResponseView::setNoOfComments);
                // Add images and attachments related to posts
                postResponseViews.add(postResponseView);
            }

            postResponseViewList.setPostResponseViews(postResponseViews);
            postResponseViewList.setTotalNoOfPost(postPage.getTotalElements());
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
        //Extract user from JWT header and fill it in post
        UserPrincipal userPrincipal=(UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        post.setUser(userPrincipal.getUser());
        Post savedPost = postRepository.save(post);

        //Upload images  to amazon S3
        if(createPostRequest.getImages().isPresent() && !createPostRequest.getImages().get().isEmpty()){
               List<Image> images=new ArrayList<>();
               createPostRequest.getImages().get().forEach(image->{
                   if (!imageExtensions.contains(image.getContentType())){
                       //TODO throw error for incompatible file type
                   }
                   Image image1=new Image();
                   String filename=image.getName().concat(UUID.randomUUID().toString()).concat(LocalDateTime.now().toString());
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
                //TODO check for file extensions and throw error
                String filename=file.getName().concat(UUID.randomUUID().toString()).concat(LocalDateTime.now().toString());
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
        postResponseView.setFullName(savedPost.getUser().getFullName());
        postResponseView.setUserId(savedPost.getUser().getId());

        return postResponseView;
    }

    @Override
    public PostSearchResponseViewList searchPost(PostSearchRequest request) {
        Pageable pageable=PageRequest.of(Optional.ofNullable(request.getPageNo()).orElse(0),Optional.ofNullable(request.getMaxItemsPerPage()).orElse(15), Sort.by(Sort.Direction.DESC,"creationDate"));
        Page<Post> postPage=null;
        List<PostSearchResponseView> responseViewList=new ArrayList<>();
        if(Optional.ofNullable(request.getTitle()).isPresent()){
            postPage=postRepository.searchPostByTitle(request.getTitle(),pageable);
        }
        // TODO more searches to add here
        if(postPage!=null && !postPage.isEmpty()){
            postPage.forEach(post -> {
                PostSearchResponseView responseView=new PostSearchResponseView();
                Optional.ofNullable(post.getId()).ifPresent(responseView::setId);
                Optional.ofNullable(post.getTitle()).ifPresent(responseView::setTitle);
                Optional.ofNullable(post.getUser().getId()).ifPresent(responseView::setUserId);
                Optional.ofNullable(post.getUser().getFullName()).ifPresent(responseView::setFullName);
                Optional.ofNullable(post.getUser().getUsername()).ifPresent(responseView::setUsername);
                responseViewList.add(responseView);
            });
        }
        PostSearchResponseViewList list=new PostSearchResponseViewList();
        Optional.ofNullable(postPage.getTotalPages()).ifPresent(list::setTotalPages);
        Optional.ofNullable(postPage.getTotalElements()).ifPresent(list::setTotalPosts);
        Optional.ofNullable(postPage.getSize()).ifPresent(list::setMaxPosts);
        Optional.ofNullable(postPage.getNumber()).ifPresent(list::setPageNo);
        Optional.ofNullable(responseViewList).ifPresent(list::setPostSearchResponseViews);
        return list;
    }

    @Override
    public DeleteResponseView deletePost(int postId) {
        return null;
    }

    @Override
    public PostResponseView getPost(int postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if(optionalPost.isPresent()){
            PostResponseView responseView=new PostResponseView();
            Post post = optionalPost.get();
            Optional.ofNullable(post.getId()).ifPresent(responseView::setId);
            Optional.ofNullable(post.getTitle()).ifPresent(responseView::setTitle);
            Optional.ofNullable(post.getDescription()).ifPresent(responseView::setDescription);
            Optional.ofNullable(post.getCreationDate()).ifPresent(responseView::setCreationDate);
            Optional.ofNullable(post.getUser().getId()).ifPresent(responseView::setUserId);
            Optional.ofNullable(post.getUser().getFullName()).ifPresent(responseView::setFullName);
            Optional.ofNullable(commentRepository.getPostCommentCount(post)).ifPresent(responseView::setNoOfComments);
            Optional.ofNullable(reviewRepository.getPostReviewCount(post,ReviewType.LIKE)).ifPresent(responseView::setNoOfLikes);
            // TODO add images and files to view
            return responseView;
        }
        return null;
    }

    @Override
    public CommentResponseView newComment(CommentRequest request) {
        Optional<Post> optionalPost = postRepository.findById(request.getPostId());
        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        UserPrincipal userPrincipal=(UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (optionalPost.isPresent() && userPrincipal.getUser()!=null){
            Comment comment=new Comment();
            Optional.ofNullable(request.getTitle()).ifPresent(comment::setTitle);
            Optional.ofNullable(request.getDescription()).ifPresent(comment::setDescription);
            Optional.ofNullable(LocalDateTime.now()).ifPresent(comment::setCommentDate);
            optionalPost.ifPresent(comment::setPost);
            Optional.ofNullable(userPrincipal.getUser()).ifPresent(comment::setUser);
            Comment savedComment = commentRepository.save(comment);

            CommentResponseView view=new CommentResponseView();
            Optional.ofNullable(savedComment.getTitle()).ifPresent(view::setTitle);
            Optional.ofNullable(savedComment.getDescription()).ifPresent(view::setDescription);
            Optional.ofNullable(savedComment.getId()).ifPresent(view::setId);
            Optional.ofNullable(savedComment.getCommentDate()).ifPresent(view::setCommentDate);
            Optional.ofNullable(savedComment.getPost().getId()).ifPresent(view::setPostId);
            Optional.ofNullable(savedComment.getUser().getId()).ifPresent(view::setUserId);
            Optional.ofNullable(savedComment.getUser().getFullName()).ifPresent(view::setFullName);
            return view;
        }
        return null;
    }

    @Override
    public CommentResponseViewList getPostComments(int postId, PostCommentFetchRequest request) {
        Pageable pageable=PageRequest.of(Optional.ofNullable(request.getPageNo()).orElse(0),Optional.ofNullable(request.getItemsPerPage()).orElse(15),Sort.by(Sort.Direction.ASC,"commentDate"));
        Optional<Post> optionalPost = postRepository.findById(postId);
        Page<Comment> comments=null;
        if (optionalPost.isPresent()){
            comments = commentRepository.findByPost(optionalPost.get(), pageable);
        }
        if(comments!=null && !comments.isEmpty()){
            List<CommentResponseView> viewList=new ArrayList<>();
            for (Comment comment:comments){
                CommentResponseView view=new CommentResponseView();
                Optional.ofNullable(comment.getTitle()).ifPresent(view::setTitle);
                Optional.ofNullable(comment.getDescription()).ifPresent(view::setDescription);
                Optional.ofNullable(comment.getId()).ifPresent(view::setId);
                Optional.ofNullable(comment.getCommentDate()).ifPresent(view::setCommentDate);
                Optional.ofNullable(comment.getPost().getId()).ifPresent(view::setPostId);
                Optional.ofNullable(comment.getUser().getId()).ifPresent(view::setUserId);
                Optional.ofNullable(comment.getUser().getFullName()).ifPresent(view::setFullName);
                viewList.add(view);
            }
            CommentResponseViewList responseViewList=new CommentResponseViewList();
            Optional.ofNullable(comments.getTotalPages()).ifPresent(responseViewList::setTotalPages);
            Optional.ofNullable(comments.getTotalElements()).ifPresent(responseViewList::setTotalNoOfComment);
            Optional.ofNullable(comments.getNumber()).ifPresent(responseViewList::setPageNo);
            Optional.ofNullable(viewList).ifPresent(responseViewList::setCommentResponseViews);
            return responseViewList;
        }
        return null;
    }

    @Override
    public LikePostResponse likePost(int postId, int userId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Review> optionalReview = reviewRepository.findByPostAndUserAndReviewType(optionalPost.get(), optionalUser.get(), ReviewType.LIKE);
        LikePostResponse response=null;
        if(optionalReview.isPresent()){
            response=new LikePostResponse();
            Optional.ofNullable(optionalReview.get()).map(Review::getPost).map(Post::getId).ifPresent(response::setPostId);
            Optional.ofNullable(optionalReview.get()).map(Review::getUser).map(User::getId).ifPresent(response::setUserId);
            Optional.ofNullable(reviewRepository.getPostReviewCount(optionalPost.get(),ReviewType.LIKE)).ifPresent(response::setNoOfLikes);
            Optional.ofNullable("Like removed").ifPresent(response::setMessage);
            reviewRepository.delete(optionalReview.get());
        }
        else if(optionalPost.isPresent() && optionalUser.isPresent()){
            Review review=new Review();
            Optional.ofNullable(optionalPost.get()).ifPresent(review::setPost);
            Optional.ofNullable(optionalUser.get()).ifPresent(review::setUser);
            Optional.ofNullable(ReviewType.LIKE).ifPresent(review::setReviewType);
            Optional.ofNullable(LocalDateTime.now()).ifPresent(review::setReviewDate);

            Review save = reviewRepository.save(review);

            response=new LikePostResponse();
            Optional.ofNullable(save).map(Review::getPost).map(Post::getId).ifPresent(response::setPostId);
            Optional.ofNullable(save).map(Review::getUser).map(User::getId).ifPresent(response::setUserId);
            Optional.ofNullable(reviewRepository.getPostReviewCount(optionalPost.get(),ReviewType.LIKE)).ifPresent(response::setNoOfLikes);
            Optional.ofNullable("You Liked this post").ifPresent(response::setMessage);

        }
        return response;
    }

    @Override
    public DislikePostResponse dislikePost(int postId, int userId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Review> optionalReview = reviewRepository.findByPostAndUserAndReviewType(optionalPost.get(), optionalUser.get(), ReviewType.DISLIKE);
        DislikePostResponse response=null;
        if(optionalReview.isPresent()){
            response=new DislikePostResponse();
            Optional.ofNullable(optionalReview.get()).map(Review::getPost).map(Post::getId).ifPresent(response::setPostId);
            Optional.ofNullable(optionalReview.get()).map(Review::getUser).map(User::getId).ifPresent(response::setUserId);
            Optional.ofNullable(reviewRepository.getPostReviewCount(optionalPost.get(),ReviewType.DISLIKE)).ifPresent(response::setNoOfDislikes);
            Optional.ofNullable("Dislike removed").ifPresent(response::setMessage);
            reviewRepository.delete(optionalReview.get());
        }
        else if(optionalPost.isPresent() && optionalUser.isPresent()){
            Review review=new Review();
            Optional.ofNullable(optionalPost.get()).ifPresent(review::setPost);
            Optional.ofNullable(optionalUser.get()).ifPresent(review::setUser);
            Optional.ofNullable(ReviewType.DISLIKE).ifPresent(review::setReviewType);
            Optional.ofNullable(LocalDateTime.now()).ifPresent(review::setReviewDate);

            Review save = reviewRepository.save(review);

            response=new DislikePostResponse();
            Optional.ofNullable(save).map(Review::getPost).map(Post::getId).ifPresent(response::setPostId);
            Optional.ofNullable(save).map(Review::getUser).map(User::getId).ifPresent(response::setUserId);
            Optional.ofNullable(reviewRepository.getPostReviewCount(optionalPost.get(),ReviewType.DISLIKE)).ifPresent(response::setNoOfDislikes);
            Optional.ofNullable("You Disliked this post").ifPresent(response::setMessage);
        }
        return null;
    }
}