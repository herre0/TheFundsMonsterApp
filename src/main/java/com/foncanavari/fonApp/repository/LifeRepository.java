package com.foncanavari.fonApp.repository;

import com.foncanavari.fonApp.model.Life;
import com.foncanavari.fonApp.model.PortFon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LifeRepository extends JpaRepository<Life, Integer> {

    @Query(value = "select giris_sayisi from life where tarih = ?1", nativeQuery = true)
    int getSayiByDate(String date);

    @Query(value = "select * from life where tarih = ?1", nativeQuery = true)
    Life getLifeByDate(String date);

}
