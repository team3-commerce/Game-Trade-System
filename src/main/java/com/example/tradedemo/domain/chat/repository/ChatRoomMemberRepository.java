package com.example.tradedemo.domain.chat.repository;

import com.example.tradedemo.domain.chat.entity.ChatRoom;
import com.example.tradedemo.domain.chat.entity.ChatRoomMember;
import com.example.tradedemo.domain.members.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    // 특정 회원이 참여한 채팅방 목록 조회
    @Query("SELECT crm.chatRoom FROM ChatRoomMember crm WHERE crm.member.email = :email")
    List<ChatRoom> findChatRoomsByMemberEmail(@Param("email") String email);

    // 이미 참여 중인지 체크 (중복 참여 방지)
    boolean existsByChatRoomAndMember(ChatRoom chatRoom, Member member);

    // 두 멤버가 함께 참여한 1:1 채팅방 조회
    // → 같은 채팅방에 두 멤버가 모두 속해 있는 경우를 찾음
    @Query("""
        SELECT crm1.chatRoom
        FROM ChatRoomMember crm1
        JOIN ChatRoomMember crm2 ON crm1.chatRoom = crm2.chatRoom
        WHERE crm1.member.email = :email1
          AND crm2.member.email = :email2
        """)
    Optional<ChatRoom> findCommonRoom(
            @Param("email1") String email1,
            @Param("email2") String email2);

    // 특정 채팅방에서 나를 제외한 상대방 조회
    // → 1:1 채팅방에서 "상대방 닉네임으로 채팅방 이름 표시"에 사용
    @Query("""
        SELECT crm.member
        FROM ChatRoomMember crm
        WHERE crm.chatRoom = :chatRoom
          AND crm.member.email != :myEmail
        """)
    List<Member> findOtherMembers(
            @Param("chatRoom") ChatRoom chatRoom,
            @Param("myEmail") String myEmail);
}
