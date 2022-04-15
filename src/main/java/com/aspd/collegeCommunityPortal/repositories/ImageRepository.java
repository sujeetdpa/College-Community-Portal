package com.aspd.collegeCommunityPortal.repositories;

import com.aspd.collegeCommunityPortal.model.Image;
import com.aspd.collegeCommunityPortal.model.Post;
import com.aspd.collegeCommunityPortal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image,Integer> {

    List<Image> findImageByPost(Post post);
    Page<Image> findImageByUser(User user, Pageable pageable);
}
