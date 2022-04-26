package com.aspd.collegeCommunityPortal.repositories;

import com.aspd.collegeCommunityPortal.model.Post;
import com.aspd.collegeCommunityPortal.model.Review;
import com.aspd.collegeCommunityPortal.model.ReviewType;
import com.aspd.collegeCommunityPortal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Integer> {

    @Query(value = "select count(r.user) from Review r where r.post= ?1 and r.reviewType= ?2")
    Integer getPostReviewCount(Post post,ReviewType reviewType);

    Optional<Review> findByPostAndUserAndReviewType(Post post, User user,ReviewType reviewType);
    @Query(value = "select count(r) from Review r where r.user = ?1 and r.reviewType= ?2")
    Integer countByUser(User user,ReviewType reviewType);
    @Query(value = "select count(r) from Review r where r.post in ?1 and r.reviewType= ?2")
    Integer countByPosts(List<Post> posts,ReviewType reviewType);

    @Query(value = "select count(r) from Review r where r.reviewType= ?1")
    Integer countByReviewType(ReviewType reviewType);
}
