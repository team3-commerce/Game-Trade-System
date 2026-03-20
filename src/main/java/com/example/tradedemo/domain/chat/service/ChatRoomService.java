package com.example.tradedemo.domain.chat.service;

import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.chat.dto.ChatMessageResponse;
import com.example.tradedemo.domain.chat.dto.ChatRoomResponse;
import com.example.tradedemo.domain.chat.dto.CreateRoomRequest;
import com.example.tradedemo.domain.chat.dto.MemberInfo;
import com.example.tradedemo.domain.chat.entity.ChatRoom;
import com.example.tradedemo.domain.chat.repository.ChatMessageRepository;
import com.example.tradedemo.domain.chat.repository.ChatRoomMemberRepository;
import com.example.tradedemo.domain.chat.repository.ChatRoomRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMemberService chatMemberService;

    // 채팅방 목록 조회

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getMyRooms(String email) {
        return chatRoomMemberRepository.findChatRoomsByMemberEmail(email)
                .stream()
                .map(room -> {
                    // 해당 채팅방에서 나를 제외한 상대방 조회
                    List<Member> others = chatRoomMemberRepository.findOtherMembers(room, email);
                    // 1:1 채팅이므로 첫 번째 상대방의 닉네임으로 displayName 생성
                    String otherNickname = others.get(0).getNickname();
                    return ChatRoomResponse.of(room, otherNickname);
                })
                .toList();
    }

    // 채팅방 생성

    public ChatRoomResponse createRoom(CreateRoomRequest request, String creatorEmail) {

        // 1:1 로 제한 (나 자신 제외, 중복 제거 후)
        List<String> validInvitees = request.inviteeEmails() == null ? List.of() :
                request.inviteeEmails().stream()
                        .filter(e -> e != null && !e.isBlank() && !e.equals(creatorEmail))
                        .distinct()
                        .toList();

        if (validInvitees.size() != 1) {
            throw new IllegalArgumentException("1:1 채팅방은 대화 상대를 1명만 선택해야 합니다.");
        }

        String inviteeEmail = validInvitees.get(0);

        // 중복 채팅방 체크 — 두 멤버가 이미 함께 속한 채팅방이 있으면 생성 불가
        chatRoomMemberRepository.findCommonRoom(creatorEmail, inviteeEmail).ifPresent(existing -> {
            Member invitee = memberRepository.findByEmail(inviteeEmail)
                    .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));
            throw new IllegalStateException(
                    invitee.getNickname() + "님과의 채팅방이 이미 존재합니다.");
        });

        // 채팅방 이름 자동 생성 — "{상대방 닉네임}과의 채팅"
        Member invitee = memberRepository.findByEmail(inviteeEmail)
                .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));
        String roomName = invitee.getNickname() + "과의 채팅";

        // 1단계: ChatRoom 독립 트랜잭션으로 저장 후 즉시 커밋
        ChatRoom room = saveRoom(roomName);

        // 2단계: 생성자 + 상대방 참여 등록
        List.of(creatorEmail, inviteeEmail).forEach(email ->
                memberRepository.findByEmail(email).ifPresent(member ->
                        chatMemberService.registerMember(room, member)
                )
        );

        return ChatRoomResponse.from(room);
    }

    @Transactional
    public ChatRoom saveRoom(String name) {
        return chatRoomRepository.save(new ChatRoom(name));
    }

    // 초대 가능 회원 목록(회원 전체 조회)

    @Transactional(readOnly = true)
    public List<MemberInfo> getInvitableMembers(String myEmail) {
        return memberRepository.findAll().stream()
                .filter(m -> !m.getEmail().equals(myEmail))
                .map(m -> new MemberInfo(m.getEmail(), m.getNickname()))
                .toList();
    }

    // 메시지 조회

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getRecentMessages(int size) {
        return chatMessageRepository.findRecentMessages(PageRequest.of(0, size))
                .stream()
                .map(ChatMessageResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessagesBefore(Long lastMessageId, int size) {
        return chatMessageRepository.findMessagesBefore(lastMessageId, PageRequest.of(0, size))
                .stream()
                .map(ChatMessageResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessagesByRoom(Long roomId, int size) {
        return chatMessageRepository.findRecentByRoom(roomId, PageRequest.of(0, size))
                .stream()
                .map(ChatMessageResponse::new)
                .toList();
    }

}
