package com.example.tradedemo.domain.chat.service;

import com.example.tradedemo.domain.chat.entity.ChatRoom;
import com.example.tradedemo.domain.chat.entity.ChatRoomMember;
import com.example.tradedemo.domain.chat.repository.ChatRoomMemberRepository;
import com.example.tradedemo.domain.members.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMemberService {

    private final ChatRoomMemberRepository chatRoomMemberRepository;

    @Transactional
    public void registerMember(ChatRoom room, Member member) {
        if (chatRoomMemberRepository.existsByChatRoomAndMember(room, member)) {
            log.info("[ChatMember] 이미 참여 중 (무시) - roomId: {}, memberId: {}",
                    room.getId(), member.getId());
            return;
        }
        chatRoomMemberRepository.save(new ChatRoomMember(room, member));
        log.info("[ChatMember] 참여 등록 완료 - roomId: {}, memberId: {}",
                room.getId(), member.getId());
    }
}
