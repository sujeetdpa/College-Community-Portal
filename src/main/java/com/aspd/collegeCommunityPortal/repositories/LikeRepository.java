package com.aspd.collegeCommunityPortal.repositories;

import com.aspd.collegeCommunityPortal.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like,Integer> {

    @Query(value = "select post,count(user) from like where post in :postIds group by post")
    public Optional<Map<Integer,Integer>> getPostsLikeCount(@Param("postIds") List<Integer> postIds);
}
