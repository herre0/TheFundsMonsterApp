package com.foncanavari.fonApp.repository;

import com.foncanavari.fonApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User,Integer> {

    @Query(value = "select user_type from kullanici where username=?1 and pass = ?2",nativeQuery = true)
    String findByUser(String email, String pass);

    @Query(value = "select * from kullanici where email=?1",nativeQuery = true)
    User findUserByEmail(String email);
}
