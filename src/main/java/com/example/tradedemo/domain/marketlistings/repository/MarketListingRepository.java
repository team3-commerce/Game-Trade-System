package com.example.tradedemo.domain.marketlistings.repository;

import com.example.tradedemo.domain.marketlistings.entity.MarketListing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketListingRepository extends JpaRepository<MarketListing, Long>, MarketListingCustomRepository {}
