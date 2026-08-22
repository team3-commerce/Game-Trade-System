package com.example.tradedemo.domain.chat.repository;

import com.example.tradedemo.domain.chat.dto.ChatRoomResponse;
import com.example.tradedemo.domain.chat.entity.ChatRoom;
import com.example.tradedemo.domain.chat.entity.QChatRoomMember;
import com.example.tradedemo.domain.chat.enums.ChatRoomMemberRole;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.example.tradedemo.domain.chat.entity.QChatRoomMember.chatRoomMember;

@RequiredArgsConstructor
public class ChatRoomMemberCustomRepositoryImpl implements ChatRoomMemberCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatRoomResponse> findChatRoomsByMemberEmail(String email) {
        return findRooms(email, null);
    }

    @Override
    public List<ChatRoomResponse> findChatRoomsByMemberEmailAndRole(String email, ChatRoomMemberRole role) {
        return findRooms(email, role);
    }

    // 같은 채팅방의 상대방 정보를 한 번에 조회
    private List<ChatRoomResponse> findRooms(String email, ChatRoomMemberRole role) {
        // 같은 채팅방의 상대방 행을 위해 작성
        var other = new QChatRoomMember("other");

        return queryFactory
                .select(Projections.constructor(ChatRoomResponse.class,
                        chatRoomMember.chatRoom.id,
                        chatRoomMember.chatRoom.name,
                        other.member.nickname.append("와의 채팅"),
                        chatRoomMember.role.stringValue(),
                        chatRoomMember.chatRoom.marketListing.id,
                        chatRoomMember.chatRoom.marketListing.itemName,
                        chatRoomMember.chatRoom.createdAt
                ))
                .from(chatRoomMember)
                .join(chatRoomMember.chatRoom)
                .leftJoin(chatRoomMember.chatRoom.marketListing)
                .join(other).on(
                        other.chatRoom.eq(chatRoomMember.chatRoom)    // 같은 채팅방이면서
                                .and(other.member.email.ne(email))    // 내 이메일이 아닌 상대방 행 찾기
                )
                .where(
                        chatRoomMember.member.email.eq(email),
                        role != null ? chatRoomMember.role.eq(role) : null
                )
                .fetch();
    }

    // 같은 구매자 + 같은 상품 채팅방 중복 체크
    @Override
    public Optional<ChatRoom> findRoomByBuyerAndListing(String buyerEmail, Long listingId) {
        ChatRoom result = queryFactory
                .select(chatRoomMember.chatRoom)
                .from(chatRoomMember)
                .where(
                        chatRoomMember.member.email.eq(buyerEmail),
                        chatRoomMember.chatRoom.marketListing.id.eq(listingId)
                )
                .fetchFirst(); // 존재 여부만 확인

        return Optional.ofNullable(result);
    }
}
