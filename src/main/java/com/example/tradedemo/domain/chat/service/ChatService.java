package com.example.tradedemo.domain.chat.service;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.chat.dto.ChatMessageResponse;
import com.example.tradedemo.domain.chat.dto.ChatRoomResponse;
import com.example.tradedemo.domain.chat.dto.CreateRoomRequest;
import com.example.tradedemo.domain.chat.entity.ChatRoom;
import com.example.tradedemo.domain.chat.entity.ChatRoomMember;
import com.example.tradedemo.domain.chat.enums.ChatRoomMemberRole;
import com.example.tradedemo.domain.chat.repository.ChatMessageRepository;
import com.example.tradedemo.domain.chat.repository.ChatRoomMemberRepository;
import com.example.tradedemo.domain.chat.repository.ChatRoomRepository;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    /** 내가 참여한 전체 채팅방 조회 */
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getMyRooms(String email) {
        return chatRoomMemberRepository.findChatRoomsByMemberEmail(email);
    }

    /** 내가 BUYER인 채팅방 조회 (내가 채팅을 시작한 채팅방) */
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getMyBuyerRooms(String email) {
        return chatRoomMemberRepository.findChatRoomsByMemberEmailAndRole(email, ChatRoomMemberRole.BUYER);
    }

    /** 내가 SELLER인 채팅방 조회 (상대방이 채팅을 시작한 채팅방) */
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getMySellerRooms(String email) {
        return chatRoomMemberRepository.findChatRoomsByMemberEmailAndRole(email, ChatRoomMemberRole.SELLER);
    }

    /**
     * 채팅방 생성 (상품 탭 "판매자와 채팅하기")
     * 1. 같은 구매자 + 같은 상품 중복 채팅방 있으면 예외
     * 2. 채팅방 생성 ("[상품명] 판매자닉네임")
     * 3. 구매자 = BUYER, 판매자 = SELLER 참여 등록
     */
    public ChatRoomResponse createRoom(MarketListing marketListing, Member buyer, Member seller) {

        // 1. 중복 채팅방 존재하는지 확인
        boolean alreadyExists = chatRoomMemberRepository
                .findRoomByBuyerAndListing(buyer.getEmail(), marketListing.getId())
                .isPresent();

        if (alreadyExists) {
            throw new ServiceException(ErrorEnum.ERR_CHAT_ROOM_LISTING_ALREADY_EXISTS);
        }

        // 2. 채팅방 생성
        String roomName = "[" + marketListing.getItemName() + "] " + seller.getNickname();
        ChatRoom room = saveRoom(roomName, marketListing);

        // 3. 구매자, 판매자를 채팅방 참여자로 등록
        chatRoomMemberRepository.save(ChatRoomMember.create(room, buyer, ChatRoomMemberRole.BUYER));
        chatRoomMemberRepository.save(ChatRoomMember.create(room, seller, ChatRoomMemberRole.SELLER));

        log.info("[ChatRoom] 채팅방 생성 완료 - roomId: {}, buyer: {}, seller: {}",
                room.getId(), buyer.getEmail(), seller.getEmail());

        return ChatRoomResponse.of(room, seller.getNickname(), ChatRoomMemberRole.BUYER.name(), marketListing.getItemName());
    }

    /**
     * 채팅방 저장
     */
    @Transactional
    public ChatRoom saveRoom(String name, MarketListing marketListing) {
        return chatRoomRepository.save(ChatRoom.create(name, marketListing));
    }

    /**
     * 채팅방별 커서 기반 메시지 조회
     *
     * @param roomId        채팅방 ID
     * @param lastMessageId null → 최신 n개 (최초 입장 / 재연결 시 최신부터 재조회)
     *                      값 있음 → 해당 ID 이전 n개 (이전 메시지 더 보기)
     * @param size          페이지 크기 (기본 30)
     *
     * 재연결 복구 전략:
     *   lastMessageId=null로 최신 n개를 다시 조회하면
     *   끊긴 동안 누락된 메시지를 포함해 최신 상태로 복구 가능
     */
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessagesByRoom(Long roomId, Long lastMessageId, int size) {

        return chatMessageRepository.findByRoomWithCursor(roomId, lastMessageId, size);
    }

    /**
     * 채팅방의 상품 상태 조회
     * 반환값으로 입력창 활성/비활성 결정
     * 시스템 메시지 전송은 이 메서드가 아닌 ChatMessageController.send()에서 처리
     */
    @Transactional(readOnly = true)
    public MarketListingStatus getListingStatus(Long roomId) {
        ChatRoom room = chatRoomRepository.findByIdWithListing(roomId)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_CHAT_ROOM_NOT_FOUND));

        if (room.getMarketListing() == null) {
            throw new ServiceException(ErrorEnum.ERR_MARKET_LISTING_NOT_FOUND);
        }

        return room.getMarketListing().getStatus();
    }

}
