package com.example.tradedemo.domain.coupon.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCouponPolicy is a Querydsl query type for CouponPolicy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCouponPolicy extends EntityPathBase<CouponPolicy> {

    private static final long serialVersionUID = -541221491L;

    public static final QCouponPolicy couponPolicy = new QCouponPolicy("couponPolicy");

    public final com.example.tradedemo.common.entity.QBase _super = new com.example.tradedemo.common.entity.QBase(this);

    public final ComparablePath<java.time.Duration> couponDuration = createComparable("couponDuration", java.time.Duration.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> expendQuantity = createNumber("expendQuantity", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.example.tradedemo.domain.coupon.enums.IssueType> issueType = createEnum("issueType", com.example.tradedemo.domain.coupon.enums.IssueType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<java.math.BigDecimal> moneyAmount = createNumber("moneyAmount", java.math.BigDecimal.class);

    public final StringPath name = createString("name");

    public final ComparablePath<java.time.Duration> policyDuration = createComparable("policyDuration", java.time.Duration.class);

    public final DateTimePath<java.time.LocalDateTime> policyExpiredAt = createDateTime("policyExpiredAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> policyStartedAt = createDateTime("policyStartedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> totalQuantity = createNumber("totalQuantity", Long.class);

    public QCouponPolicy(String variable) {
        super(CouponPolicy.class, forVariable(variable));
    }

    public QCouponPolicy(Path<? extends CouponPolicy> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCouponPolicy(PathMetadata metadata) {
        super(CouponPolicy.class, metadata);
    }

}

