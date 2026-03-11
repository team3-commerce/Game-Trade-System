package com.example.tradedemo.domain.members.repository;

import com.example.tradedemo.domain.members.entity.MemberItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberItemRepository extends JpaRepository<MemberItem, Long>, MemberItemCustomRepository {

    Optional<MemberItem> findById(Long id);
}