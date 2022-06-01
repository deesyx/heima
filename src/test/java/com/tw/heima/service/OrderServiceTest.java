package com.tw.heima.service;

import com.tw.heima.client.RentalDelegationClient;
import com.tw.heima.client.dto.response.RentSeekingResponse;
import com.tw.heima.controller.dto.request.OrderCreateRequest;
import com.tw.heima.repository.OrderRepository;
import com.tw.heima.repository.entity.OrderEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RentalDelegationClient rentalDelegationClient;

    @Test
    void should_create_order_success() {
        when(orderRepository.save(any())).thenReturn(OrderEntity.builder()
                .id(1)
                .agentId("agentId")
                .clientId("M123456")
                .createdAt(LocalDateTime.now())
                .mouthPrice(new BigDecimal("3000"))
                .rentalDelegationId("rentalDelegationId")
                .build());
        when(rentalDelegationClient.confirmRentSeeking(eq("rentalDelegationId"), any()))
                .thenReturn(RentSeekingResponse.builder()
                        .rentId("rentId")
                        .build());

        int orderId = orderService.createOrder("M123456", OrderCreateRequest.builder()
                .agentId("agentId")
                .mouthPrice(new BigDecimal("3000"))
                .rentalDelegationId("rentalDelegationId")
                .build());

        assertThat(orderId, is(1));
    }
}