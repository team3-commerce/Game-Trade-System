package com.example.tradedemo.auth.interceptor;

import com.example.tradedemo.auth.dto.PrincipalDetails;
import com.example.tradedemo.auth.provider.JwtTokenProvider;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // CONNECT 연결일 때 JWT 토큰 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || authHeader.isBlank()) {
                throw new ServiceException(ErrorEnum.ERR_AUTH_INVALID_TOKEN);
            }

            String token;

            if (authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else {
                token = authHeader;
            }

            String email = jwtTokenProvider.getUserEmail(token);

            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new ServiceException(ErrorEnum.ERR_MEMBER_NOT_FOUND));

            accessor.setUser(new PrincipalDetails(member));
        }

        return message;
    }

}
