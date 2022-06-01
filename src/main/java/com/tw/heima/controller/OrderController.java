package com.tw.heima.controller;

import com.tw.heima.controller.dto.request.OrderCreateRequest;
import com.tw.heima.controller.dto.response.OrderCreateResponse;
import com.tw.heima.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/renting-orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderCreateResponse createOrder(
            @RequestHeader String clientId,
            @RequestBody OrderCreateRequest orderCreateRequest
    ) {
        final int orderId = orderService.createOrder(clientId, orderCreateRequest);
        return new OrderCreateResponse(orderId);
    }
}
