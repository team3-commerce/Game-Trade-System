package com.example.tradedemo.domain.item.entity;

import com.example.tradedemo.common.entity.Base;
import com.example.tradedemo.domain.item.enums.ItemType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 아이템 이름
     */
    @NotNull
    @Column(nullable = false, length = 64)
    private String name;

    /**
     * 아이템 타입
     */
    @NotNull
    @Column(nullable = false)
    private ItemType itemType;

    public static Item create(String name, ItemType type) {
        Item item = new Item();

        item.name = Objects.requireNonNull(name);
        item.itemType = Objects.requireNonNull(type);

        return item;
    }
}
