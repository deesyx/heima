package com.tw.heima.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Order {
    private Integer id;
    private String rentalDelegationId;
    private BigDecimal mouthPrice;
    private String clientId;
    private String agentId;
}
