package com.tw.heima.service;

import com.tw.heima.client.BusinessPaymentClient;
import com.tw.heima.client.dto.response.RequestPaymentResponse;
import com.tw.heima.repository.TravelContractRepository;
import com.tw.heima.repository.entity.FixedFeeRequestEntity;
import com.tw.heima.repository.entity.TravelContractEntity;
import feign.FeignException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TravelContractServiceTest {

    @InjectMocks
    private TravelContractService travelContractService;

    @Mock
    private TravelContractRepository travelContractRepository;

    @Mock
    private BusinessPaymentClient businessPaymentClient;

    @Nested
    class RequestFixdFee {
        @Test
        void should_return_requestId_and_initiate_payment_when_cid_is_valid() throws Exception {
            TravelContractEntity contract = TravelContractEntity.builder()
                    .cid("123")
                    .fixedFeeRequest(FixedFeeRequestEntity.builder()
                            .requestId("1-2-3")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .build())
                    .build();
            when(travelContractRepository.findByCid("123")).thenReturn(contract);
            when(businessPaymentClient.requestPayment(any())).thenReturn(new RequestPaymentResponse("paymentId"));

            String requestId = travelContractService.requestFixdFee("123", "cardNumber");

            assertThat(requestId, is("1-2-3"));
        }

        @Test
        void should_initiate_payment_success_when_business_payment_service_handle_request_success_but_return_failed() throws Exception {
            TravelContractEntity contract = TravelContractEntity.builder()
                    .cid("123")
                    .fixedFeeRequest(FixedFeeRequestEntity.builder()
                            .requestId("1-2-3")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .build())
                    .build();
            when(travelContractRepository.findByCid("123")).thenReturn(contract);
            FeignException.GatewayTimeout gatewayTimeout = givenGatewayTimeoutException();
            when(businessPaymentClient.requestPayment(any())).thenThrow(gatewayTimeout);
            when(businessPaymentClient.getPaymentRequest(any())).thenReturn(new RequestPaymentResponse("paymentId"));

            String requestId = travelContractService.requestFixdFee("123", "cardNumber");

            assertThat(requestId, is("1-2-3"));
        }
    }

    private FeignException.GatewayTimeout givenGatewayTimeoutException() {
        FeignException.GatewayTimeout gatewayTimeout = mock(FeignException.GatewayTimeout.class);
        when(gatewayTimeout.status()).thenReturn(504);
        return gatewayTimeout;
    }
}