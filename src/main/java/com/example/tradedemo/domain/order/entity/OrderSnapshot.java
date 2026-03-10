package com.example.tradedemo.domain.order.entity;

import com.example.tradedemo.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 시점의 상품 정보를 보관하는 스냅샷 엔티티
 */
@Entity
@Getter
@Table(name = "order_snapshots")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderSnapshot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 거래 당시 가격
     */
    @Column(nullable = false)
    private BigDecimal price;

    /**
     * 상품 이름
     */
    @Column(nullable = false)
    private String productName;

    /**
     * 상품 수량
     */
    @Column(nullable = false)
    private Long productQuantity;

    /**
     * 주문
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders ordersId;

    /**
     * 정적 팩토리 메서드
     */
    public static OrderSnapshot create(BigDecimal price, String productName, Long productQuantity, Orders order) {
        OrderSnapshot orderSnapshot = new OrderSnapshot();
        orderSnapshot.price = price;
        orderSnapshot.productName = productName;
        orderSnapshot.productQuantity = productQuantity;
        orderSnapshot.ordersId = order;

        return orderSnapshot;
    }
}
