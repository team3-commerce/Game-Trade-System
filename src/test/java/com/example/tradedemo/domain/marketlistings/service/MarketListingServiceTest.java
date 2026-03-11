package com.example.tradedemo.domain.marketlistings.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.enums.ItemType;
import com.example.tradedemo.domain.marketlistings.dto.request.CreateMarketListRequest;
import com.example.tradedemo.domain.marketlistings.dto.response.GetMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.enums.SalesDurations;
import com.example.tradedemo.domain.marketlistings.exception.MarketListingOverSellingException;
import com.example.tradedemo.domain.marketlistings.exception.MarketListingOwnerMismatchException;
import com.example.tradedemo.domain.marketlistings.repository.MarketListingRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberItem;
import com.example.tradedemo.domain.members.entity.MemberRole;
import com.example.tradedemo.domain.members.repository.MemberItemRepository;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MarketListingServiceTest {
    @Mock
    private MarketListingRepository marketListingRepository;

    @Mock
    private MemberItemRepository memberItemRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MarketListingService marketListingService;

    private static record MemberAndItem(Member member, Item item, MemberItem memberItem) {}

    /**
     * 시나리오 1 given을 설정합니다.
     *
     * 유저 momo@naver.com
     *
     * 아이템 검
     *
     * momo는 검 10개를 가지고 있다
     */
    private MemberAndItem setupScenario1() {
        Member member = Member.create("momo@naver.com", "1234qwer", MemberRole.USER);
        ReflectionTestUtils.setField(member, "id", 100L);
        lenient().when(memberRepository.findById(100L)).thenReturn(Optional.of(member));

        Item item = Item.create("검", ItemType.EQUIPMENT);
        ReflectionTestUtils.setField(item, "id", 200L);

        MemberItem memberItem = MemberItem.create(member, item, 10L, LocalDateTime.now());
        ReflectionTestUtils.setField(memberItem, "id", 300L);
        lenient().when(memberItemRepository.findById(300L)).thenReturn(Optional.of(memberItem));

        return new MemberAndItem(member, item, memberItem);
    }

    @Test
    @DisplayName("마켓 리스팅 추가")
    void createMarketListing() {
        // =============
        // GIVEN
        // =============
        MemberAndItem memberAndItem = setupScenario1();

        given(marketListingRepository.saveAndFlush(any(MarketListing.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime now = LocalDateTime.now();

        // =============
        // WHEN
        // =============
        CreateMarketListRequest req = new CreateMarketListRequest(
                memberAndItem.memberItem().getId(), 10L, BigDecimal.valueOf(1000L), SalesDurations.HOURS_24);

        GetMarketListingResponse res =
                marketListingService.createMarketListing(memberAndItem.member().getId(), req);

        // =============
        // THEN
        // =============
        // repository에 실제로 저장되는 것이 아니므로 pass
        // assertThat(res.getMarketListingId()).isNotNull();
        assertThat(res.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(1000L));
        assertThat(res.getUnitPrice()).isEqualByComparingTo(BigDecimal.valueOf(100L));
        assertThat(res.getMarketListingStatus()).isEqualTo(MarketListingStatus.SELLING);
        assertThat(res.getQuantity()).isEqualTo(10L);
        assertThat(res.getSaleEndAt()).isAfter(now.plus(SalesDurations.HOURS_24.getDuration()));
        assertThat(res.getItem().itemId()).isEqualTo(memberAndItem.item().getId());
    }

    @Test
    @DisplayName("본인의 아이템만 판매가능")
    void canOnlySellYourOwn() {
        // =============
        // GIVEN
        // =============
        MemberAndItem memberAndItem = setupScenario1();

        Member member2 = Member.create("steven@gmail.com", "1234qwer", MemberRole.USER);
        ReflectionTestUtils.setField(member2, "id", 101L);
        given(memberRepository.findById(101L)).willReturn(Optional.of(member2));

        // =============
        // WHEN && then
        // =============
        CreateMarketListRequest req = new CreateMarketListRequest(
                memberAndItem.memberItem().getId(), 10L, BigDecimal.valueOf(1000L), SalesDurations.HOURS_24);

        assertThrows(MarketListingOwnerMismatchException.class, () -> {
            marketListingService.createMarketListing(member2.getId(), req);
        });
    }

    @Test
    @DisplayName("가지고있는 아이템 보다 더 많이 판매 불가")
    void canOnlySellWhatYouHave() {
        // =============
        // GIVEN
        // =============
        MemberAndItem memberAndItem = setupScenario1();

        // =============
        // WHEN && then
        // =============
        CreateMarketListRequest req = new CreateMarketListRequest(
                memberAndItem.memberItem().getId(), 100L, BigDecimal.valueOf(1000L), SalesDurations.HOURS_24);

        assertThrows(MarketListingOverSellingException.class, () -> {
            marketListingService.createMarketListing(memberAndItem.member().getId(), req);
        });
    }
}
