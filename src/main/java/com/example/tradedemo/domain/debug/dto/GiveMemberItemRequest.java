package com.example.tradedemo.domain.debug.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GiveMemberItemRequest {
    @NotNull(message = "아이템 ID는 필수입니다")
    Long itemId;

    // null이면 현재 시간 사용
    LocalDateTime acquiredAt;

    @NotNull(message = "수량 필수입니다")
    @Min(value = 1, message = "수량은 1이상 이어야 합니다")
    Long quantity;
}
