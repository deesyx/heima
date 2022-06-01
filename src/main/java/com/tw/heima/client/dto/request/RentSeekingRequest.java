package com.tw.heima.client.dto.request;

import com.tw.heima.service.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RentSeekingRequest {
    private Integer orderId;

    public static RentSeekingRequest from(Order order) {
        return RentSeekingRequest.builder()
                .orderId(order.getId())
                .build();
    }
}
