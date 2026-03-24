package com.example.tradedemo.domain.marketlistings.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMarketListing is a Querydsl query type for MarketListing
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMarketListing extends EntityPathBase<MarketListing> {

    private static final long serialVersionUID = -373703346L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMarketListing marketListing = new QMarketListing("marketListing");

    public final com.example.tradedemo.common.entity.QBase _super = new com.example.tradedemo.common.entity.QBase(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath itemName = createString("itemName");

    public final com.example.tradedemo.domain.members.entity.QMember member;

    public final com.example.tradedemo.domain.members.entity.QMemberItem memberItem;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Long> quantity = createNumber("quantity", Long.class);

    public final DateTimePath<java.time.LocalDateTime> saleEndAt = createDateTime("saleEndAt", java.time.LocalDateTime.class);

    public final EnumPath<com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus> status = createEnum("status", com.example.tradedemo.domain.marketlistings.enums.MarketListingStatus.class);

    public final NumberPath<java.math.BigDecimal> totalPrice = createNumber("totalPrice", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> unitPrice = createNumber("unitPrice", java.math.BigDecimal.class);

    public QMarketListing(String variable) {
        this(MarketListing.class, forVariable(variable), INITS);
    }

    public QMarketListing(Path<? extends MarketListing> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMarketListing(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMarketListing(PathMetadata metadata, PathInits inits) {
        this(MarketListing.class, metadata, inits);
    }

    public QMarketListing(Class<? extends MarketListing> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.example.tradedemo.domain.members.entity.QMember(forProperty("member")) : null;
        this.memberItem = inits.isInitialized("memberItem") ? new com.example.tradedemo.domain.members.entity.QMemberItem(forProperty("memberItem"), inits.get("memberItem")) : null;
    }

}

