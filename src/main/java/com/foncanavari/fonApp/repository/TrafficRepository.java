package com.foncanavari.fonApp.repository;

import com.foncanavari.fonApp.model.Traffic;
import com.foncanavari.fonApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface TrafficRepository extends JpaRepository<Traffic, Integer> {


    @Query(value = "select * from traffic where ip_adress=?1",nativeQuery = true)
    Traffic findTrafficByIpAdress(String ip);
}
