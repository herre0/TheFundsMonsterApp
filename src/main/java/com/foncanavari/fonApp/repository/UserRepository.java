package com.foncanavari.fonApp.repository;

import com.foncanavari.fonApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User,Integer> {

    @Query(value = "select * from kullanici where username=?1",nativeQuery = true)
    User findUserByUsername(String username);

    Boolean existsByUsername(String username);

}
