package com.example.tradedemo.domain.members.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 엔티티
 *
 */
@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    private Member(String email, String password, MemberRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static Member create(String email, String password, MemberRole role) {
        return new Member(email, password, role);
    }
}
