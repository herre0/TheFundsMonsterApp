package com.foncanavari.fonApp.repository;

import com.foncanavari.fonApp.model.ERole;
import com.foncanavari.fonApp.model.Role;
import com.foncanavari.fonApp.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}
