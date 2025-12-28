package com.pironews.news_ims.repository;


import com.pironews.news_ims.model.ERole;
import com.pironews.news_ims.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}