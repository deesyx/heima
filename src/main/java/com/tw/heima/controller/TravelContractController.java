package com.tw.heima.controller;

import com.tw.heima.controller.dto.request.RequestFixedFeeRequest;
import com.tw.heima.controller.dto.response.RequestFixedFeeResponse;
import com.tw.heima.service.TravelContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travel-contracts")
@RequiredArgsConstructor
public class TravelContractController {

    private final TravelContractService travelContractService;

    @PostMapping("/{cid}/fixd-fee")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestFixedFeeResponse requestFixdFee(@PathVariable("cid") String cid, @RequestBody RequestFixedFeeRequest request) throws InterruptedException {
        String requestId = travelContractService.requestFixdFee(cid, request.getDestinationCardNumber());
        return new RequestFixedFeeResponse(requestId);
    }
}
