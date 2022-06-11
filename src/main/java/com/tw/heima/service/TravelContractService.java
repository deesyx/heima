package com.tw.heima.service;

import com.tw.heima.client.BusinessPaymentClient;
import com.tw.heima.client.dto.request.RequestPaymentRequest;
import com.tw.heima.client.dto.response.RequestPaymentResponse;
import com.tw.heima.exception.BadRequestException;
import com.tw.heima.exception.DataNotFoundException;
import com.tw.heima.exception.ExceptionType;
import com.tw.heima.exception.ExternalServerException;
import com.tw.heima.repository.TravelContractRepository;
import com.tw.heima.repository.entity.TravelContractEntity;
import com.tw.heima.service.model.FixedFeeRequest;
import com.tw.heima.service.model.TravelContract;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.tw.heima.exception.ExceptionType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TravelContractService {

    private final TravelContractRepository travelContractRepository;
    private final BusinessPaymentClient businessPaymentClient;

    public String requestFixdFee(String cid, String destinationCardNumber) throws InterruptedException {
        TravelContractEntity contractEntity = travelContractRepository.findByCid(cid)
                .orElseThrow(() -> new DataNotFoundException(DATA_NOT_FOUND, "contract not found"));
        TravelContract contract = TravelContract.fromEntity(contractEntity);
        if (contract.getFixedFeeRequest().hasConfirm()) {
            throw new BadRequestException(INPUT_PARAM_INVALID, "contract has finish payment");
        }

        FixedFeeRequest fixedFeeRequest = contract.getFixedFeeRequest();
        RequestPaymentRequest requestPaymentRequest = RequestPaymentRequest.builder()
                .requestId(fixedFeeRequest.getRequestId())
                .amount(fixedFeeRequest.getFixedFeeAmount())
                .destinationCardNumber(destinationCardNumber)
                .build();

        try {
            businessPaymentClient.requestPayment(requestPaymentRequest);
        } catch (FeignException feignException) {
            if (feignException.status() >= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                String paymentId = queryPaymentRequest(fixedFeeRequest.getRequestId());
                log.info(String.format("payment has success requested for %s with paymentId %s", fixedFeeRequest.getRequestId(), paymentId));
            } else {
                throw feignException;
            }
        }

        return fixedFeeRequest.getRequestId();
    }

    private String queryPaymentRequest(String requestId) throws InterruptedException {
        int totalRetryTimes = 3;
        for (int retryTime = 0; retryTime < totalRetryTimes; retryTime++) {
            Thread.sleep(3000);
            RequestPaymentResponse response;
            try {
                response = businessPaymentClient.getPaymentRequest(requestId);
            } catch (FeignException feignException) {
                if (feignException.status() == HttpStatus.NOT_FOUND.value()) {
                    log.info(String.format("payment request has not handled for %s", requestId));
                    throw new ExternalServerException(RETRY_LATTER, "business payment service is temporarily unavailable");
                } else if (feignException.status() >= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    continue;
                } else {
                    throw feignException;
                }
            }
            return response.getPaymentId();
        }
        throw new ExternalServerException(ExceptionType.CONTACT_IT, "business payment service is unavailable");
    }
}
