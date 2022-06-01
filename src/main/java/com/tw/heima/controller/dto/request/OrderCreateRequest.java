package com.tw.heima.controller.dto.request;

import com.tw.heima.service.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderCreateRequest {
    private String rentalDelegationId;
    private BigDecimal mouthPrice;
    private String agentId;
}
