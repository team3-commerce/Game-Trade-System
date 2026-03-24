package com.example.tradedemo.domain.wallet.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWalletHistories is a Querydsl query type for WalletHistories
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWalletHistories extends EntityPathBase<WalletHistories> {

    private static final long serialVersionUID = 993087057L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWalletHistories walletHistories = new QWalletHistories("walletHistories");

    public final com.example.tradedemo.common.entity.QBase _super = new com.example.tradedemo.common.entity.QBase(this);

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> balanceSnapshot = createNumber("balanceSnapshot", java.math.BigDecimal.class);

    public final com.example.tradedemo.domain.coupon.entity.QCouponHistory couponHistory;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.example.tradedemo.domain.members.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final com.example.tradedemo.domain.order.entity.QOrder order;

    public final EnumPath<com.example.tradedemo.domain.wallet.enums.WalletStatus> type = createEnum("type", com.example.tradedemo.domain.wallet.enums.WalletStatus.class);

    public final QWallet wallet;

    public QWalletHistories(String variable) {
        this(WalletHistories.class, forVariable(variable), INITS);
    }

    public QWalletHistories(Path<? extends WalletHistories> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWalletHistories(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWalletHistories(PathMetadata metadata, PathInits inits) {
        this(WalletHistories.class, metadata, inits);
    }

    public QWalletHistories(Class<? extends WalletHistories> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.couponHistory = inits.isInitialized("couponHistory") ? new com.example.tradedemo.domain.coupon.entity.QCouponHistory(forProperty("couponHistory"), inits.get("couponHistory")) : null;
        this.member = inits.isInitialized("member") ? new com.example.tradedemo.domain.members.entity.QMember(forProperty("member")) : null;
        this.order = inits.isInitialized("order") ? new com.example.tradedemo.domain.order.entity.QOrder(forProperty("order"), inits.get("order")) : null;
        this.wallet = inits.isInitialized("wallet") ? new QWallet(forProperty("wallet"), inits.get("wallet")) : null;
    }

}

