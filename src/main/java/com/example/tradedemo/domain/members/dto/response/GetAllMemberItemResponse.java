package com.example.tradedemo.domain.members.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetAllMemberItemResponse {
    private final Long memberItemId;
    private final String itemName;
    private final Long quantity;
    private final LocalDateTime acquiredAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
}
