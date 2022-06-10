package com.tw.heima.client;

import com.tw.heima.client.dto.request.RequestPaymentRequest;
import com.tw.heima.client.dto.response.RequestPaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "business-payment", url = "${services.business-payment.url}")
public interface BusinessPaymentClient {

    @PostMapping("/union-pay/payments")
    RequestPaymentResponse requestPayment(RequestPaymentRequest request);
}
