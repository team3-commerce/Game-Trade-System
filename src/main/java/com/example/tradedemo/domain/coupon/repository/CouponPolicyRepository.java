package com.example.tradedemo.domain.coupon.repository;

import com.example.tradedemo.domain.coupon.entity.CouponPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long> {}
