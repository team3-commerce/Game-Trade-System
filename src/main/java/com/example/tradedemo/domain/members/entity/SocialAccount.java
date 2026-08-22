package com.example.tradedemo.domain.members.entity;

import com.example.tradedemo.common.entity.Base;
import com.example.tradedemo.domain.members.enums.SocialProvider;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "social_accounts", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialProvider provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(nullable = false)
    private LocalDateTime linkedAt;

    private SocialAccount(Member member, SocialProvider provider, String providerId) {
        this.member = member;
        this.provider = provider;
        this.providerId = providerId;
        this.linkedAt = LocalDateTime.now();
    }

    public static SocialAccount create(Member member, SocialProvider provider, String providerId) {
        return new SocialAccount(member, provider, providerId);
    }
}
