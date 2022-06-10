package com.tw.heima.service;

import com.tw.heima.client.BusinessPaymentClient;
import com.tw.heima.client.dto.response.RequestPaymentResponse;
import com.tw.heima.exception.BadRequestException;
import com.tw.heima.exception.DataNotFoundException;
import com.tw.heima.exception.ExceptionType;
import com.tw.heima.repository.TravelContractRepository;
import com.tw.heima.repository.entity.FixedFeeConfirmationEntity;
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
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
            when(travelContractRepository.findByCid("123")).thenReturn(Optional.of(contract));
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
            when(travelContractRepository.findByCid("123")).thenReturn(Optional.of(contract));
            FeignException.GatewayTimeout gatewayTimeout = givenGatewayTimeoutException();
            when(businessPaymentClient.requestPayment(any())).thenThrow(gatewayTimeout);
            when(businessPaymentClient.getPaymentRequest(any())).thenReturn(new RequestPaymentResponse("paymentId"));

            String requestId = travelContractService.requestFixdFee("123", "cardNumber");

            assertThat(requestId, is("1-2-3"));
        }

        @Test
        void should_throw_DataNotFoundException_when_cid_is_not_found() {
            when(travelContractRepository.findByCid("321")).thenReturn(Optional.empty());

            DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> travelContractService.requestFixdFee("321", "cardNumber"));

            assertThat(exception.getType(), is(ExceptionType.DATA_NOT_FOUND));
            assertThat(exception.getDetail(), is("contract not found"));
        }

        @Test
        void should_throw_badRequestException_when_contract_has_finish_fixed_fee_payment() {
            FixedFeeConfirmationEntity fixedFeeConfirmation = FixedFeeConfirmationEntity.builder().fixedFeeAmount(BigDecimal.valueOf(1000)).build();
            TravelContractEntity contract = TravelContractEntity.builder()
                    .cid("123")
                    .fixedFeeRequest(FixedFeeRequestEntity.builder()
                            .requestId("1-2-3")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .fixedFeeConfirmation(fixedFeeConfirmation)
                            .build())
                    .build();
            when(travelContractRepository.findByCid("123")).thenReturn(Optional.of(contract));

            BadRequestException exception = assertThrows(BadRequestException.class, () -> travelContractService.requestFixdFee("123", "cardNumber"));

            assertThat(exception.getType(), is(ExceptionType.INPUT_PARAM_INVALID));
            assertThat(exception.getDetail(), is("contract has finish payment"));
        }
    }

    private FeignException.GatewayTimeout givenGatewayTimeoutException() {
        FeignException.GatewayTimeout gatewayTimeout = mock(FeignException.GatewayTimeout.class);
        when(gatewayTimeout.status()).thenReturn(504);
        return gatewayTimeout;
    }
}