package com.tw.heima.repository;

import com.tw.heima.repository.entity.TravelContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TravelContractRepository extends JpaRepository<TravelContractEntity, Integer> {
    Optional<TravelContractEntity> findByCid(String cid);
}