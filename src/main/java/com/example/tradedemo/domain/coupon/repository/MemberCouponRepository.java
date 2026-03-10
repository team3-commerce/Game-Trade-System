package com.example.tradedemo.domain.coupon.repository;

import com.example.tradedemo.domain.coupon.entity.MemberCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {}
