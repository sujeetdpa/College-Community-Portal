package com.aspd.collegeCommunityPortal.repositories;

import com.aspd.collegeCommunityPortal.model.Comment;
import com.aspd.collegeCommunityPortal.model.Post;
import com.aspd.collegeCommunityPortal.model.User;
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
    @Query(value = "select c.post_id,count(c.user_id) from Comment c where c.post_id in ?1 group by c.post_id",nativeQuery = true)
    Optional<Map<Integer,Integer>> getPostsCommentCount(List<Integer> post);

    @Query(value = "select count(c.user) from Comment c where c.post= ?1")
    Integer getPostCommentCount(Post post);

    Page<Comment> findByPost(Post post, Pageable pageable);
    @Query(value = "select count(c) from Comment c where c.user = ?1")
    Integer countByUser(User user);
    @Query(value = "select count(c) from Comment c where c.post in ?1")
    Integer countByPosts(List<Post> posts);
}
