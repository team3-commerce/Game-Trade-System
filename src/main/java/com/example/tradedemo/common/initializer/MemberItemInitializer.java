package com.example.tradedemo.common.initializer;

import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.repository.ItemRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.entity.MemberItem;
import com.example.tradedemo.domain.members.repository.MemberItemRepository;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@ConditionalOnProperty(name = "app.add-test-items", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class MemberItemInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final MemberItemRepository memberItemRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        // 테스트용 member
        Member member = memberRepository.findById(1L)
                .orElseThrow();

        // 아이템 조회
        Item sword = itemRepository.findByName("검")
                .orElseThrow();

        Item armor = itemRepository.findByName("갑옷")
                .orElseThrow();
        /**
         * 인벤토리 생성, 획득 시간은 생성시간으로 설정했음
         */
        memberItemRepository.save(
                MemberItem.create(member, sword, 10L, LocalDateTime.now())
        );
        memberItemRepository.save(
                MemberItem.create(member, armor, 5L, LocalDateTime.now())
        );
    }
}