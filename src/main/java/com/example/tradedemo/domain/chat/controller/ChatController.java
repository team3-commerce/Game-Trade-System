package com.example.tradedemo.domain.chat.controller;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.domain.chat.dto.ChatMessageResponse;
import com.example.tradedemo.domain.chat.dto.ChatRoomResponse;
import com.example.tradedemo.domain.chat.dto.CreateRoomRequest;
import com.example.tradedemo.domain.chat.facade.ChatFacade;
import com.example.tradedemo.domain.chat.service.ChatService;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final ChatFacade chatFacade;

    /** 전체 채팅방 목록 */
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getMyRooms(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK.value()),
                chatService.getMyRooms(principalDetails.getEmail())));
    }

    /** 내가 BUYER인 채팅방 조회 */
    @GetMapping("/rooms/buyer")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getMyBuyerRooms(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK.value()),
                chatService.getMyBuyerRooms(principalDetails.getEmail())));
    }

    /** 내가 SELLER인 채팅방 조회 */
    @GetMapping("/rooms/seller")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getMySellerRooms(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK.value()),
                chatService.getMySellerRooms(principalDetails.getEmail())));
    }

    /** 채팅방 생성 */
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createRoom(
            @RequestBody CreateRoomRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                String.valueOf(HttpStatus.CREATED.value()),
                chatFacade.createRoom(request, principalDetails.getEmail())));
    }

    /**
     * 채팅방별 커서 기반 메시지 조회
     * lastMessageId 없음 → 최신 50개 (최초 입장 / 재연결 복구)
     * lastMessageId 있음 → 해당 ID 이전 n개 (이전 메시지 더 보기)
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getRoomMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long lastMessageId,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK.value()),
                chatService.getMessagesByRoom(roomId, lastMessageId, size)));
    }

    /**
     * 채팅방의 상품 판매 상태 조회
     * 반환값으로 입력창 활성/비활성 결정
     */
    @GetMapping("/rooms/{roomId}/listing-status")
    public ResponseEntity<ApiResponse<MarketListingStatus>> getListingStatus(
            @PathVariable Long roomId) {
        return ResponseEntity.ok(ApiResponse.success(String.valueOf(HttpStatus.OK.value()),
                chatService.getListingStatus(roomId)));
    }


}
