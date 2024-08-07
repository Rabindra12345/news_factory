package com.pironews.piropironews.repository;


import com.pironews.piropironews.model.ERole;
import com.pironews.piropironews.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}