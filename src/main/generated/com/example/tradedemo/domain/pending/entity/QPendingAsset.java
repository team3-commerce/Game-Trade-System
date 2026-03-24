package com.example.tradedemo.domain.pending.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPendingAsset is a Querydsl query type for PendingAsset
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPendingAsset extends EntityPathBase<PendingAsset> {

    private static final long serialVersionUID = 791012667L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPendingAsset pendingAsset = new QPendingAsset("pendingAsset");

    public final com.example.tradedemo.common.entity.QBase _super = new com.example.tradedemo.common.entity.QBase(this);

    public final DateTimePath<java.time.LocalDateTime> claimedAt = createDateTime("claimedAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> expiredAt = createDateTime("expiredAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isClaimed = createBoolean("isClaimed");

    public final NumberPath<Long> itemQuantity = createNumber("itemQuantity", Long.class);

    public final com.example.tradedemo.domain.marketlistings.entity.QMarketListing marketListing;

    public final com.example.tradedemo.domain.members.entity.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<java.math.BigDecimal> moneyAmount = createNumber("moneyAmount", java.math.BigDecimal.class);

    public final com.example.tradedemo.domain.order.entity.QOrder order;

    public final EnumPath<com.example.tradedemo.domain.pending.enums.PendingType> pendingType = createEnum("pendingType", com.example.tradedemo.domain.pending.enums.PendingType.class);

    public final EnumPath<com.example.tradedemo.domain.pending.enums.Type> type = createEnum("type", com.example.tradedemo.domain.pending.enums.Type.class);

    public QPendingAsset(String variable) {
        this(PendingAsset.class, forVariable(variable), INITS);
    }

    public QPendingAsset(Path<? extends PendingAsset> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPendingAsset(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPendingAsset(PathMetadata metadata, PathInits inits) {
        this(PendingAsset.class, metadata, inits);
    }

    public QPendingAsset(Class<? extends PendingAsset> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.marketListing = inits.isInitialized("marketListing") ? new com.example.tradedemo.domain.marketlistings.entity.QMarketListing(forProperty("marketListing"), inits.get("marketListing")) : null;
        this.member = inits.isInitialized("member") ? new com.example.tradedemo.domain.members.entity.QMember(forProperty("member")) : null;
        this.order = inits.isInitialized("order") ? new com.example.tradedemo.domain.order.entity.QOrder(forProperty("order"), inits.get("order")) : null;
    }

}

