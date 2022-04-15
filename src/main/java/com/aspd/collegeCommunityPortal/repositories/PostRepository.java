package com.aspd.collegeCommunityPortal.repositories;

import com.aspd.collegeCommunityPortal.model.Post;
import com.aspd.collegeCommunityPortal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer> {

    @Query(value = "select p from Post p where p.title like %?1% and p.isDeleted= false")
    Page<Post> searchPostByTitle(String title, Pageable pageable);

    Page<Post> findPostByUser(User user,Pageable pageable);
}
