package com.tw.heima.service;

import com.tw.heima.client.BusinessPaymentClient;
import com.tw.heima.client.InvoiceClient;
import com.tw.heima.client.dto.response.RequestInvoiceResponse;
import com.tw.heima.client.dto.response.RequestPaymentResponse;
import com.tw.heima.exception.BadRequestException;
import com.tw.heima.exception.DataNotFoundException;
import com.tw.heima.exception.ExceptionType;
import com.tw.heima.exception.ExternalServerException;
import com.tw.heima.repository.TravelContractRepository;
import com.tw.heima.repository.entity.*;
import com.tw.heima.service.model.FixedFeeInvoiceRequest;
import com.tw.heima.service.model.FixedFeeInvoiceRequestStatus;
import feign.FeignException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TravelContractServiceTest {

    @InjectMocks
    private TravelContractService travelContractService;

    @Mock
    private TravelContractRepository travelContractRepository;

    @Mock
    private BusinessPaymentClient businessPaymentClient;

    @Mock
    private InvoiceClient invoiceClient;

    @Captor
    ArgumentCaptor<TravelContractEntity> contractEntityCaptor;

    @Nested
    class RequestFixedFee {
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

            String requestId = travelContractService.requestFixedFee("123", "cardNumber");

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

            String requestId = travelContractService.requestFixedFee("123", "cardNumber");

            assertThat(requestId, is("1-2-3"));
        }

        @Test
        void should_throw_DataNotFoundException_when_cid_is_not_found() {
            when(travelContractRepository.findByCid("321")).thenReturn(Optional.empty());

            DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> travelContractService.requestFixedFee("321", "cardNumber"));

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

            BadRequestException exception = assertThrows(BadRequestException.class, () -> travelContractService.requestFixedFee("123", "cardNumber"));

            assertThat(exception.getType(), is(ExceptionType.INPUT_PARAM_INVALID));
            assertThat(exception.getDetail(), is("contract has finish payment"));
        }

        @Test
        void should_throw_ExternalServerException_with_RETRY_LATER_when_business_payment_service_has_not_handle_request() {
            TravelContractEntity contract = TravelContractEntity.builder()
                    .cid("123")
                    .fixedFeeRequest(FixedFeeRequestEntity.builder()
                            .requestId("1-2-3")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .build())
                    .build();
            when(travelContractRepository.findByCid("123")).thenReturn(Optional.of(contract));
            FeignException.ServiceUnavailable serviceUnavailable = givenServiceUnavailableException();
            when(businessPaymentClient.requestPayment(any())).thenThrow(serviceUnavailable);
            FeignException.NotFound notFound = givenNotFoundException();
            when(businessPaymentClient.getPaymentRequest(any())).thenThrow(notFound);

            ExternalServerException exception = assertThrows(ExternalServerException.class, () -> travelContractService.requestFixedFee("123", "cardNumber"));

            assertThat(exception.getType(), is(ExceptionType.RETRY_LATTER));
            assertThat(exception.getDetail(), is("business payment service is temporarily unavailable"));
        }

        @Test
        void should_throw_ExternalServerException_with_CONTACT_IT_when_business_payment_service_is_unavailable() {
            TravelContractEntity contract = TravelContractEntity.builder()
                    .cid("123")
                    .fixedFeeRequest(FixedFeeRequestEntity.builder()
                            .requestId("1-2-3")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .build())
                    .build();
            when(travelContractRepository.findByCid("123")).thenReturn(Optional.of(contract));
            FeignException.ServiceUnavailable serviceUnavailable = givenServiceUnavailableException();
            when(businessPaymentClient.requestPayment(any())).thenThrow(serviceUnavailable);
            when(businessPaymentClient.getPaymentRequest(any())).thenThrow(serviceUnavailable);

            ExternalServerException exception = assertThrows(ExternalServerException.class, () -> travelContractService.requestFixedFee("123", "cardNumber"));

            assertThat(exception.getType(), is(ExceptionType.CONTACT_IT));
            assertThat(exception.getDetail(), is("business payment service is unavailable"));
        }
    }

    @Nested
    class RequestFixedFeeInvoice {
        @Test
        void should_return_FixedFeeInvoiceRequest_and_initiate_invoice_when_cid_is_valid() {
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
            when(invoiceClient.requestInvoice(any())).thenReturn(new RequestInvoiceResponse("invoiceId"));

            FixedFeeInvoiceRequest fixedFeeInvoiceRequest = travelContractService.requestFixedFeeInvoice("123", "tax123");

            verify(travelContractRepository).save(contractEntityCaptor.capture());
            assertThat(contractEntityCaptor.getValue().getFixedFeeInvoiceRequest(), is(notNullValue()));
            assertThat(fixedFeeInvoiceRequest.getRequestId(), is(notNullValue()));
            assertThat(fixedFeeInvoiceRequest.status(), is(FixedFeeInvoiceRequestStatus.PROCESSING));
        }

        @Test
        void should_return_FixedFeeInvoiceRequest_with_status_COMPLETED_when_contract_has_finish_fixed_fee_invoice() {
            FixedFeeConfirmationEntity fixedFeeConfirmation = FixedFeeConfirmationEntity.builder().fixedFeeAmount(BigDecimal.valueOf(1000)).build();
            FixedFeeInvoiceConfirmationEntity fixedFeeInvoiceConfirmation = FixedFeeInvoiceConfirmationEntity.builder().fixedFeeAmount(BigDecimal.valueOf(1000)).build();
            TravelContractEntity contract = TravelContractEntity.builder()
                    .cid("123")
                    .fixedFeeRequest(FixedFeeRequestEntity.builder()
                            .requestId("1-2-3")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .fixedFeeConfirmation(fixedFeeConfirmation)
                            .build())
                    .fixedFeeInvoiceRequest(FixedFeeInvoiceRequestEntity.builder()
                            .requestId("1-2-3")
                            .taxIdentifier("tax123")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .fixedFeeInvoiceConfirmation(fixedFeeInvoiceConfirmation)
                            .build())
                    .build();
            when(travelContractRepository.findByCid("123")).thenReturn(Optional.of(contract));

            FixedFeeInvoiceRequest fixedFeeInvoiceRequest = travelContractService.requestFixedFeeInvoice("123", "tax123");

            verify(travelContractRepository, never()).save(any());
            verify(invoiceClient, never()).requestInvoice(any());
            assertThat(fixedFeeInvoiceRequest.getRequestId(), is(notNullValue()));
            assertThat(fixedFeeInvoiceRequest.status(), is(FixedFeeInvoiceRequestStatus.COMPLETED));
        }

        @Test
        void should_throw_DataNotFoundException_when_cid_is_not_found() {
            when(travelContractRepository.findByCid("321")).thenReturn(Optional.empty());

            DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> travelContractService.requestFixedFeeInvoice("321", "cardNumber"));

            assertThat(exception.getType(), is(ExceptionType.DATA_NOT_FOUND));
            assertThat(exception.getDetail(), is("contract not found"));
        }

        @Test
        void should_BadRequestException_when_contract_has_not_finished_fixed_fee_payment() {
            TravelContractEntity contract = TravelContractEntity.builder()
                    .cid("123")
                    .fixedFeeRequest(FixedFeeRequestEntity.builder()
                            .requestId("1-2-3")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .build())
                    .build();
            when(travelContractRepository.findByCid("123")).thenReturn(Optional.of(contract));

            BadRequestException exception = assertThrows(BadRequestException.class, () -> travelContractService.requestFixedFeeInvoice("123", "tax123"));

            assertThat(exception.getType(), is(ExceptionType.INPUT_PARAM_INVALID));
            assertThat(exception.getDetail(), is("contract has not finished fixed fee payment"));
        }

        @Test
        void should_return_FixedFeeInvoiceRequest_when_invoice_service_is_unavailable() {
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
            FeignException.GatewayTimeout gatewayTimeout = givenGatewayTimeoutException();
            when(invoiceClient.requestInvoice(any())).thenThrow(gatewayTimeout);

            FixedFeeInvoiceRequest fixedFeeInvoiceRequest = travelContractService.requestFixedFeeInvoice("123", "tax123");

            verify(travelContractRepository).save(contractEntityCaptor.capture());
            assertThat(contractEntityCaptor.getValue().getFixedFeeInvoiceRequest(), is(notNullValue()));
            assertThat(fixedFeeInvoiceRequest.getRequestId(), is(notNullValue()));
            assertThat(fixedFeeInvoiceRequest.status(), is(FixedFeeInvoiceRequestStatus.PROCESSING));
        }

        @Test
        void should_return_FixedFeeInvoiceRequest_with_status_FAILED_when_has_retry_over_12_times_to_request_invoice_but_failed() {
            FixedFeeConfirmationEntity fixedFeeConfirmation = FixedFeeConfirmationEntity.builder().fixedFeeAmount(BigDecimal.valueOf(1000)).build();
            List<InvoiceRequestRetryRecordEntity> invoiceRequestRetryRecords = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                invoiceRequestRetryRecords.add(new InvoiceRequestRetryRecordEntity());
            }
            TravelContractEntity contract = TravelContractEntity.builder()
                    .cid("123")
                    .fixedFeeRequest(FixedFeeRequestEntity.builder()
                            .requestId("1-2-3")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .fixedFeeConfirmation(fixedFeeConfirmation)
                            .build())
                    .fixedFeeInvoiceRequest(FixedFeeInvoiceRequestEntity.builder()
                            .requestId("1-2-3")
                            .taxIdentifier("tax123")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .invoiceRequestRetryRecords(invoiceRequestRetryRecords)
                            .build())
                    .build();
            when(travelContractRepository.findByCid("123")).thenReturn(Optional.of(contract));

            FixedFeeInvoiceRequest fixedFeeInvoiceRequest = travelContractService.requestFixedFeeInvoice("123", "tax123");

            verify(travelContractRepository, never()).save(any());
            verify(invoiceClient, never()).requestInvoice(any());
            assertThat(fixedFeeInvoiceRequest.getRequestId(), is(notNullValue()));
            assertThat(fixedFeeInvoiceRequest.status(), is(FixedFeeInvoiceRequestStatus.FAILED));
        }
    }

    @Nested
    class RetryRequestFixedFeeInvoice {
        @Test
        void should_retry_call_invoice_service_success() {
            FixedFeeConfirmationEntity fixedFeeConfirmation = FixedFeeConfirmationEntity.builder().fixedFeeAmount(BigDecimal.valueOf(1000)).build();
            TravelContractEntity contract = TravelContractEntity.builder()
                    .cid("123")
                    .fixedFeeRequest(FixedFeeRequestEntity.builder()
                            .requestId("1-2-3")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .fixedFeeConfirmation(fixedFeeConfirmation)
                            .build())
                    .fixedFeeInvoiceRequest(FixedFeeInvoiceRequestEntity.builder()
                            .requestId("1-2-3")
                            .taxIdentifier("tax123")
                            .fixedFeeAmount(BigDecimal.valueOf(1000))
                            .build())
                    .build();
            when(travelContractRepository.findAll()).thenReturn(List.of(contract));
            when(invoiceClient.requestInvoice(any())).thenReturn(new RequestInvoiceResponse("invoiceId"));

            travelContractService.retryRequestFixedFeeInvoice();

            verify(travelContractRepository).save(contractEntityCaptor.capture());
            assertThat(contractEntityCaptor.getValue().getFixedFeeInvoiceRequest().getInvoiceRequestRetryRecords().size(), is(1));
        }
    }

    private FeignException.GatewayTimeout givenGatewayTimeoutException() {
        FeignException.GatewayTimeout gatewayTimeout = mock(FeignException.GatewayTimeout.class);
        lenient().when(gatewayTimeout.status()).thenReturn(504);
        return gatewayTimeout;
    }

    private FeignException.ServiceUnavailable givenServiceUnavailableException() {
        FeignException.ServiceUnavailable serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        when(serviceUnavailable.status()).thenReturn(503);
        return serviceUnavailable;
    }

    private FeignException.NotFound givenNotFoundException() {
        FeignException.NotFound notFound = mock(FeignException.NotFound.class);
        when(notFound.status()).thenReturn(404);
        return notFound;
    }
}