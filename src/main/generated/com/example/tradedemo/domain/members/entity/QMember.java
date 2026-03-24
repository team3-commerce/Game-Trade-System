package com.example.tradedemo.domain.members.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1012985278L;

    public static final QMember member = new QMember("member1");

    public final com.example.tradedemo.common.entity.QBase _super = new com.example.tradedemo.common.entity.QBase(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> lastLoginAt = createDateTime("lastLoginAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final StringPath refreshToken = createString("refreshToken");

    public final EnumPath<com.example.tradedemo.domain.members.enums.MemberRole> role = createEnum("role", com.example.tradedemo.domain.members.enums.MemberRole.class);

    public final EnumPath<com.example.tradedemo.domain.members.enums.MemberStatus> status = createEnum("status", com.example.tradedemo.domain.members.enums.MemberStatus.class);

    public final DateTimePath<java.time.LocalDateTime> statusChangedAt = createDateTime("statusChangedAt", java.time.LocalDateTime.class);

    public final StringPath statusReason = createString("statusReason");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

