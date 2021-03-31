package com.foncanavari.fonApp.repository;

import com.foncanavari.fonApp.model.Fon;
import com.foncanavari.fonApp.model.PortFon;
import com.foncanavari.fonApp.model.Portfoy;
import com.foncanavari.fonApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PortfonRepository extends JpaRepository<PortFon, Integer> {

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "delete from portfoy_fonlar where portfoy_id=?1",nativeQuery = true)
    void deleteportfoyfonByPortfoyId(int id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "delete from kullanici_portfoyler where portfoyler_id=?1",nativeQuery = true)
    void  deleteuserportfoyByPortfoyId(int id);

    @Query(value = "select * from portfon where fon_kod = ?1", nativeQuery = true)
    List<PortFon> findAllByFonkod(String kod);

    @Query(value = "select * from portfon where id = ?1", nativeQuery = true)
    List<PortFon> findAllByID(int id);

    @Query(value = "select * from portfon where degeri is null or agirlik is null or alis_maliyeti is null or gunluk_getiri_tl is null or adet is null or toplam_getiri_tl is null", nativeQuery = true)
    List<PortFon> getNullListNotUpdated();

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "delete from portfoy_fonlar where fonlar_id=?1",nativeQuery = true)
    void  deletePortfonbyFonId(int id);
}
