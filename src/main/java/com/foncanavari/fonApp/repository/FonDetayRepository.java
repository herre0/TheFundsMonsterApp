package com.foncanavari.fonApp.repository;

import com.foncanavari.fonApp.model.Fon;
import com.foncanavari.fonApp.model.FonDetay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FonDetayRepository extends JpaRepository<FonDetay, Integer> {

    @Query(value = "select count(*) from FonDetay where fon_kod = ?1",nativeQuery = true)
    int countByKod(String kod);

    @Query(value = "select * from FonDetay where fon_kod = ?1",nativeQuery = true)
    FonDetay getByKod(String kod);

    @Query(value = "select * from FonDetay where fon_kod like %?1% or fon_ad like %?1%",nativeQuery = true)
    List<FonDetay> findAllLike(String search);

    @Query(value = "select * from FonDetay where category = ?1",nativeQuery = true)
    List<FonDetay> findByCategory(String category);

    @Query(value = "select g_tarih from FonDetay where fon_kod = ?1",nativeQuery = true)
    String getUpdatedDate(String kod);
}
