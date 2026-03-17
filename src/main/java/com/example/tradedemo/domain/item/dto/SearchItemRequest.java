package com.example.tradedemo.domain.item.dto;

import java.util.Locale;

import com.example.tradedemo.domain.item.enums.ItemType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchItemRequest {
    @Min(0)
    @NotNull()
    private Integer page = 0;

    private String keyword;

    private ItemType itemType;

    private String sortCreatedAt;

    /**
     * @return 생성일을 기준으로 옛날것 부터 조회를 원하는지
     */
    public boolean shouldSortCreatedAtAsc() {
        if ("asc".equalsIgnoreCase(sortCreatedAt)) {
            return true;
        } else if ("desc".equalsIgnoreCase(sortCreatedAt)) {
            return false;
        }
        return false;
    }

    /**
     * keyword를 정규화 합니다
     * <p>
     * 키워드가 공백이면 null 반환
     * <p>
     * 키워드 양옆의 공백 제거.
     * <p>
     * 키워드 안에 공백문자가 연속으로 있으면 1개로 줄임.
     * <p>
     * 언어 종류에 영향을 받지 않도록 locale 지정.
     * @return 정규화된 keyword
     */
    public String getNormalizedKeyword() {
        if (keyword == null) {
            return null;
        }

        if (keyword.isBlank()) {
            return null;
        }

        return keyword.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
