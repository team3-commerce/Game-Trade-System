package com.example.tradedemo.domain.coupon.repository;

import com.example.tradedemo.domain.coupon.entity.CouponHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponHistoryRepository extends JpaRepository<CouponHistory, Long> {}
