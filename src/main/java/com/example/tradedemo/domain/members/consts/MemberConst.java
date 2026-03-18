package com.example.tradedemo.domain.members.consts;

public final class MemberConst {

    private MemberConst() {}

    // 상태 변경 사유
    public static final String REASON_SIGNUP = "신규 회원 가입";
    public static final String REASON_WITHDRAWAL = "사용자 요청에 의한 회원 탈퇴";
    public static final String REASON_DORMANT = "30일 이상 미접속으로 인한 휴면 전환";
    public static final String REASON_ACTIVATE = "계정 활동 재개";
}
