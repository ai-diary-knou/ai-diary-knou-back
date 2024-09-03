package com.aidiary.core.utils;

import com.aidiary.common.vo.PagingRequest;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class QueryDslOrderSpecifier {

    @AllArgsConstructor
    @Getter
    protected static class OrderProperty {
        private EntityPathBase<?> entityPathBase;
        private String property;

        public static OrderProperty of(String orderProperty) {
            System.out.println("orderProperty : " + orderProperty);
            String[] splits = orderProperty.split("\\.");
            return new OrderProperty(convertToQEntity(splits[0]), splits[1]);
        }
    }

    public static EntityPathBase<?> convertToQEntity(String className) {

        try {

            Class<?> qClass = Class.forName("com.aidiary.core.entity.Q" + className);
            Field field = qClass.getDeclaredField(toLowerCaseFirstChar(className));

            if (!EntityPathBase.class.isAssignableFrom(field.getType())) {
                throw new IllegalArgumentException("Q 클래스 필드가 EntityPathBase의 인스턴스가 아닙니다: " + qClass.getSimpleName());
            }

            return (EntityPathBase<?>) field.get(null);

        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            log.info("엔티티에 대한 Q 클래스를 찾을 수 없습니다: ", e);
            throw new RuntimeException("엔티티에 대한 Q 클래스를 찾을 수 없습니다: " + className, e);
        }
    }

    private static String toLowerCaseFirstChar(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    public static OrderSpecifier<?>[] getOrderSpecifier(PagingRequest pageRequest) {

        return pageRequest.getSort().getOrders().stream()
                .map(order -> {
                    OrderProperty orderProperty = OrderProperty.of(order.getProperty());
                    EntityPathBase<?> entityPathBase = orderProperty.getEntityPathBase();
                    PathBuilder<?> pathBuilder = new PathBuilder<>(entityPathBase.getType(), entityPathBase.getMetadata());
                    return new OrderSpecifier(
                            order.isAscending() ? Order.ASC : Order.DESC,
                            pathBuilder.get(orderProperty.getProperty())
                    );
                }).toArray(OrderSpecifier[]::new);
    }

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
