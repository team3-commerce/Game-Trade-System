package com.example.tradedemo.scenario;

import com.example.tradedemo.auth.dto.LoginAuthRequest;
import com.example.tradedemo.auth.dto.SignupAuthRequest;
import com.example.tradedemo.domain.debug.dto.GiveMemberItemRequest;
import com.example.tradedemo.domain.marketlistings.dto.CreateMarketListingRequest;
import com.example.tradedemo.domain.marketlistings.enums.SalesDurations;
import com.example.tradedemo.domain.members.enums.MemberRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-scenario")
@AutoConfigureRestTestClient
@Testcontainers
public class ScenarioTest1 {
    @Autowired
    private RestTestClient client;

    static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis")).withExposedPorts(6379);

    static GenericContainer mysql = new GenericContainer(DockerImageName.parse("mysql:8.4"))
            .withExposedPorts(3306)
            .withEnv("MYSQL_ROOT_PASSWORD", "1234")
            .withEnv("MYSQL_DATABASE", "tradedb");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        redis.start();
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);

        mysql.start();
        registry.add("spring.datasource.url", () -> {
            return "jdbc:mysql://" + mysql.getHost() + ":" + mysql.getFirstMappedPort() + "/tradedb";
        });
    }

    /**
     * 쿠폰 발급 부터 구매, 판매까지 정말 되는지만 체크하는 테스트 입니다.
     * @throws IOException
     */
    @Test
    void 회원가입부터_구매_판매_까지() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        final String member1Email = "member1@gmail.com";
        final String member1Password = "1234qwer";

        // 회원가입
        signup(new SignupAuthRequest(member1Email, member1Password, "멤버1", MemberRole.USER));

        // 로그인
        String member1JwtToken = login(mapper, new LoginAuthRequest(member1Email, member1Password));

        // 멤버 1 한테 아이템을 주기
        Long itemId = getFirstItemId(mapper, member1JwtToken);

        client.post()
                .uri("/api/debug/give-member-item")
                .header("Authorization", "Bearer " + member1JwtToken)
                .body(new GiveMemberItemRequest(member1Email, itemId, null, 10L))
                .exchangeSuccessfully();

        Long memberItemId = getMyFirstItemId(mapper, member1JwtToken);

        // 멤버 1 이 아이템을 판매 등록
        client.post()
                .uri("/api/v1/market-listings")
                .header("Authorization", "Bearer " + member1JwtToken)
                .body(new CreateMarketListingRequest(memberItemId, new BigDecimal(1000L), 2L, SalesDurations.HOURS_12))
                .exchangeSuccessfully();

        final String member2Email = "member2@gmail.com";
        final String member2Password = "1234qwer";

        signup(new SignupAuthRequest(member2Email, member2Password, "멤버2", MemberRole.USER));
        String member2JwtToken = login(mapper, new LoginAuthRequest(member2Email, member2Password));

        Long couponId = getMyFirstUnusedCouponId(mapper, member2JwtToken);

        // 멤버 2 가 쿠폰을 사용
        client.post()
                .uri("/api/v2/me/coupons/%s/use".formatted(couponId))
                .header("Authorization", "Bearer " + member2JwtToken)
                .exchangeSuccessfully();

        // 멤버 2 가 마켓 리스팅을 조회
        Long marketListingId = getFirstMarketListingId(mapper, member2JwtToken);

        // 멤버 2 가 구매
        client.post()
                .uri("api/v2/market-listings/%s".formatted(marketListingId))
                .header("Authorization", "Bearer " + member2JwtToken)
                .exchangeSuccessfully();

        // 정산
        Long member1PendingAsset = getFirstPendingAssetId(mapper, member1JwtToken);
        settlePendingAsset(member1JwtToken, member1PendingAsset);

        Long member2PendingAsset = getFirstPendingAssetId(mapper, member2JwtToken);
        settlePendingAsset(member2JwtToken, member2PendingAsset);
    }

    private void signup(SignupAuthRequest req) {
        client.post().uri("/api/v1/auth/signup").body(req).exchangeSuccessfully();
    }

    private String login(ObjectMapper mapper, LoginAuthRequest req) throws IOException {
        byte[] res = client.post()
                .uri("/api/v1/auth/login")
                .body(req)
                .exchangeSuccessfully()
                .expectBody()
                .returnResult()
                .getResponseBody();

        return mapper.readTree(res).get("data").get("accessToken").asText();
    }

    private Long getFirstItemId(ObjectMapper mapper, String jwtToken) throws IOException {
        byte[] res = client.get()
                .uri("/api/v2/items")
                .header("Authorization", "Bearer " + jwtToken)
                .exchangeSuccessfully()
                .expectBody()
                .returnResult()
                .getResponseBody();

        return mapper.readTree(res)
                .get("data")
                .get("content")
                .get(0)
                .get("itemId")
                .asLong();
    }

    private Long getMyFirstItemId(ObjectMapper mapper, String jwtToken) throws IOException {
        byte[] res = client.get()
                .uri("/api/v1/me/items")
                .header("Authorization", "Bearer " + jwtToken)
                .exchangeSuccessfully()
                .expectBody()
                .returnResult()
                .getResponseBody();

        return mapper.readTree(res)
                .get("data")
                .get("content")
                .get(0)
                .get("memberItemId")
                .asLong();
    }

    private Long getMyFirstUnusedCouponId(ObjectMapper mapper, String jwtToken) throws IOException {
        byte[] res = client.get()
                .uri("/api/v2/me/coupons?stats=UNUSED")
                .header("Authorization", "Bearer " + jwtToken)
                .exchangeSuccessfully()
                .expectBody()
                .returnResult()
                .getResponseBody();

        return mapper.readTree(res)
                .get("data")
                .get("content")
                .get(0)
                .get("memberCouponId")
                .asLong();
    }

    private Long getFirstMarketListingId(ObjectMapper mapper, String jwtToken) throws IOException {
        byte[] res = client.get()
                .uri("/api/v2/market-listings")
                .header("Authorization", "Bearer " + jwtToken)
                .exchangeSuccessfully()
                .expectBody()
                .returnResult()
                .getResponseBody();

        return mapper.readTree(res)
                .get("data")
                .get("content")
                .get(0)
                .get("marketListingId")
                .asLong();
    }

    private Long getFirstPendingAssetId(ObjectMapper mapper, String jwtToken) throws IOException {
        byte[] res = client.get()
                .uri("/api/v1/me/pending-assets")
                .header("Authorization", "Bearer " + jwtToken)
                .exchangeSuccessfully()
                .expectBody()
                .returnResult()
                .getResponseBody();

        return mapper.readTree(res).get("data").get(0).get("pendingAssetId").asLong();
    }

    private void settlePendingAsset(String jwtToken, Long pendingAssetId) throws IOException {
        client.post()
                .uri("/api/v2/me/pending-assets/%s".formatted(pendingAssetId))
                .header("Authorization", "Bearer " + jwtToken)
                .exchangeSuccessfully();
    }
}
