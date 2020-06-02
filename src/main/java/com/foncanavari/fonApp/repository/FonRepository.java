package com.foncanavari.fonApp.repository;

import com.foncanavari.fonApp.controller.FonController;
import com.foncanavari.fonApp.model.Category;
import com.foncanavari.fonApp.model.Fon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FonRepository extends JpaRepository<Fon, Integer> {

    @Query(value = "SELECT * FROM Fon",
            countQuery = "SELECT count(*) FROM Fon",
            nativeQuery = true)
    Page<Fon> findAllPage(Pageable pageable);

    @Query(value = "select * from Fon where kod = ?1", nativeQuery = true)
    Fon findByKodu(String kod);

    @Query(value = "select * from Fon where kod like %?1% or ad like %?1% limit 10", nativeQuery = true)
    List<Fon> findAllLike(String search);

    @Query(value = "select fn1.id,fn1.category as name, (select substring(CAST(avg(fn2.gunluk_artis) as char),1,5) from fon fn2 where fn2.category = fn1.category) as gunluk_ort_artis from fon fn1 group by fn1.category", nativeQuery = true)
    List getArtislarGroupByCategory();


}