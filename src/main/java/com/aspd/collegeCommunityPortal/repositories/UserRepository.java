package com.aspd.collegeCommunityPortal.repositories;

import com.aspd.collegeCommunityPortal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
}
