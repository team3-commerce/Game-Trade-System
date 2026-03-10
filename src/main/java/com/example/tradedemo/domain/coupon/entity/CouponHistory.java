package com.example.tradedemo.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
