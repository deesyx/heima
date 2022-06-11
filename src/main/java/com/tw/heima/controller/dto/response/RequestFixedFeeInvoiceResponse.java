package com.tw.heima.controller.dto.response;

import com.tw.heima.service.model.FixedFeeInvoiceRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestFixedFeeInvoiceResponse {
    private String requestId;
    private FixedFeeInvoiceRequestStatus status;
}
