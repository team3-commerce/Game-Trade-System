package com.example.tradedemo.domain.chat.dto;

import java.util.List;

public record CreateRoomRequest(List<String> inviteeEmails) {
}
