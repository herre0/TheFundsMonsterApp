package com.foncanavari.fonApp.repository;

import com.foncanavari.fonApp.model.ERole;
import com.foncanavari.fonApp.model.Fon;
import com.foncanavari.fonApp.model.Portfoy;
import com.foncanavari.fonApp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfoyRepository extends JpaRepository<Portfoy, Integer> {

    @Query(value = "select * from portfoy where user_id = ?1",nativeQuery = true)
    Portfoy findPortfoyByUser_id(int user_id);

    @Query(value = "select * from portfoy where user_id = ?1 and adi = ?2",nativeQuery = true)
    Portfoy findPortfoyByUseridandPname(int user_id, String pname);

    @Query(value = "select * from portfoy p, portfoy_fonlar pf where p.id = pf.portfoy_id and pf.fonlar_id = ?1",nativeQuery = true)
    Portfoy findPortfoyByPortfonId(int portfon_id);

    @Modifying
    @Transactional
    @Query(value = "update portfoy set portfoy_degeri = ?1, gunluk_getiri_tl = ?2, gunluk_getiri_yuzde = ?3, toplam_getiri_tl = ?4, toplam_getiri_yuzde = ?5 where id = ?6",nativeQuery = true)
    void updatePortfoy(Double deger, Double gunluk_tl, Double gunluk_yuzde, Double toplam_tl, Double toplam_yuzde, int id);


}
