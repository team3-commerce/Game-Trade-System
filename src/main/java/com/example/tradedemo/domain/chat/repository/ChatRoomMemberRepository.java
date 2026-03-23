package com.example.tradedemo.domain.chat.repository;

import com.example.tradedemo.domain.chat.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long>, ChatRoomMemberCustomRepository {

}
