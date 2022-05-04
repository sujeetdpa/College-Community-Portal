package com.aspd.collegeCommunityPortal.repositories;

import com.aspd.collegeCommunityPortal.model.Role;
import com.aspd.collegeCommunityPortal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByMobileNo(String mobileNo);

    Optional<User> findByUniversityId(String universityId);

    @Query(value = "select count(u) from User u INNER JOIN u.roles r where r= ?1")
    Integer countByRole(Role role);
}
