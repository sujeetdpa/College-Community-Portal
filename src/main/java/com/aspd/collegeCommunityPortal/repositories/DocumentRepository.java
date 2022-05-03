package com.aspd.collegeCommunityPortal.repositories;

import com.aspd.collegeCommunityPortal.model.Document;
import com.aspd.collegeCommunityPortal.model.Post;
import com.aspd.collegeCommunityPortal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    List<Document> findByPost(Post post);

    Page<Document> findByUser(User user, Pageable pageable);

    @Query(value = "select count(d) from Document d where d.user = ?1")
    Integer countByUser(User user);

    void deleteByUser(User user);
}
