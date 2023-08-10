package com.oieho.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkImage is a Querydsl query type for WorkImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkImage extends EntityPathBase<WorkImage> {

    private static final long serialVersionUID = 305433032L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorkImage workImage = new QWorkImage("workImage");

    public final StringPath imgName = createString("imgName");

    public final NumberPath<Long> inum = createNumber("inum", Long.class);

    public final StringPath path = createString("path");

    public final StringPath uuid = createString("uuid");

    public final QWorkBoard workBoard;

    public QWorkImage(String variable) {
        this(WorkImage.class, forVariable(variable), INITS);
    }

    public QWorkImage(Path<? extends WorkImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorkImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorkImage(PathMetadata metadata, PathInits inits) {
        this(WorkImage.class, metadata, inits);
    }

    public QWorkImage(Class<? extends WorkImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.workBoard = inits.isInitialized("workBoard") ? new QWorkBoard(forProperty("workBoard")) : null;
    }

}

