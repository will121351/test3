package com.jspxcms.ext.domain.dsl;

import static com.querydsl.core.types.PathMetadataFactory.*;
import com.jspxcms.ext.domain.VisitStat;


import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVisitStat is a Querydsl query type for VisitStat
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QVisitStat extends EntityPathBase<VisitStat> {

    private static final long serialVersionUID = 1388917893L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVisitStat visitStat = new QVisitStat("visitStat");

    public final DateTimePath<java.sql.Timestamp> date = createDateTime("date", java.sql.Timestamp.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> ipViews = createNumber("ipViews", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> period = createNumber("period", Integer.class);

    public final com.jspxcms.core.domain.dsl.QSite site;

    public final NumberPath<Integer> type = createNumber("type", Integer.class);

    public final NumberPath<Integer> uniqueViews = createNumber("uniqueViews", Integer.class);

    public final NumberPath<Integer> views = createNumber("views", Integer.class);

    public QVisitStat(String variable) {
        this(VisitStat.class, forVariable(variable), INITS);
    }

    public QVisitStat(Path<? extends VisitStat> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVisitStat(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVisitStat(PathMetadata metadata, PathInits inits) {
        this(VisitStat.class, metadata, inits);
    }

    public QVisitStat(Class<? extends VisitStat> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.site = inits.isInitialized("site") ? new com.jspxcms.core.domain.dsl.QSite(forProperty("site"), inits.get("site")) : null;
    }

}

