package tool.builder;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.tradedemo.auth.dto.SignupAuthRequest;
import com.example.tradedemo.auth.service.AuthService;
import com.example.tradedemo.domain.debug.dto.GiveMemberItemRequest;
import com.example.tradedemo.domain.debug.service.DebugService;
import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.item.enums.ItemType;
import com.example.tradedemo.domain.item.repository.ItemRepository;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.example.tradedemo.domain.members.repository.MemberRepository;
import com.example.tradedemo.domain.wallet.entity.Wallet;
import com.example.tradedemo.domain.wallet.repository.WalletRepository;

import lombok.RequiredArgsConstructor;
import tool.DataBuilder;

@Component
@RequiredArgsConstructor
public class SellerBuyerBuilder implements DataBuilder {

    final int MEMBER_SIZE = 1000;

    final BigDecimal INIT_MONEY = BigDecimal.valueOf(Long.MAX_VALUE / 2);
    final Long MEMBER_ITEM_COUNT = Long.MAX_VALUE / 2;
    final String MEMBER_PASSWORD = "1234567890";

    private final MemberRepository memberRepository;

    private final AuthService authService;
    private final WalletRepository walletRepository;
    private final DebugService debugService;
    private final ItemRepository itemRepository;

    private final TransactionTemplate tx;

    @Transactional
    @Override
    public void run() throws Exception {
        if (memberRepository.findAll().size() > 0) {
            throw new Exception("SellBuyBuilder는 member가 없다는 전제 하에서만 작동합니다.");
        }

        Item item = itemRepository.saveAndFlush(Item.create("ITEM", ItemType.EQUIPMENT));


        // 멤버 추가
        for (int i=0; i<MEMBER_SIZE; i++) {
            authService.signup(
                new SignupAuthRequest(
                    "user" + i + "@test.com",
                    MEMBER_PASSWORD,
                    "test user " + i,
                    MemberRole.USER
                )
            );
        }

        // 돈을 주기
        for (int i=0; i<MEMBER_SIZE; i++) {
            Wallet wallet = walletRepository.findByMemberId(Long.valueOf(i+1)).orElseThrow();
            wallet.addBalance(INIT_MONEY);
        }

        // 아이템을 주기
        for (int i=0; i<MEMBER_SIZE; i++) {
            debugService.giveMemberItem(new GiveMemberItemRequest(
                        getMemberEmail(i),
                        item.getId(),
                        null,
                        MEMBER_ITEM_COUNT
            ));
        }
    }

    private String getMemberEmail(int memberId) {
        return "user" + memberId + "@test.com";
    }
}
