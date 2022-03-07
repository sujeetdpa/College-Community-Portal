package com.aspd.collegeCommunityPortal.repositories;

import com.aspd.collegeCommunityPortal.model.Comment;
import com.aspd.collegeCommunityPortal.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {
    @Query(value = "select post,count(user) from Comment where post in :post group by post")
    Optional<Map<Integer,Integer>> getPostsCommentCount(@Param("post") List<Post> post);

    @Query(value = "select count(user) from Comment where post= ?1")
    Integer getPostCommentCount(Post post);

    Page<Comment> findByPost(Post post, Pageable pageable);
}
