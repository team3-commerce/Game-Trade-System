package com.example.tradedemo.domain.pending.repository;

import com.example.tradedemo.domain.pending.entity.PendingAsset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingRepository extends JpaRepository<PendingAsset,Long> {

}
