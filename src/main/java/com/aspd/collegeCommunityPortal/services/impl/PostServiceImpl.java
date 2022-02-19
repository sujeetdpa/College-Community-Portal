package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.data.request.PostRequest;
import com.aspd.collegeCommunityPortal.data.response.PostResponseView;
import com.aspd.collegeCommunityPortal.data.response.PostResponseViewList;
import com.aspd.collegeCommunityPortal.model.Post;
import com.aspd.collegeCommunityPortal.repositories.CommentRepository;
import com.aspd.collegeCommunityPortal.repositories.LikeRepository;
import com.aspd.collegeCommunityPortal.repositories.PostRepository;
import com.aspd.collegeCommunityPortal.repositories.UserRepository;
import com.aspd.collegeCommunityPortal.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;

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
            Optional<Map<Integer, Integer>> postsLikeCount = likeRepository.getPostsLikeCount(postIds);
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
                postResponseView.setTitle(post.getTitle());
                postResponseView.setCreationDate(post.getCreationDate());
                postResponseView.setDescription(post.getDescription());
                Optional.ofNullable(post.getUser().getFirstName().concat(" ").concat(post.getUser().getLastName())).ifPresent(postResponseView::setUser);
                Optional.ofNullable(post.getUser().getId()).ifPresent(postResponseView::setUserId);
                Optional.ofNullable(postLikeCount.get(post.getId())).ifPresent(postResponseView::setNoOfLikes);
                Optional.ofNullable(postCommentCount.get(post.getId())).ifPresent(postResponseView::setNoOfComments);
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
}
