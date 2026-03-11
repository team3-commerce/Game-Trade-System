package com.example.tradedemo.domain.members.entity;

public enum MemberStatus {
    ACTIVE, // 활성화
    INACTIVE, // 비활성화 (30일 미접속. 관리자 정지)
    WITHDRAWN // 탈퇴 (소프트 삭제 상태)
}
