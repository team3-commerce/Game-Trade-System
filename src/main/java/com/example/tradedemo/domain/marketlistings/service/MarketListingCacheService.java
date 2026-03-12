package com.example.tradedemo.domain.marketlistings.service;

import com.example.tradedemo.domain.marketlistings.consts.MarketListingConsts;
import com.example.tradedemo.domain.marketlistings.dto.response.SearchTrendingKeywordResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
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
         * 동일 검색어 어뷰징 방지용
         */
        String dupCheckKey = MarketListingConsts.MARKET_LISTING + memberId + ":" + normalizedKeyword;
        Boolean firstSearch = redisTemplate
                .opsForValue()
                .setIfAbsent(
                        dupCheckKey,
                        normalizedKeyword,
                        Duration.ofMinutes(MarketListingConsts.SEARCH_DUPLICATE_PREVENT_MINUTES));

        if (!firstSearch) {
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
}
