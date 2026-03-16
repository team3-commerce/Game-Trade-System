package com.example.tradedemo.domain.memberItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.domain.members.dto.GetAllMemberItemResponse;
import com.example.tradedemo.domain.members.dto.GetMemberItemResponse;
import com.example.tradedemo.domain.members.repository.MemberItemRepository;
import com.example.tradedemo.domain.members.service.MemberItemService;
import com.example.tradedemo.domain.pending.repository.PendingAssetRepository;
import com.example.tradedemo.domain.pending.service.PendingAssetService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@EnableCaching
class MemberItemServiceCacheTest {

    @Autowired
    private MemberItemService memberItemService;

    @Autowired
    private PendingAssetService pendingAssetService;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private MemberItemRepository memberItemRepository;

    @MockitoBean
    private PendingAssetRepository pendingAssetRepository;

    @AfterEach
    void init() {
        Cache inventoryList = cacheManager.getCache("inventoryList");
        if (inventoryList != null) {
            inventoryList.clear();
        }

        Cache inventoryItem = cacheManager.getCache("inventoryItem");
        if (inventoryItem != null) {
            inventoryItem.clear();
        }
    }

    @Test
    void 같은_memberId와_page로_두번_조회하면_레포지토리는_한번만_호출된다() {
        // given
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Page<GetAllMemberItemResponse> page = new PageImpl<>(
                List.of(new GetAllMemberItemResponse(
                        1L, "sword", 3L, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now())),
                pageable,
                1);

        given(memberItemRepository.findAllMemberItemByMemberId(memberId, pageable))
                .willReturn(page);

        // when
        PageResponse<GetAllMemberItemResponse> first = memberItemService.getAllMemberItemV2(memberId, pageable);

        PageResponse<GetAllMemberItemResponse> second = memberItemService.getAllMemberItemV2(memberId, pageable);

        // then
        verify(memberItemRepository, times(1)).findAllMemberItemByMemberId(memberId, pageable);

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
    }

    @Test
    void page가_다르면_서로_다른_캐시로_처리되어_레포지토리가_두번_호출된다() {
        // given
        Long memberId = 1L;
        Pageable page0 = PageRequest.of(0, 10);
        Pageable page1 = PageRequest.of(1, 10);

        Page<GetAllMemberItemResponse> responsePage0 = new PageImpl<>(List.of(), page0, 0);

        Page<GetAllMemberItemResponse> responsePage1 = new PageImpl<>(List.of(), page1, 0);

        given(memberItemRepository.findAllMemberItemByMemberId(memberId, page0)).willReturn(responsePage0);
        given(memberItemRepository.findAllMemberItemByMemberId(memberId, page1)).willReturn(responsePage1);

        // when
        memberItemService.getAllMemberItemV2(memberId, page0);
        memberItemService.getAllMemberItemV2(memberId, page1);

        // then
        verify(memberItemRepository, times(1)).findAllMemberItemByMemberId(memberId, page0);

        verify(memberItemRepository, times(1)).findAllMemberItemByMemberId(memberId, page1);
    }

    @Test
    void 같은_memberId와_memberItemId로_두번_조회하면_단건_레포지토리는_한번만_호출된다() {

        // given
        Long memberId = 1L;
        Long memberItemId = 10L;

        GetMemberItemResponse response = mock(GetMemberItemResponse.class);

        given(memberItemRepository.findMemberItemByMemberIdAndMemberItemId(memberId, memberItemId))
                .willReturn(Optional.of(response));

        // when
        memberItemService.getMemberItemV2(memberId, memberItemId);
        memberItemService.getMemberItemV2(memberId, memberItemId);

        // then
        verify(memberItemRepository, times(1)).findMemberItemByMemberIdAndMemberItemId(memberId, memberItemId);
    }
}
