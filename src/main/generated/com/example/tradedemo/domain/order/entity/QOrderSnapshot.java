package com.example.tradedemo.domain.order.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrderSnapshot is a Querydsl query type for OrderSnapshot
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderSnapshot extends EntityPathBase<OrderSnapshot> {

    private static final long serialVersionUID = -736296711L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrderSnapshot orderSnapshot = new QOrderSnapshot("orderSnapshot");

    public final com.example.tradedemo.common.entity.QBase _super = new com.example.tradedemo.common.entity.QBase(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QOrder ordersId;

    public final NumberPath<java.math.BigDecimal> price = createNumber("price", java.math.BigDecimal.class);

    public final StringPath productName = createString("productName");

    public final NumberPath<Long> productQuantity = createNumber("productQuantity", Long.class);

    public QOrderSnapshot(String variable) {
        this(OrderSnapshot.class, forVariable(variable), INITS);
    }

    public QOrderSnapshot(Path<? extends OrderSnapshot> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrderSnapshot(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrderSnapshot(PathMetadata metadata, PathInits inits) {
        this(OrderSnapshot.class, metadata, inits);
    }

    public QOrderSnapshot(Class<? extends OrderSnapshot> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.ordersId = inits.isInitialized("ordersId") ? new QOrder(forProperty("ordersId"), inits.get("ordersId")) : null;
    }

}

