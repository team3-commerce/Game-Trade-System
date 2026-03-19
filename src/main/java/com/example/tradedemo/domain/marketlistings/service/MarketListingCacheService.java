package com.example.tradedemo.domain.marketlistings.service;

import com.example.tradedemo.common.dto.PageResponse;
import com.example.tradedemo.domain.marketlistings.consts.MarketListingConsts;
import com.example.tradedemo.domain.marketlistings.dto.SearchAllMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.SearchMarketListingResponse;
import com.example.tradedemo.domain.marketlistings.dto.SearchTrendingKeywordResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketListingCacheService {
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisTemplate<String, Object> objRedisTemplate;

    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final ObjectMapper objectMapper;
    private static final String MARKET_LISTING_ITEM_PREFIX = "marketListing:item:";
    private static final String MARKET_LISTING_FIRST_PAGE_KEY = "marketListing:firstPage";
    private static final Long MARKET_LISTING_CACHE_FIRST_PAGE_TIME = 3L;
    private static final Long MARKET_LISTING_CACHE_GET = 10L;


    /**
     * 검색한 키워드를 카운트해서 캐시에 저장하는 메서드
     */
    public void cacheSearchKeyword(Long memberId, String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return;
        }

        /**
         *  prefix 검색을 redis sorted cache 에서 하기 위해서는 score 값이 0으로 동일해야함
         *  따라서 인기 검색어 카운트용 키, prefix 검색용 키 두 종류 필요
         */
        String key = getTrendingKey();
        String prefixKey = getPrefixKey();

        /**
         * 키워드 양옆의 공백 제거
         * 키워드 안에 공백문자가 연속으로 있으면 1개로 줄임
         * 언어 종류에 영향을 받지 않도록 locale 지정
         */
        String normalizedKeyword = keyword.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);

        /**
         * 동일 유저가 동일 검색어 중복 검색하는 경우 제외
         */
        boolean isAlreadyCheck = isDupCheck(memberId, normalizedKeyword);

        if(isAlreadyCheck){
            return;
        }

        /**
         *  똑같은 keyword를 서로 다른 키에 저장
         *  하나는 인기 검색어 검색 횟수 카운트용, 하나는 prefix 검색어 조회용
         */
        redisTemplate.opsForZSet().incrementScore(key, normalizedKeyword, 1);
        redisTemplate.opsForZSet().add(prefixKey, normalizedKeyword, 0);

        Long ttl = redisTemplate.getExpire(key);
        Long prefixTtl = redisTemplate.getExpire(prefixKey);

        if (ttl == null || ttl <= 0) {
            redisTemplate.expire(key, Duration.ofDays(MarketListingConsts.TRENDING_KEYWORD_TIME_LIMIT));
        }

        if (prefixTtl == null || prefixTtl <= 0) {
            redisTemplate.expire(prefixKey, Duration.ofDays(MarketListingConsts.TRENDING_KEYWORD_TIME_LIMIT));
        }
    }

    /**
     * 검색 횟수가 가장 높은 인기 검색어 조회
     */
    public List<SearchTrendingKeywordResponse> getTrendingKeywordList() {
        String key = getTrendingKey();

        Set<ZSetOperations.TypedTuple<String>> trendingKeywords = redisTemplate
                .opsForZSet()
                .reverseRangeWithScores(key, 0, MarketListingConsts.TRENDING_SEARCH_LIMIT - 1);

        return trendingKeywords.stream()
                .map(t -> SearchTrendingKeywordResponse.create(
                        t.getValue(), t.getScore() == null ? 0L : t.getScore().longValue()))
                .toList();
    }

    /**
     *
     * prefixKeyword가 포함된 검색어 중 검색 횟수가 가장 높은 검색어 조회
     */
    public List<SearchTrendingKeywordResponse> getTrendingKeywordListWithPrefix(String prefixKeyword) {

        if (prefixKeyword == null || prefixKeyword.isBlank()) {
            return List.of();
        }

        String key = getTrendingKey();
        String prefixKey = getPrefixKey();

        String normalizedPrefixKeyword =
                prefixKeyword.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);

        // 해당 키워드로 시작하는 단어 모두 검색
        Set<String> prefixedKeywords = redisTemplate
                .opsForZSet()
                .rangeByLex(prefixKey, Range.closed(normalizedPrefixKeyword, normalizedPrefixKeyword + "\uffff"));

        // 검색한 단어들 중 검색횟수가 가장 높은 일부 데이터만 반환
        return prefixedKeywords.stream()
                .map(keyword -> {
                    Double score = redisTemplate.opsForZSet().score(key, keyword);
                    long count = score == null ? 0L : score.longValue();
                    return SearchTrendingKeywordResponse.create(keyword, count);
                })
                .sorted(Comparator.comparing(SearchTrendingKeywordResponse::getSearchCount)
                        .reversed())
                .limit(MarketListingConsts.TRENDING_SEARCH_LIMIT)
                .toList();
    }

    /**
     * 마켓리스팅 첫번째 페이지 캐시 데이터를 조회
     */
    public PageResponse<SearchAllMarketListingResponse> getMarketListingFirstPage(){
        String key = getMarketListingFirstPageKey();

        Object value = objRedisTemplate.opsForValue().get(key);
        if(value == null) return null;

        return objectMapper.convertValue(
                value,
                new TypeReference<PageResponse<SearchAllMarketListingResponse>>() {}
        );
    }

    /**
     * 마켓리스팅 단건 상품 캐시 데이터를 조회
     */
    public SearchMarketListingResponse getMarketListingItem(Long marketListingId) {
        String key = getMarketListingItemKey(marketListingId);

        Object value = objRedisTemplate.opsForValue().get(key);
        if(value == null) return null;

        return  objectMapper.convertValue(value, SearchMarketListingResponse.class);
    }

    public void setMarketListingItem(Long marketListingId, SearchMarketListingResponse result){
        String key = getMarketListingItemKey(marketListingId);
        objRedisTemplate.opsForValue().set(key, result, MarketListingConsts.LISTING_ITEM_TIME_LIMIT, TimeUnit.MINUTES);
    }

    private String getTrendingKey() {
        return MarketListingConsts.MARKET_LISTING
                + MarketListingConsts.TRENDING_SEARCH
                + LocalDate.now().format(MarketListingConsts.TRENDING_SEARCH_FORMATTER);
    }

    private String getPrefixKey() {
        return MarketListingConsts.MARKET_LISTING
                + MarketListingConsts.TRENDING_PREFIX_KEYWORD
                + LocalDate.now().format(MarketListingConsts.TRENDING_SEARCH_FORMATTER);
    }

    /**
     *
     * 상품 구매 : 캐시 생성
     * 마켓(거래소)에서 조회한 캐시 데이터를 저장
     * 상품 구매 시 조회한 캐시 데이터 삭제
     *
     */


    /**
     * 단건 조회 저장
     * @param marketListingId
     * @param value
     */
    public void setMarketListingItem(Long marketListingId, Object value) {
        objectRedisTemplate.opsForValue().set(
                getMarketListingItemKey(marketListingId),
                value,
                Duration.ofMinutes(MARKET_LISTING_CACHE_GET)
        );
    }
    /**
     * 단건 조회 삭제
     * @param marketListingId
     */
    public void deleteMarketListingItem(Long marketListingId) {
        objectRedisTemplate.delete(getMarketListingItemKey(marketListingId));
        log.info("[MarketListingCacheService] 단건 캐시 삭제 - key: {}", getMarketListingItemKey(marketListingId));
    }
    /**
     * 첫 페이지 저장
     * @param value
     */
    public void setMarketListingFirstPage(Object value) {
        objectRedisTemplate.opsForValue().set(
                MARKET_LISTING_FIRST_PAGE_KEY,
                value,
                Duration.ofMinutes(MARKET_LISTING_CACHE_FIRST_PAGE_TIME)
        );
    }
    /**
     * 첫 페이지 삭제
     */
    public void deleteMarketListingFirstPage() {
        objectRedisTemplate.delete(MARKET_LISTING_FIRST_PAGE_KEY);
        log.info("[MarketListingCacheService] 첫 페이지 캐시 삭제 - key: {}", MARKET_LISTING_FIRST_PAGE_KEY);
    }

    /**
     * 마켓(거래소)의 키, id
     * @param marketListingId
     * @return
     */
    private String getMarketListingItemKey(Long marketListingId) {
        return MARKET_LISTING_ITEM_PREFIX + marketListingId;
    }

    private String getMarketListingFirstPageKey(){
        return MarketListingConsts.MARKET_LISTING + MarketListingConsts.FIRST_PAGE;
    }

    private boolean isDupCheck(Long memberId, String normalizedKeyword){
        String dupCheckKey = MarketListingConsts.MARKET_LISTING + memberId + ":" + normalizedKeyword;
        Boolean firstSearch = redisTemplate
                .opsForValue()
                .setIfAbsent(
                        dupCheckKey,
                        normalizedKeyword,
                        Duration.ofMinutes(MarketListingConsts.SEARCH_DUPLICATE_PREVENT_MINUTES));

        return !Boolean.TRUE.equals(firstSearch);
    }
}
