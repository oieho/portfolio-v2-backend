package com.oieho.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkBoard is a Querydsl query type for WorkBoard
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkBoard extends EntityPathBase<WorkBoard> {

    private static final long serialVersionUID = 299028307L;

    public static final QWorkBoard workBoard = new QWorkBoard("workBoard");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final EnumPath<Category> category = createEnum("category", Category.class);

    public final StringPath description = createString("description");

    public final SetPath<String, StringPath> hashTag = this.<String, StringPath>createSet("hashTag", String.class, StringPath.class, PathInits.DIRECT2);

    public final NumberPath<Integer> hits = createNumber("hits", Integer.class);

    public final StringPath portfolioContent = createString("portfolioContent");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final StringPath title = createString("title");

    public final ListPath<String, StringPath> tools = this.<String, StringPath>createList("tools", String.class, StringPath.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updDate = _super.updDate;

    public final NumberPath<Long> wno = createNumber("wno", Long.class);

    public final ListPath<WorkComment, QWorkComment> workComment = this.<WorkComment, QWorkComment>createList("workComment", WorkComment.class, QWorkComment.class, PathInits.DIRECT2);

    public final ListPath<WorkImage, QWorkImage> workImages = this.<WorkImage, QWorkImage>createList("workImages", WorkImage.class, QWorkImage.class, PathInits.DIRECT2);

    public QWorkBoard(String variable) {
        super(WorkBoard.class, forVariable(variable));
    }

    public QWorkBoard(Path<? extends WorkBoard> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWorkBoard(PathMetadata metadata) {
        super(WorkBoard.class, metadata);
    }

}

