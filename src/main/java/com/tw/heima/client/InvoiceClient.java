package com.tw.heima.client;

import com.tw.heima.client.dto.request.RequestInvoiceRequest;
import com.tw.heima.client.dto.response.RequestInvoiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "invoice", url = "${services.invoice.url}")
public interface InvoiceClient {

    @PostMapping("/invoices")
    RequestInvoiceResponse requestInvoice(@RequestBody RequestInvoiceRequest request);
}
