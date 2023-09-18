package com.oieho.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDummyIP is a Querydsl query type for DummyIP
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDummyIP extends EntityPathBase<DummyIP> {

    private static final long serialVersionUID = -1392213939L;

    public static final QDummyIP dummyIP = new QDummyIP("dummyIP");

    public final StringPath ipAddr = createString("ipAddr");

    public QDummyIP(String variable) {
        super(DummyIP.class, forVariable(variable));
    }

    public QDummyIP(Path<? extends DummyIP> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDummyIP(PathMetadata metadata) {
        super(DummyIP.class, metadata);
    }

}

