package com.tw.heima.service;

import com.tw.heima.client.BusinessPaymentClient;
import com.tw.heima.client.dto.request.RequestPaymentRequest;
import com.tw.heima.repository.TravelContractRepository;
import com.tw.heima.repository.entity.FixedFeeRequestEntity;
import com.tw.heima.service.model.FixedFeeRequest;
import com.tw.heima.service.model.TravelContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TravelContractService {

    private final TravelContractRepository travelContractRepository;
    private final BusinessPaymentClient businessPaymentClient;

    public String requestFixdFee(String cid, String destinationCardNumber) {
        TravelContract contract = TravelContract.fromEntity(travelContractRepository.findByCid(cid));
        FixedFeeRequest fixedFeeRequest = contract.getFixedFeeRequest();

        RequestPaymentRequest requestPaymentRequest = RequestPaymentRequest.builder()
                .requestId(fixedFeeRequest.getRequestId())
                .amount(fixedFeeRequest.getFixedFeeAmount())
                .destinationCardNumber(destinationCardNumber)
                .build();

        businessPaymentClient.requestPayment(requestPaymentRequest);

        return fixedFeeRequest.getRequestId();
    }
}
