package com.example.tradedemo.domain.members.repository;

import com.example.tradedemo.domain.members.entity.MemberItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberItemRepository extends JpaRepository<MemberItem, Long>, MemberItemCustomRepository {}
