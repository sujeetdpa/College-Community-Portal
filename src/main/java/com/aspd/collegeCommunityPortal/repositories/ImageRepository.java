package com.aspd.collegeCommunityPortal.repositories;

import com.aspd.collegeCommunityPortal.model.Image;
import com.aspd.collegeCommunityPortal.model.Post;
import com.aspd.collegeCommunityPortal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image,Integer> {

    List<Image> findImageByPost(Post post);
    List<Image> findImageByUser(User user);
}
