package com.example.tradedemo.domain.order.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrder is a Querydsl query type for Order
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrder extends EntityPathBase<Order> {

    private static final long serialVersionUID = 286698517L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrder order = new QOrder("order1");

    public final com.example.tradedemo.common.entity.QBase _super = new com.example.tradedemo.common.entity.QBase(this);

    public final com.example.tradedemo.domain.members.entity.QMember buyer;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.example.tradedemo.domain.item.entity.QItem item;

    public final com.example.tradedemo.domain.marketlistings.entity.QMarketListing marketListing;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final com.example.tradedemo.domain.members.entity.QMember seller;

    public final NumberPath<java.math.BigDecimal> transactionMoney = createNumber("transactionMoney", java.math.BigDecimal.class);

    public final NumberPath<Long> transactionStock = createNumber("transactionStock", Long.class);

    public QOrder(String variable) {
        this(Order.class, forVariable(variable), INITS);
    }

    public QOrder(Path<? extends Order> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrder(PathMetadata metadata, PathInits inits) {
        this(Order.class, metadata, inits);
    }

    public QOrder(Class<? extends Order> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.buyer = inits.isInitialized("buyer") ? new com.example.tradedemo.domain.members.entity.QMember(forProperty("buyer")) : null;
        this.item = inits.isInitialized("item") ? new com.example.tradedemo.domain.item.entity.QItem(forProperty("item")) : null;
        this.marketListing = inits.isInitialized("marketListing") ? new com.example.tradedemo.domain.marketlistings.entity.QMarketListing(forProperty("marketListing"), inits.get("marketListing")) : null;
        this.seller = inits.isInitialized("seller") ? new com.example.tradedemo.domain.members.entity.QMember(forProperty("seller")) : null;
    }

}

