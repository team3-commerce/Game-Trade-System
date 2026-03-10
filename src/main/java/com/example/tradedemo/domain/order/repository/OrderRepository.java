package com.example.tradedemo.domain.order.repository;

import com.example.tradedemo.domain.order.entity.Orders;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    // 내 구매 내역 조회
    List<Orders> findByBuyerId(Long buyerId);

    // 내 판매 내역 조회
    List<Orders> findBySellerId(Long sellerId);
}
