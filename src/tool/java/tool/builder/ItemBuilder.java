package tool.builder;

import tool.DataBuilder;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.tradedemo.domain.item.enums.ItemType;

/**
 * 아이템을 추가하는 클래스 입니다. 저의 테스트 목적으로도 작성 되었지만 또 이 테스트 도구를 쓰는 사람의 참조 용으로도 작성 되었습니다.
 * <p>
 * 만약 저 처럼 DB에 데이터를 저장하고 싶으신 분은 {@link DataBuilder}를 상속 받은 뒤 @{@link Component}를 위에 추가하시면 됩니다.
 * 그 다음에는 평소 서비스 쓰는 거 처럼 작성하시면 됩니다.
 */
@Component
@RequiredArgsConstructor
public class ItemBuilder implements DataBuilder {
    private final JdbcTemplate jdbcTemplate;

    private final Random random = new Random(123456789);

    private final LocalDateTime STARTING_DATE = LocalDateTime.of(2000, 1, 1, 0, 0);

    private final String SQL_STATEMENT =
            """
        insert into items (
            item_type, name, created_at, modified_at
        ) values (?, ?, ?, ?);
    """;

    /**
     * 데이터를 넣기위한 코드를 여기에 작성하시면 됩니다.
     */
    @Override
    @Transactional
    public void run() throws Exception {
        jdbcTemplate.batchUpdate(SQL_STATEMENT, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ItemType type = ItemType.EQUIPMENT;

                if (random.nextInt(100) < 40) {
                    type = ItemType.CONSUMABLE;
                }

                final long twoYearsInSeconds = Duration.ofDays(365 * 2).toSeconds();

                LocalDateTime createdAt = STARTING_DATE.plusSeconds(random.nextLong(twoYearsInSeconds));
                LocalDateTime modifiedAt =createdAt.plusSeconds(random.nextLong(twoYearsInSeconds));

                ps.setString(1, type.toString());
                ps.setString(2, getItemName());
                ps.setObject(3, createdAt);
                ps.setObject(4, modifiedAt);
            }

            @Override
            public int getBatchSize() {
                // return 5000000; // 300 MB 정도 나 되기 때문에 일단은 좀작게
                return 50000;
            }
        });
    }

    private String getItemName() {
        StringBuilder sb = new StringBuilder();

        sb.append(mod1[random.nextInt(mod1.length)]).append(" ");
        sb.append(mod2[random.nextInt(mod2.length)]).append(" ");
        sb.append(mod3[random.nextInt(mod3.length)]).append(" ");
        sb.append(mod4[random.nextInt(mod4.length)]).append(" ");
        sb.append(mod5[random.nextInt(mod5.length)]).append(" ");
        sb.append(mod6[random.nextInt(mod6.length)]);

        return sb.toString();
    }

    private final String[] mod1 = new String[] {
        "짱쎈", "간지나는", "훌룡한", "멋진", "불티나는", "싸구려", "초보자용", "중급자용", "고급자용", "소박한", "조촐한", "어메이징한", "기분좋은", "기분나쁜", "이상한",
        "기막힌", "예쁜", "빨간", "파란", "노란", "핑크빛"
    };

    private final String[] mod2 = new String[] {
        "고대의", "미래의", "중세의", "동방의", "서양의",
        "지하세계의", "천상의", "심해의", "용족의", "엘프의",
        "드워프의", "마왕의", "전설의", "잊혀진", "저주받은",
        "축복받은", "왕실의", "해적의", "암흑의", "신성한"
    };

    private final String[] mod3 = new String[] {
        "황금", "백금", "미스릴", "오리하르콘", "다이아몬드",
        "강철", "티타늄", "흑요석", "루비", "에메랄드",
        "사파이어", "뼈", "수정", "운석", "청동",
        "텅스텐", "은", "탄소_섬유", "드래곤_스케일", "아다만티움",
        "100%", "1000%", "10000%", "100000%"
    };

    private final String[] mod4 = new String[] {
        "불꽃", "얼음", "번개", "맹독", "암흑",
        "성스러운", "바람", "대지", "혼돈의", "시간의",
        "공간의", "피의", "영혼", "폭발", "흡혈",
        "마나", "중력", "음파", "환영", "소멸",
    };

    private final String[] mod5 = new String[] {
        "수리검", "장검", "독화살", "철퇴", "삼지창", "권총", "쌍권총", "장검", "단검", "AK-47", "AK-69", "m16", "폭탄", "물약", "갑옷", "방탄복",
        "새총", "엽총", "돌", "마검", "로켓포", "미사일"
    };

    private final String[] mod6 = new String[] {
        "+1", "+2", "+3", "+5", "+7",
        "+10", "+15", "+20", "+50", "+99",
        "Lv_1", "Lv_5", "Lv_10", "Lv_50", "Lv_99",
        "★", "★★", "★★★", "★★★★", "★★★★★"
    };
}
