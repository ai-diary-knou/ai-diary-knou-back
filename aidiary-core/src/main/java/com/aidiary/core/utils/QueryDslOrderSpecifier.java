package com.aidiary.core.utils;

import com.aidiary.common.vo.PagingRequest;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;

public class QueryDslOrderSpecifier {

    public static OrderSpecifier<?>[] getOrderSpecifier(PagingRequest pageRequest, EntityPathBase<?> entityPathBase) {

        return pageRequest.getSort().getOrders().stream()
                .map(order -> {
                    String property = order.getProperty(); // 정렬할 필드명
                    PathBuilder<?> pathBuilder = new PathBuilder<>(entityPathBase.getType(), entityPathBase.getMetadata());
                    return new OrderSpecifier(
                            order.isAscending() ? Order.ASC : Order.DESC,
                            pathBuilder.get(property)
                    );
                }).toArray(OrderSpecifier[]::new);
    }

}
