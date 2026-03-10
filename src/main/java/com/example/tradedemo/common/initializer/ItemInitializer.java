package com.example.tradedemo.common.initializer;

import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.enums.ItemType;
import com.example.tradedemo.domain.item.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 초기 item들을 생성해주는 class입니다.
 * <p>
 * 만약 설정 파일에
 * <pre>
 * {@code
 * app:
 *   add-test-items: true
 * }
 * </pre>
 *
 * 로 설정되어 있을 경우 test item들을 추가해 줍니다.
 */
@Component()
@ConditionalOnProperty(name = "app.add-test-items", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
public class ItemInitializer implements ApplicationRunner {
    private final ItemRepository itemRepository;

    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        itemRepository.save(Item.create("검", ItemType.EQUIPMENT));
        itemRepository.save(Item.create("갑옷", ItemType.EQUIPMENT));

        itemRepository.save(Item.create("치유 물약", ItemType.CONSUMABLE));
        itemRepository.save(Item.create("폭탄", ItemType.CONSUMABLE));
    }
}
