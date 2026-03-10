package com.example.tradedemo.domain.order.repository;

import com.example.tradedemo.domain.order.entity.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // 내 구매 내역 조회
    List<Order> findByBuyerId(Long buyerId);

    // 내 판매 내역 조회
    List<Order> findBySellerId(Long sellerId);
}
