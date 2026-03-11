package com.example.tradedemo.domain.members.repository;

import java.util.Optional;

import com.example.tradedemo.domain.members.entity.MemberItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberItemRepository extends JpaRepository<MemberItem, Long>, MemberItemCustomRepository {

    Optional<MemberItem> findById(Long id);
}