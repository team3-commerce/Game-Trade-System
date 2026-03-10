package com.example.tradedemo.domain.members.entity;

import com.example.tradedemo.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberItems extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
