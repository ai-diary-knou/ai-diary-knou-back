package com.aidiary.common.vo;

import com.aidiary.common.enums.PagingOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Builder
public class PagingRequest {
    private int pageNumber;
    private int pageSize;
    private Sort sort;

    @AllArgsConstructor
    @Getter
    public static class Sort{
        List<Order> orders;

        public static Sort by(String... properties){
            List<Order> orders = Arrays.stream(properties)
                    .map(property -> new Order(property, PagingOrder.ASC))
                    .collect(Collectors.toList());
            return new Sort(orders);
        }

        public static Sort by(Order... orders) {
            return new Sort(Arrays.stream(orders).collect(Collectors.toList()));
        }
    }

    @AllArgsConstructor
    @Getter
    public static class Order{
        private String property;
        private PagingOrder order;

        public static Order asc(String property){
            return new Order(property, PagingOrder.ASC);
        }

        public static Order desc(String property) {
            return new Order(property, PagingOrder.DESC);
        }

        public boolean isAscending() {
            return PagingOrder.ASC.equals(this.order);
        }
    }

    public static PagingRequest of(int pageNumber, int pageSize, Sort sort){
        return PagingRequest.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .sort(sort)
                .build();
    }

    public static PagingRequest of(Sort sort) {
        return PagingRequest.builder().sort(sort).build();
    }

}
