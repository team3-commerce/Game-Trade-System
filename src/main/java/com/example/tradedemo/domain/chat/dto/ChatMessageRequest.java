package com.example.tradedemo.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageRequest {
    private Long roomId;
    private String content;
}
