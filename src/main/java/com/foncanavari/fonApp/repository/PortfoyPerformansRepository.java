package com.foncanavari.fonApp.repository;

import com.foncanavari.fonApp.model.PortFon;
import com.foncanavari.fonApp.model.PortfoyPerformans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PortfoyPerformansRepository extends JpaRepository<PortfoyPerformans, Integer> {

    @Query(value = "select * from portfoy_performans where portfoy_id = ?1 order by id desc limit 30", nativeQuery = true)
    List<PortfoyPerformans> findAllByPortfoyId(int portfoy_id);

    @Query(value = "select * from portfoy_performans where tarih = ?1 and portfoy_id = ?2", nativeQuery = true)
    PortfoyPerformans getOneByTarihAndId(String tarih, int portfoy_id);


}
