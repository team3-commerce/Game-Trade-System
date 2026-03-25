package com.example.tradedemo.domain.chat.facade;

import com.example.tradedemo.domain.chat.dto.ChatRoomResponse;
import com.example.tradedemo.domain.chat.dto.CreateRoomRequest;
import com.example.tradedemo.domain.chat.service.ChatService;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.service.MarketListingService;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatFacade {

    private final ChatService chatService;
    private final MemberService memberService;
    private final MarketListingService marketListingService;

    public ChatRoomResponse createRoom(CreateRoomRequest request, String buyerEmail) {
        // 1. 상품 조회 + SELLING 상태 검증
        MarketListing listing = marketListingService.findMarketListing(request.listingId());

        // 2. 구매자, 판매자 회원 정보 조회
        Member buyer = memberService.findMemberByEmail(buyerEmail);
        Member seller = memberService.findMemberByEmail(request.sellerEmail());

        return chatService.createRoom(listing, buyer, seller);
    }

}
