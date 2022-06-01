package com.tw.heima.service;


import com.tw.heima.client.RentalDelegationClient;
import com.tw.heima.client.dto.request.RentSeekingRequest;
import com.tw.heima.controller.dto.request.OrderCreateRequest;
import com.tw.heima.repository.OrderRepository;
import com.tw.heima.repository.entity.OrderEntity;
import com.tw.heima.service.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final RentalDelegationClient rentalDelegationClient;

    private final OrderRepository orderRepository;

    public int createOrder(String clientId, OrderCreateRequest request) {
        final Order order = Order.builder()
                .agentId(request.getAgentId())
                .clientId(clientId)
                .mouthPrice(request.getMouthPrice())
                .rentalDelegationId(request.getRentalDelegationId())
                .build();
        final Order persistedOrder = orderRepository.save(OrderEntity.from(order)).to();

        try {
            rentalDelegationClient.confirmRentSeeking(persistedOrder.getRentalDelegationId(), RentSeekingRequest.from(order));
        } catch (Exception e) {
            log.error("call confirm rent seeking failed", e);
        }

        return persistedOrder.getId();
    }
}
