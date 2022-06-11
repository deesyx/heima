package com.tw.heima.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestInvoiceRequest {
    private String identifier;
    private BigDecimal amount;
    private String requestId;
}
