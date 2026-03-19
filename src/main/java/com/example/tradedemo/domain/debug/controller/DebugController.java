package com.example.tradedemo.domain.debug.controller;

import com.example.tradedemo.common.dto.ApiResponse;
import com.example.tradedemo.domain.debug.dto.GiveMemberItemRequest;
import com.example.tradedemo.domain.debug.service.DebugService;
import com.example.tradedemo.domain.members.dto.GetMemberItemResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor()
@RequestMapping("/api/debug")
@Profile("!prod")
@ConditionalOnProperty(name = "app.debug-api.enabled", havingValue = "true")
public class DebugController {

    private final DebugService debugService;
    
    @PostMapping("/give-member-item")
    public ResponseEntity<ApiResponse<GetMemberItemResponse>> getItem(
            @Valid @RequestBody GiveMemberItemRequest request) {

        GetMemberItemResponse res = debugService.giveMemberItem(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(String.valueOf(HttpStatus.OK.value()), res));
    }
}
