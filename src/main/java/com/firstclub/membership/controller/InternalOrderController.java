package com.firstclub.membership.controller;

import com.firstclub.membership.generated.api.InternalOrdersApi;
import com.firstclub.membership.generated.model.OrderIngestionRequest;
import com.firstclub.membership.generated.model.UserMonthlyOrdersMeta;
import com.firstclub.membership.service.OrderAggregateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InternalOrderController implements InternalOrdersApi {
    private final OrderAggregateService orderAggregateService;

    public InternalOrderController(OrderAggregateService orderAggregateService) {
        this.orderAggregateService = orderAggregateService;
    }

    @Override
    public ResponseEntity<UserMonthlyOrdersMeta> ingestOrder(
        String xUserId,
        OrderIngestionRequest orderIngestionRequest
    ) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(orderAggregateService.ingestOrder(xUserId, orderIngestionRequest.getOrderValue()));
    }
}
