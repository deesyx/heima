package com.tw.heima.repository;

import com.tw.heima.repository.entity.OrderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldCreateOrderSuccess() {
        OrderEntity orderEntity = OrderEntity.builder()
                .clientId("M123456")
                .mouthPrice(BigDecimal.valueOf(2000))
                .agentId("agentId")
                .rentalDelegationId("rentalDelegationId")
                .build();

        OrderEntity persistedOrder = orderRepository.save(orderEntity);

        assertThat(persistedOrder.getId(), is(notNullValue()));
        assertThat(persistedOrder.getCreatedAt(), is(notNullValue()));
    }
}