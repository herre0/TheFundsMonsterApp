package com.foncanavari.fonApp.repository;

import com.foncanavari.fonApp.model.PortfoyGecmis;
import com.foncanavari.fonApp.model.PortfoyPerformans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PortfoyGecmisRepository extends JpaRepository<PortfoyGecmis, Integer> {


    @Query(value = "select * from portfoy_gecmis where portfoy_id = ?1 order by id desc", nativeQuery = true)
    List<PortfoyGecmis> findGecmisListByPortfoyId(int portfoy_id);
}
