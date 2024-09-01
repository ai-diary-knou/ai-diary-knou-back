package com.aidiary.core.dto;

import com.aidiary.common.enums.PagingOrder;
import com.aidiary.common.vo.PagingRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.Objects;
import java.util.stream.Collectors;

public class JpaPagingRequest {

    public static PageRequest of(PagingRequest pagingRequest){
        Sort sort = Sort.by(pagingRequest.getSort().getOrders().stream().map(JpaPagingRequest::toJpaOrder).collect(Collectors.toList()));
        return PageRequest.of(pagingRequest.getPageNumber(), pagingRequest.getPageSize(), sort);
    }

    private static Sort.Order toJpaOrder(PagingRequest.Order order) {
        if (Objects.isNull(order.getOrder()) || PagingOrder.ASC.equals(order.getOrder())) {
            return Sort.Order.asc(order.getProperty());
        }
        return Sort.Order.desc(order.getProperty());
    }

}
