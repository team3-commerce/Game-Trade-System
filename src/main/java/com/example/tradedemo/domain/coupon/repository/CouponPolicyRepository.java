package com.example.tradedemo.domain.coupon.repository;

import com.example.tradedemo.domain.coupon.entity.CouponPolicy;
import com.example.tradedemo.domain.coupon.enums.IssueType;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long>, CouponPolicyCustomRepository {

    boolean existsByName(String name);

    // 회원가입 시 자동 발급할 정책 조회
    Optional<CouponPolicy> findByIssueType(IssueType issueType);

    // AUTO_SIGNUP 중복 생성 방지용
    boolean existsByIssueType(IssueType issueType);

    // FIRST_COME 단건 조회
    Optional<CouponPolicy> findByIdAndIssueType(Long id, IssueType issueType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cp FROM CouponPolicy cp WHERE cp.id = :id AND cp.issueType = :issueType")
    Optional<CouponPolicy> findByIdAndIssueTypeWithLock(Long id, IssueType issueType);
}
