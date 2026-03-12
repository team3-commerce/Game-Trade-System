package com.example.tradedemo.domain.members.repository;

import com.example.tradedemo.domain.members.entity.MemberItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberItemRepository extends JpaRepository<MemberItem, Long>, MemberItemCustomRepository {

    Optional<MemberItem> findById(Long id);

    /**
     * 안밴토리
     * @param memberId
     * @param itemId
     * @return
     */
    Optional<MemberItem> findByMemberIdAndItemId(Long memberId, Long itemId);
}
