package com.tw.heima.controller;

import com.tw.heima.controller.dto.request.RequestFixedFeeInvoiceRequest;
import com.tw.heima.controller.dto.request.RequestFixedFeeRequest;
import com.tw.heima.controller.dto.response.RequestFixedFeeInvoiceResponse;
import com.tw.heima.controller.dto.response.RequestFixedFeeResponse;
import com.tw.heima.service.TravelContractService;
import com.tw.heima.service.model.FixedFeeInvoiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel-contracts")
@RequiredArgsConstructor
public class TravelContractController {

    private final TravelContractService travelContractService;

    @PostMapping("/{cid}/fixed-fee")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestFixedFeeResponse requestFixedFee(@PathVariable("cid") String cid, @RequestBody RequestFixedFeeRequest request) throws InterruptedException {
        String requestId = travelContractService.requestFixedFee(cid, request.getDestinationCardNumber());
        return new RequestFixedFeeResponse(requestId);
    }

    @PostMapping("/{cid}/fixed-fee-invoice")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestFixedFeeInvoiceResponse requestFixedFeeInvoice(@PathVariable("cid") String cid, @RequestBody RequestFixedFeeInvoiceRequest request) {
        FixedFeeInvoiceRequest fixedFeeInvoiceRequest = travelContractService.requestFixedFeeInvoice(cid, request.getIdentifier());
        return RequestFixedFeeInvoiceResponse.builder()
                .requestId(fixedFeeInvoiceRequest.getRequestId())
                .status(fixedFeeInvoiceRequest.status())
                .build();
    }
}
