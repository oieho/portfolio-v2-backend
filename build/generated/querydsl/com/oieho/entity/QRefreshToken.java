package com.oieho.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRefreshToken is a Querydsl query type for RefreshToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRefreshToken extends EntityPathBase<RefreshToken> {

    private static final long serialVersionUID = -672732640L;

    public static final QRefreshToken refreshToken1 = new QRefreshToken("refreshToken1");

    public final DateTimePath<java.util.Date> expirationTime = createDateTime("expirationTime", java.util.Date.class);

    public final StringPath refreshToken = createString("refreshToken");

    public final NumberPath<Long> refreshTokenSeq = createNumber("refreshTokenSeq", Long.class);

    public final StringPath userId = createString("userId");

    public QRefreshToken(String variable) {
        super(RefreshToken.class, forVariable(variable));
    }

    public QRefreshToken(Path<? extends RefreshToken> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRefreshToken(PathMetadata metadata) {
        super(RefreshToken.class, metadata);
    }

}

