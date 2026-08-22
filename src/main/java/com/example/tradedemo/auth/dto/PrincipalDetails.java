package com.example.tradedemo.auth.dto;

import com.example.tradedemo.domain.members.entity.Member;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class PrincipalDetails implements UserDetails, OAuth2User, Principal {
    private final Member member;
    private Map<String, Object> attributes;
    private String nameAttributeKey;

    public PrincipalDetails(Member member) {
        this.member = member;
    }

    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    public PrincipalDetails(Member member, Map<String, Object> attributes, String nameAttributeKey) {
        this.member = member;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    public String getEmail() {
        return member.getEmail();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getName() {
        // 서비스별 지정된 키 값이 있으면 해당 값을 우선 반환
        if (attributes != null && nameAttributeKey != null) {
            Object value = attributes.get(nameAttributeKey);
            if (value != null) return String.valueOf(value);
        }
        
        // 예외 상황 대비 폴백
        if (attributes != null) {
            if (attributes.containsKey("id")) return String.valueOf(attributes.get("id"));
            if (attributes.containsKey("sub")) return String.valueOf(attributes.get("sub"));
        }
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
