package com.example.tradedemo.domain.members.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMemberItemResponse {
    private Long memberItemId;
    private String itemName;
    private Long quantity;
    private LocalDateTime acquiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
