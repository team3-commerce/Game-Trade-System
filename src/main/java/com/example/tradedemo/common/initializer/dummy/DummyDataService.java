package com.example.tradedemo.common.initializer.dummy;

import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.enums.ItemType;
import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus;
import com.example.tradedemo.domain.marketlistings.enums.SalesDurations;
import com.example.tradedemo.domain.members.consts.MemberConst;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.enums.MemberStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class DummyDataService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker(new Locale("ko"));

    private static final int MEMBER_BATCH_SIZE = 1000;
    private static final int ITEM_BATCH_SIZE = 1000;
    private static final int MEMBER_ITEM_BATCH_SIZE = 10000;
    private static final int MARKET_LISTING_BATCH_SIZE = 10000;

    public void createDummyMember(int totalMemberCount){

        List<Member> batchMembers = new ArrayList<>(MEMBER_BATCH_SIZE);

        String sql = """
            INSERT INTO members
            (email, password, nickname, role, status, members.status_changed_at, members.status_reason)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        String fixedEncodedPassword = passwordEncoder.encode("1234567890");

        for (int i = 0; i < totalMemberCount; i++) {
            String email = "user" + i + "@test.com";
            String nickname = faker.funnyName().name() + i;
            MemberRole role = MemberRole.USER;

            batchMembers.add(Member.create(email, fixedEncodedPassword, nickname, role));

            if(batchMembers.size() == MEMBER_BATCH_SIZE){
                jdbcTemplate.batchUpdate(sql, batchMembers, MEMBER_BATCH_SIZE, (ps, member) -> {
                    ps.setString(1, member.getEmail());
                    ps.setString(2, member.getPassword());
                    ps.setString(3, member.getNickname());
                    ps.setString(4, member.getRole().name());
                    ps.setString(5, member.getStatus().name());
                    ps.setTimestamp(6, Timestamp.valueOf(member.getStatusChangedAt()));
                    ps.setString(7, member.getStatusReason());
                });
                batchMembers.clear();
            }
        }

        if(!batchMembers.isEmpty()){
            jdbcTemplate.batchUpdate(sql, batchMembers, MEMBER_BATCH_SIZE, (ps, member) -> {
                ps.setString(1, member.getEmail());
                ps.setString(2, member.getPassword());
                ps.setString(3, member.getNickname());
                ps.setString(4, member.getRole().name());
                ps.setString(5, member.getStatus().name());
                ps.setTimestamp(6, Timestamp.valueOf(member.getStatusChangedAt()));
                ps.setString(7, member.getStatusReason());
            });
        }
    }

    public void createDummyItem(int totalItemCount) {
        List<Item> batchItems = new ArrayList<>(totalItemCount);

        String sql = """
            INSERT INTO items
            (name, item_type)
            VALUES (?, ?)
            """;

        String[] tiers = {"S", "A", "B", "C", "D"};

        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < totalItemCount; i++) {
            String name = tiers[random.nextInt(tiers.length)] + "_" + faker.minecraft().itemName() + "_" + i;
            ItemType itemType = ItemType.EQUIPMENT;

            batchItems.add(Item.create(name, itemType));

            if(batchItems.size() == ITEM_BATCH_SIZE){
                jdbcTemplate.batchUpdate(sql, batchItems, ITEM_BATCH_SIZE, (ps, item) -> {
                    ps.setString(1, item.getName());
                    ps.setString(2, item.getItemType().name());
                });
                batchItems.clear();
            }
        }

        if(!batchItems.isEmpty()){
            jdbcTemplate.batchUpdate(sql, batchItems, ITEM_BATCH_SIZE, (ps, item) -> {
                ps.setString(1, item.getName());
                ps.setString(2, item.getItemType().name());
            });
        }
    }

    public void createDummyMemberItem(int totalMemberItemCount) {

        long start = System.currentTimeMillis();

        List<Long> memberIdList = jdbcTemplate.queryForList("SELECT id FROM members", Long.class);
        List<Long> itemIdList = jdbcTemplate.queryForList("SELECT id FROM items", Long.class);

        List<Object[]> batchMemberItems = new ArrayList<>(MEMBER_BATCH_SIZE);

        String sql = """
                INSERT INTO member_items
                (quantity, acquired_at, member_id, item_id)
                VALUES (?, ?, ?, ?)
                """;

        LocalDateTime baseTime = LocalDateTime.now();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for(int i = 0 ; i < totalMemberItemCount; i++){
            Long quantity = random.nextLong(1, 11);

            // 획득일은 현재 시간 기준 1일 전후로 랜덤값으로 설정
            LocalDateTime acquiredAt = baseTime.plusSeconds(random
                    .nextLong(-86400, 0));

            Long memberId = memberIdList.get(random.nextInt(memberIdList.size()));
            Long itemId = itemIdList.get(random.nextInt(itemIdList.size()));

            batchMemberItems.add(new Object[]{quantity, Timestamp.valueOf(acquiredAt), memberId, itemId});

            if(batchMemberItems.size() == MEMBER_ITEM_BATCH_SIZE){
                jdbcTemplate.batchUpdate(sql, batchMemberItems);
                batchMemberItems.clear();

                long elapsed = System.currentTimeMillis() - start;

                log.info("create {} memberItem in {} s", i + 1, elapsed / 1000.0);
            }
        }

        if(!batchMemberItems.isEmpty()){
            jdbcTemplate.batchUpdate(sql, batchMemberItems);
        }
    }

    public void createDummyMarketListing(int totalMarketListingCount) {

        long start = System.currentTimeMillis();

        String selectSql = """
                SELECT mi.id, mi.member_id, mi.quantity, i.name
                FROM member_items mi
                join items i on i.id = mi.item_id
                LIMIT %d
                """.formatted(totalMarketListingCount);

        // marketListing 더미 데이터 생성을 위한 memberItem 조회
        List<MemberItemDto> memberItemList = jdbcTemplate.query(selectSql, (rs, rowNum) ->
                new MemberItemDto(
                        rs.getLong("id"),
                        rs.getLong("member_id"),
                        rs.getLong("quantity"),
                        rs.getString("name")
                )
        );

        if(memberItemList.size() < totalMarketListingCount){
            throw new RuntimeException("marketListing 생성 수는 memberItem 더미 데이터 수보다 클 수 없습니다");
        }

        List<Object[]> batchMarketListings = new ArrayList<>(MARKET_LISTING_BATCH_SIZE);

        String insertSql = """
                INSERT INTO market_listings
                (item_name, total_price, unit_price, quantity, status, sale_end_at, member_id, member_items_id, created_at, modified_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        LocalDateTime baseTime = LocalDateTime.now();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        Collections.shuffle(memberItemList);

        for(int i = 0 ; i < totalMarketListingCount; i++){
            MemberItemDto memberItem = memberItemList.get(i);

            String itemName = memberItem.itemName.toLowerCase();
            BigDecimal unitPrice = BigDecimal.valueOf(random.nextInt(1000, 5001));
            Long quantity = memberItem.quantity;
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
            Duration saleDuration = SalesDurations.values()[random.nextInt(SalesDurations.values().length)].getDuration();
            LocalDateTime createdAt = baseTime.plusSeconds(random.nextLong(0, 86401));
            LocalDateTime modifiedAt = createdAt;
            LocalDateTime saleEndAt = createdAt.plus(saleDuration);
            Long memberId = memberItem.memberId;
            Long memberItemId = memberItem.memberItemId;

            batchMarketListings.add(new Object[]{
                    itemName,
                    totalPrice,
                    unitPrice,
                    quantity,
                    MarketListingStatus.SELLING.name(),
                    Timestamp.valueOf(saleEndAt),
                    memberId,
                    memberItemId,
                    Timestamp.valueOf(createdAt),
                    Timestamp.valueOf(modifiedAt)
            });

            if (batchMarketListings.size() == MARKET_LISTING_BATCH_SIZE) {
                jdbcTemplate.batchUpdate(insertSql, batchMarketListings);
                batchMarketListings.clear();

                long elapsed = System.currentTimeMillis() - start;

                log.info("create {} marketListing in {} s", i + 1, elapsed / 1000.0);
            }
        }

        if(!batchMarketListings.isEmpty()){
            jdbcTemplate.batchUpdate(insertSql, batchMarketListings);
        }
    }

    private record MemberItemDto(
            Long memberItemId,
            Long memberId,
            Long quantity,
            String itemName
    ){}
}
