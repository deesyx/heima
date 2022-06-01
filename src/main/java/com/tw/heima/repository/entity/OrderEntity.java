package com.tw.heima.repository.entity;

import com.tw.heima.service.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_detail")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String clientId;
    private String agentId;
    private String rentalDelegationId;
    private BigDecimal mouthPrice;
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static OrderEntity from(Order order) {
        return OrderEntity.builder()
                .agentId(order.getAgentId())
                .clientId(order.getClientId())
                .mouthPrice(order.getMouthPrice())
                .rentalDelegationId(order.getRentalDelegationId())
                .build();
    }

    public Order to() {
        return Order.builder()
                .id(this.id)
                .rentalDelegationId(this.rentalDelegationId)
                .mouthPrice(this.mouthPrice)
                .agentId(this.agentId)
                .clientId(this.clientId)
                .build();
    }
}
