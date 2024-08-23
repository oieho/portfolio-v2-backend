package com.oieho.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRecoverPassword is a Querydsl query type for RecoverPassword
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecoverPassword extends EntityPathBase<RecoverPassword> {

    private static final long serialVersionUID = -1168685411L;

    public static final QRecoverPassword recoverPassword = new QRecoverPassword("recoverPassword");

    public final StringPath resetToken = createString("resetToken");

    public QRecoverPassword(String variable) {
        super(RecoverPassword.class, forVariable(variable));
    }

    public QRecoverPassword(Path<? extends RecoverPassword> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRecoverPassword(PathMetadata metadata) {
        super(RecoverPassword.class, metadata);
    }

}

