package com.example.tradedemo.domain.chat.controller;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.domain.chat.dto.ChatMessageResponse;
import com.example.tradedemo.domain.chat.dto.ChatRoomResponse;
import com.example.tradedemo.domain.chat.dto.CreateRoomRequest;
import com.example.tradedemo.domain.chat.dto.MemberInfo;
import com.example.tradedemo.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatRoomService chatRoomService;

    /** 내가 참여한 채팅방 목록 조회 */
    @GetMapping("/rooms")
    public List<ChatRoomResponse> getMyRooms(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return chatRoomService.getMyRooms(principalDetails.getEmail());
    }

    /** 채팅방 생성 + 초대한 사람들 자동 참여 */
    @PostMapping("/rooms")
    public ChatRoomResponse createRoom(
            @RequestBody CreateRoomRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return chatRoomService.createRoom(request, principalDetails.getEmail());
    }

    /** 초대 가능한 회원 목록 조회 (나 자신 제외) */
    @GetMapping("/rooms/invitable-members")
    public List<MemberInfo> getInvitableMembers(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return chatRoomService.getInvitableMembers(principalDetails.getEmail());
    }

    // 메시지 조회

    /** 전체 최근 메시지 조회 */
    @GetMapping("/messages")
    public List<ChatMessageResponse> getMessages(
            @RequestParam(defaultValue = "50") int size) {
        return chatRoomService.getRecentMessages(size);
    }

    /** 커서 기반 메시지 조회 (마지막으로 읽은 messageId 이전) */
    @GetMapping("/messages/before/{id}")
    public List<ChatMessageResponse> getMessagesBefore(
            @PathVariable Long id,
            @RequestParam(defaultValue = "50") int size) {
        return chatRoomService.getMessagesBefore(id, size);
    }

    /** 채팅방별 최근 메시지 조회 */
    @GetMapping("/rooms/{roomId}/messages")
    public List<ChatMessageResponse> getRoomMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "50") int size) {
        return chatRoomService.getMessagesByRoom(roomId, size);
    }

}
