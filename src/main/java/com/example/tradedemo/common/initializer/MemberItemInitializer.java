package com.example.tradedemo.common.initializer;

import com.example.tradedemo.auth.dto.SignupAuthRequest;
import com.example.tradedemo.auth.service.AuthService;
import com.example.tradedemo.common.exception.ErrorEnum;
import com.example.tradedemo.common.exception.ServiceException;
import com.example.tradedemo.domain.debug.dto.GiveMemberItemRequest;
import com.example.tradedemo.domain.debug.service.DebugService;
import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.enums.ItemType;
import com.example.tradedemo.domain.item.repository.ItemRepository;
import com.example.tradedemo.domain.members.entity.Member;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.members.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.add-test-memberitems", havingValue = "true", matchIfMissing = false)
@Profile("!prod")
@RequiredArgsConstructor
public class MemberItemInitializer implements ApplicationRunner {

    private final ItemRepository itemRepository;
    private final AuthService authService;
    private final MemberRepository memberRepository;
    private final DebugService debugService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // 테스트용 item 생성
        Item gun = itemRepository.save(Item.create("총", ItemType.EQUIPMENT));
        Item wand = itemRepository.save(Item.create("마법 지팡이", ItemType.EQUIPMENT));

        // 모모라는 test member 생성
        authService.signup(new SignupAuthRequest("momo@gmail.com", "1234qwer", "모모", MemberRole.USER));

        Member momo = memberRepository.findByEmail("momo@gmail.com").orElseThrow(()->
            new ServiceException(ErrorEnum.ERR_AUTH_MEMBER_NOT_FOUND)
        );

        // 모모한테 총과 마법 지팡이를 선물
        GiveMemberItemRequest req = new GiveMemberItemRequest();
        req.setMemberEmail(momo.getEmail());

        req.setItemId(gun.getId());
        req.setQuantity(10L);
        debugService.giveMemberItem(req);

        req.setItemId(wand.getId());
        req.setQuantity(5L);
        debugService.giveMemberItem(req);
    }
}
