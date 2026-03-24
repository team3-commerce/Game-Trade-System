package com.example.tradedemo.domain.coupon.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCouponHistory is a Querydsl query type for CouponHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCouponHistory extends EntityPathBase<CouponHistory> {

    private static final long serialVersionUID = 1726936985L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCouponHistory couponHistory = new QCouponHistory("couponHistory");

    public final com.example.tradedemo.common.entity.QBase _super = new com.example.tradedemo.common.entity.QBase(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.example.tradedemo.domain.members.entity.QMember member;

    public final QMemberCoupon memberCoupon;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<java.math.BigDecimal> moneyAmount = createNumber("moneyAmount", java.math.BigDecimal.class);

    public final EnumPath<com.example.tradedemo.domain.coupon.enums.CouponHistoryStatus> status = createEnum("status", com.example.tradedemo.domain.coupon.enums.CouponHistoryStatus.class);

    public final DateTimePath<java.time.LocalDateTime> usedAt = createDateTime("usedAt", java.time.LocalDateTime.class);

    public QCouponHistory(String variable) {
        this(CouponHistory.class, forVariable(variable), INITS);
    }

    public QCouponHistory(Path<? extends CouponHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCouponHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCouponHistory(PathMetadata metadata, PathInits inits) {
        this(CouponHistory.class, metadata, inits);
    }

    public QCouponHistory(Class<? extends CouponHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.example.tradedemo.domain.members.entity.QMember(forProperty("member")) : null;
        this.memberCoupon = inits.isInitialized("memberCoupon") ? new QMemberCoupon(forProperty("memberCoupon"), inits.get("memberCoupon")) : null;
    }

}

