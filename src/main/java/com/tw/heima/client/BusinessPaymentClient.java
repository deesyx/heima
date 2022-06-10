package com.tw.heima.client;

import com.tw.heima.client.dto.request.RequestPaymentRequest;
import com.tw.heima.client.dto.response.RequestPaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "business-payment", url = "${services.business-payment.url}")
public interface BusinessPaymentClient {

    RequestPaymentResponse requestPayment(RequestPaymentRequest request);
}
