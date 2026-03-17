package com.example.tradedemo.domain.members.repository;

import com.example.tradedemo.domain.members.entity.MemberItem;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface MemberItemRepository extends JpaRepository<MemberItem, Long>, MemberItemCustomRepository {

    /**
     * 상품 등록 비관적 락 추가
     * @param id
     * @return
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m " +
            "from MemberItem m where m.id = :id")
    Optional<MemberItem> findByIdForUpdate(Long id);

    Optional<MemberItem> findById(Long id);

    /**
     * 안밴토리
     * @param memberId
     * @param itemId
     * @return
     */
    Optional<MemberItem> findByMemberIdAndItemId(Long memberId, Long itemId);
}
