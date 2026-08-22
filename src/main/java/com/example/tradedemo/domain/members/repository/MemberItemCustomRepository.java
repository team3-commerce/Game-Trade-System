package com.example.tradedemo.domain.members.repository;

import com.example.tradedemo.domain.members.dto.GetAllMemberItemResponse;
import com.example.tradedemo.domain.members.dto.GetMemberItemResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberItemCustomRepository {
    Page<GetAllMemberItemResponse> findAllMemberItemByMemberId(Long memberId, Pageable pageable);

    Optional<GetMemberItemResponse> findMemberItemByMemberIdAndMemberItemId(Long memberId, Long memberItemId);
}
