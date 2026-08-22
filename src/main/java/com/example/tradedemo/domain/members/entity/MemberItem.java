package com.example.tradedemo.domain.members.entity;

import com.example.tradedemo.common.entity.Base;
import com.example.tradedemo.domain.item.entity.Item;
import com.example.tradedemo.domain.members.exception.InventoryItemNotFoundException;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberItem extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long quantity;

    /**
     * 멤버가 아이템을 회득한 시간
     */
    @Column(name = "acquired_at", nullable = false)
    private LocalDateTime acquiredAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    public static MemberItem create(Member member, Item item, LocalDateTime acquiredAt, Long quantity) {
        MemberItem memberItem = new MemberItem();
        memberItem.member = member;
        memberItem.item = item;
        memberItem.acquiredAt = acquiredAt;
        memberItem.quantity = quantity;

        return memberItem;
    }

    /**
     * 아이템 등록 시 아이템 차감
     */
    public void decrease(Long quantity) {
        if (this.quantity < quantity) {
            throw new InventoryItemNotFoundException();
        }
        this.quantity -= quantity;
    }
    /**
     * 수령 시 아이템 증가
     */
    public void increase(Long quantity) {
        this.quantity += quantity;
    }
}
