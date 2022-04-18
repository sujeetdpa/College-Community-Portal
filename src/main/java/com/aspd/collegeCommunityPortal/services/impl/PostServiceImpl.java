package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.beans.request.*;
import com.aspd.collegeCommunityPortal.beans.response.*;
import com.aspd.collegeCommunityPortal.config.BucketName;
import com.aspd.collegeCommunityPortal.model.*;
import com.aspd.collegeCommunityPortal.repositories.*;
import com.aspd.collegeCommunityPortal.services.AmazonS3Service;
import com.aspd.collegeCommunityPortal.services.LocalStorageService;
import com.aspd.collegeCommunityPortal.services.PostService;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final List<String> imageExtensions=Arrays.asList(ContentType.IMAGE_GIF.getMimeType(),ContentType.IMAGE_JPEG.getMimeType(),ContentType.IMAGE_PNG.getMimeType(),ContentType.IMAGE_BMP.getMimeType());
    private final List<String> documentExtensions=Arrays.asList(MediaType.APPLICATION_PDF_VALUE.toString(),ContentType.TEXT_PLAIN.getMimeType(),".doc",".docx",".wps",".docm",".odt",".rtf",".csv",".ods",".xls",".xlsb",".xlsx",".ppt",".pptx");
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
    private LocalStorageService localStorageService;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private ImageRepository imageRepository;

    @Override
    public PostResponseViewList getAllPost(PostRequest postRequest) {
        Map<Integer,Integer> postLikeCount=null;
        Map<Integer,Integer> postCommentCount=null;
        PostResponseViewList postResponseViewList=new PostResponseViewList();
        Pageable pageable= PageRequest.of(Optional.ofNullable(postRequest.getPageNo()).orElse(0),Optional.ofNullable(postRequest.getMaxItem()).orElse(10), Sort.by(Sort.Direction.DESC,Optional.ofNullable(postRequest.getSortBy()).orElse("creationDate")));
        Page<Post> postPage = postRepository.findAll(pageable);

        if(!postPage.isEmpty()){
            List<PostResponseView> postResponseViews=new ArrayList<>();
            for(Post post:postPage){
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
            postResponseViewList.setTotalNumberOfItems(postPage.getTotalElements());
            postResponseViewList.setPageNo(postPage.getNumber());
            postResponseViewList.setTotalPages(postPage.getTotalPages());
            postResponseViewList.setMaxItems(postPage.getSize());

        }
        else {
            throw new IllegalStateException("Posts not found");
        }
        return postResponseViewList;
    }

    @Override
    public PostResponseView createPost(CreatePostRequest createPostRequest) {
        Post post=new Post();
        post.setTitle(Optional.ofNullable(createPostRequest.getTitle()).orElseThrow(() -> new RuntimeException("Title cannot be null")));
        post.setDescription(Optional.ofNullable(createPostRequest.getDescription()).orElseThrow(()->new RuntimeException("Description cannot be null")));
        Optional.ofNullable(LocalDateTime.now()).ifPresent(post::setCreationDate);
        post.setIsDeleted(false);
        UserPrincipal userPrincipal=(UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        post.setUser(userPrincipal.getUser());
        Post savedPost = postRepository.save(post);

        if (createPostRequest.getImages()!=null && !createPostRequest.getImages().isEmpty()){
            List<Image> imageList = imageRepository.findAllById(createPostRequest.getImages());
            if (imageList==null || imageList.isEmpty()){
                throw new IllegalStateException("Images not uploaded");
            }
            imageList.forEach(image -> image.setPost(savedPost));
            imageRepository.saveAll(imageList);
        }
        if (createPostRequest.getDocuments()!=null && !createPostRequest.getDocuments().isEmpty()){
            List<Document> documentList = documentRepository.findAllById(createPostRequest.getDocuments());
            if (documentList==null || documentList.isEmpty()){
                throw new IllegalStateException("Documents not uploaded");
            }
            documentList.forEach(document -> document.setPost(savedPost));
            documentRepository.saveAll(documentList);
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
        Pageable pageable=PageRequest.of(Optional.ofNullable(request.getPageNo()).orElse(0),Optional.ofNullable(request.getMaxItems()).orElse(15), Sort.by(Sort.Direction.DESC,"creationDate"));
        Page<Post> postPage=null;
        List<PostSearchResponseView> responseViewList=new ArrayList<>();
        if(Optional.ofNullable(request.getTitle()).isPresent()){
            postPage=postRepository.searchPostByTitle(request.getTitle(),pageable);
        }
        // TODO more searches to add here
        if(postPage!=null && !postPage.isEmpty()){
            postPage.stream().filter(post -> !post.getIsDeleted()).forEach(post -> {
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
        Optional.ofNullable(postPage.getTotalElements()).ifPresent(list::setTotalNumberOfItems);
        Optional.ofNullable(postPage.getSize()).ifPresent(list::setMaxItems);
        Optional.ofNullable(postPage.getNumber()).ifPresent(list::setPageNo);
        Optional.ofNullable(responseViewList).ifPresent(list::setPostSearchResponseViews);
        return list;
    }

    @Override
    public DeleteResponseView deletePost(int postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        DeleteResponseView responseView=new DeleteResponseView();
        if (optionalPost.isEmpty() || !optionalPost.isPresent()){
            responseView.setMessage("Invalid delete request");
        }
        else {
            Post post=optionalPost.get();
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!post.getUser().equals(userPrincipal.getUser())){
                responseView.setMessage("Unauthorised Request for user : "+userPrincipal.getUsername());
            }
            else{
                post.setIsDeleted(true);
                postRepository.save(post);
                responseView.setMessage(String.format("Post deleted with Title : %s",post.getTitle()));
            }
        }
        return responseView;
    }

    @Override
    public PostResponseView getPost(int postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if(optionalPost.isPresent() && !optionalPost.get().getIsDeleted()){
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
            Optional.ofNullable(imageRepository.findImageByPost(post)).map(images -> images.stream().map(Image::getId).collect(Collectors.toList())).ifPresent(responseView::setImageIds);
            Optional.ofNullable(documentRepository.findByPost(post)).map(documents -> documents.stream().map(Document::getId).collect(Collectors.toList())).ifPresent(responseView::setDocumentIds);
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
        Pageable pageable=PageRequest.of(Optional.ofNullable(request.getPageNo()).orElse(0),Optional.ofNullable(request.getMaxItem()).orElse(10),Sort.by(Sort.Direction.ASC,"commentDate"));
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
            Optional.ofNullable(comments.getTotalElements()).ifPresent(responseViewList::setTotalNumberOfItems);
            Optional.ofNullable(comments.getNumber()).ifPresent(responseViewList::setPageNo);
            Optional.ofNullable(comments.getSize()).ifPresent(responseViewList::setMaxItems);
            Optional.ofNullable(viewList).ifPresent(responseViewList::setCommentResponseViews);
            return responseViewList;
        }
        return null;
    }

    @Override
    public LikePostResponse likePost(int postId, int userId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        Optional<User> optionalUser = userRepository.findById(userId);
        LikePostResponse response=null;
        if (validatePostDislike(optionalPost.get(),optionalUser.get()).isPresent()){
            response=new LikePostResponse();
            Optional.ofNullable(optionalPost.get()).map(Post::getId).ifPresent(response::setPostId);
            Optional.ofNullable(optionalUser.get()).map(User::getId).ifPresent(response::setUserId);
            Optional.ofNullable(reviewRepository.getPostReviewCount(optionalPost.get(),ReviewType.LIKE)).ifPresent(response::setNoOfLikes);
            Optional.ofNullable("You have already disliked this post").ifPresent(response::setMessage);
        }
        else{
            Optional<Review> optionalReview = validatePostLike(optionalPost.get(), optionalUser.get());
            if (optionalReview.isPresent()) {
                reviewRepository.delete(optionalReview.get());
                response = new LikePostResponse();
                Optional.ofNullable(optionalReview.get()).map(Review::getPost).map(Post::getId).ifPresent(response::setPostId);
                Optional.ofNullable(optionalReview.get()).map(Review::getUser).map(User::getId).ifPresent(response::setUserId);
                Optional.ofNullable(reviewRepository.getPostReviewCount(optionalPost.get(), ReviewType.LIKE)).ifPresent(response::setNoOfLikes);
                Optional.ofNullable("Like removed").ifPresent(response::setMessage);
            }
            else if (optionalPost.isPresent() && optionalUser.isPresent()){
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

        }
        return response;
    }

    private Optional<Review> validatePostDislike(Post post, User user) {
        Optional<Review> optionalReview = reviewRepository.findByPostAndUserAndReviewType(post, user, ReviewType.DISLIKE);
        return optionalReview;
    }
    private Optional<Review> validatePostLike(Post post,User user){
        Optional<Review> optionalReview = reviewRepository.findByPostAndUserAndReviewType(post, user, ReviewType.LIKE);
        return optionalReview;
    }
    @Override
    public DislikePostResponse dislikePost(int postId, int userId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        Optional<User> optionalUser = userRepository.findById(userId);
        DislikePostResponse response=null;
        if (validatePostLike(optionalPost.get(),optionalUser.get()).isPresent()){
            response=new DislikePostResponse();
            Optional.ofNullable(optionalPost.get()).map(Post::getId).ifPresent(response::setPostId);
            Optional.ofNullable(optionalUser.get()).map(User::getId).ifPresent(response::setUserId);
            Optional.ofNullable(reviewRepository.getPostReviewCount(optionalPost.get(),ReviewType.DISLIKE)).ifPresent(response::setNoOfDislikes);
            Optional.ofNullable("You have already liked this post").ifPresent(response::setMessage);
        }
        else {
            Optional<Review> optionalReview = validatePostDislike(optionalPost.get(), optionalUser.get());
            if (optionalReview.isPresent()) {
                response = new DislikePostResponse();
                reviewRepository.delete(optionalReview.get());
                Optional.ofNullable(optionalReview.get()).map(Review::getPost).map(Post::getId).ifPresent(response::setPostId);
                Optional.ofNullable(optionalReview.get()).map(Review::getUser).map(User::getId).ifPresent(response::setUserId);
                Optional.ofNullable(reviewRepository.getPostReviewCount(optionalPost.get(), ReviewType.DISLIKE)).ifPresent(response::setNoOfDislikes);
                Optional.ofNullable("Dislike removed").ifPresent(response::setMessage);

            } else if (optionalPost.isPresent() && optionalUser.isPresent()) {
                Review review = new Review();
                Optional.ofNullable(optionalPost.get()).ifPresent(review::setPost);
                Optional.ofNullable(optionalUser.get()).ifPresent(review::setUser);
                Optional.ofNullable(ReviewType.DISLIKE).ifPresent(review::setReviewType);
                Optional.ofNullable(LocalDateTime.now()).ifPresent(review::setReviewDate);

                Review save = reviewRepository.save(review);

                response = new DislikePostResponse();
                Optional.ofNullable(save).map(Review::getPost).map(Post::getId).ifPresent(response::setPostId);
                Optional.ofNullable(save).map(Review::getUser).map(User::getId).ifPresent(response::setUserId);
                Optional.ofNullable(reviewRepository.getPostReviewCount(optionalPost.get(), ReviewType.DISLIKE)).ifPresent(response::setNoOfDislikes);
                Optional.ofNullable("You Disliked this post").ifPresent(response::setMessage);
            }
        }
        return response;
    }
    @Override
    public List<Integer> uploadImages(List<MultipartFile> files){
        UserPrincipal userPrincipal=(UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (files!=null && !files.isEmpty()){
            List<Image> images=new ArrayList<>();
                files.forEach(image->{
                if (!imageExtensions.contains(image.getContentType())){
                    throw new IllegalStateException("File format supported are: "+imageExtensions);
                }
                Image image1=new Image();
                String filename=LocalDateTime.now().toString().concat("_").concat(image.getOriginalFilename());
                image1.setImageName(filename);
                image1.setUser(userPrincipal.getUser()); //set user after extracting from JWT or Database;
                String path= bucketName.getCcpBucketName().concat("/").concat(userPrincipal.getUsername()).concat("/images");
                image1.setPath(path);
                image1.setUploadDate(LocalDateTime.now());
                Map<String,String> metadata=new HashMap<>();
                metadata.put("Content-Type", image.getContentType());
                metadata.put("Content-Length", String.valueOf(image.getSize()));
                try {
                    Boolean uploaded=localStorageService.uploadFile(path,filename,Optional.ofNullable(metadata),image.getInputStream());
                    if (uploaded){
                        images.add(image1);
                    }
                } catch (IOException e) {
                    throw new IllegalStateException("Error in uploading images");
                }
                });
            List<Image> imageList = imageRepository.saveAll(images);
            return imageList.stream().map(Image::getId).collect(Collectors.toList());
        }
        return null;
    }
    @Override
    public List<Integer> uploadDocuments(List<MultipartFile> files){
        UserPrincipal userPrincipal=(UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (files!=null && !files.isEmpty()){
            List<Document> documents=new ArrayList<>();
            files.forEach(file->{
                if (!documentExtensions.contains(file.getContentType())){
                    throw new IllegalStateException("Document format not supported");
                }
                String filename=LocalDateTime.now().toString().concat("_").concat(file.getOriginalFilename());
                String path=bucketName.getCcpBucketName().concat("/").concat(userPrincipal.getUsername()).concat("/documents");
                Document document=new Document();
                document.setDocumentName(filename);
                document.setUser(userPrincipal.getUser()); //TODO add user
                document.setPath(path);
                document.setUploadDate(LocalDateTime.now());
                Map<String,String> metadata=new HashMap<>();
                metadata.put("Content-Type", file.getContentType());
                metadata.put("Content-Length", String.valueOf(file.getSize()));
                try {
                    Boolean uploaded=localStorageService.uploadFile(path,filename,Optional.ofNullable(metadata),file.getInputStream());
                    if (uploaded){
                        documents.add(document);
                    }
                }
                catch (IOException e){
                    throw new IllegalStateException("Failed to upload image",e);  //TODO implement custom exception
                }
            });
            List<Document> documentList = documentRepository.saveAll(documents);
            return documentList.stream().map(Document::getId).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public byte[] downloadImage(Integer imageId) throws IOException {
        Optional<Image> image = imageRepository.findById(imageId);
        if (image.isPresent()){
            return localStorageService.downloadFile(image.get().getPath(),image.get().getImageName());
        }
        return new byte[0];
    }

    @Override
    public byte[] downloadDocument(Integer documentId) throws IOException {
        Optional<Document> document = documentRepository.findById(documentId);
        if (document.isPresent()){
            return localStorageService.downloadFile(document.get().getPath(),document.get().getDocumentName());
        }
        return new byte[0];
    }
}