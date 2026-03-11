package com.example.tradedemo.domain.item.repository;

import com.example.tradedemo.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {}
