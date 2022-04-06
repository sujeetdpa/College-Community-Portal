package com.aspd.collegeCommunityPortal.repositories;

import com.aspd.collegeCommunityPortal.model.Document;
import com.aspd.collegeCommunityPortal.model.Post;
import com.aspd.collegeCommunityPortal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document,Integer> {

    List<Document> findByPost(Post post);
    List<Document> findByUser(User user);
}
