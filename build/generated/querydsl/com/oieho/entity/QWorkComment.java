package com.oieho.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkComment is a Querydsl query type for WorkComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkComment extends EntityPathBase<WorkComment> {

    private static final long serialVersionUID = 501835660L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorkComment workComment = new QWorkComment("workComment");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final NumberPath<Long> cno = createNumber("cno", Long.class);

    public final NumberPath<Long> depth = createNumber("depth", Long.class);

    public final NumberPath<Long> face = createNumber("face", Long.class);

    public final QMember member;

    public final NumberPath<Long> rdepth = createNumber("rdepth", Long.class);

    public final DateTimePath<java.time.LocalDateTime> regDate = createDateTime("regDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> rnum = createNumber("rnum", Long.class);

    public final StringPath text = createString("text");

    public final NumberPath<Long> uid = createNumber("uid", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updDate = createDateTime("updDate", java.time.LocalDateTime.class);

    public final QWorkBoard workBoard;

    public QWorkComment(String variable) {
        this(WorkComment.class, forVariable(variable), INITS);
    }

    public QWorkComment(Path<? extends WorkComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorkComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorkComment(PathMetadata metadata, PathInits inits) {
        this(WorkComment.class, metadata, inits);
    }

    public QWorkComment(Class<? extends WorkComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.workBoard = inits.isInitialized("workBoard") ? new QWorkBoard(forProperty("workBoard")) : null;
    }

}

