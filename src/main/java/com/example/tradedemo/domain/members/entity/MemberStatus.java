package com.example.tradedemo.domain.members.entity;

public enum MemberStatus {
    ACTIVE, // 활성화
    INACTIVE_DORMANT, // 미접속 비활성화
    INACTIVE_SUSPENDED, // 관리자에 의한 정지 비활성화
    WITHDRAWN // 탈퇴 (소프트 삭제 상태)
}
