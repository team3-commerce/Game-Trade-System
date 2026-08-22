package com.example.tradedemo.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedisChatMessageRequest {
    // 서버에서 Redis에 전송하기 위한 메세지 Dto
    private Long roomId;
    private Long senderId;
    private String senderNickname;
    private String content;
}
