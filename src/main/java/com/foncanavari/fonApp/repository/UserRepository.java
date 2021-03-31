package com.foncanavari.fonApp.repository;

import com.foncanavari.fonApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User,Integer> {

    @Query(value = "select * from kullanici where username=?1",nativeQuery = true)
    User findUserByUsername(String username);

    Boolean existsByUsername(String username);

    @Query(value = "select count(*) - 13 from kullanici",nativeQuery = true)
    int getKullaniciSayisi();

    @Query(value = "select count(*) from portfoy",nativeQuery = true)
    int getPortfoySayisi();

    @Query(value = "select count(*) from kullanici where son_giris like ?1%",nativeQuery = true)
    int getgirisSayisi(String tarih);
}
