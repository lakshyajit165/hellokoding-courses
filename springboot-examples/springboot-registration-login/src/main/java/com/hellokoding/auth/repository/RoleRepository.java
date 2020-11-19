package com.hellokoding.auth.repository;

import com.hellokoding.auth.model.Role;
import com.hellokoding.auth.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>{
    Optional<Role> findByName(RoleName roleName);
}
