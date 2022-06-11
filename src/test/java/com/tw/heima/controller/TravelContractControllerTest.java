package com.tw.heima.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.heima.controller.dto.request.RequestFixedFeeInvoiceRequest;
import com.tw.heima.controller.dto.request.RequestFixedFeeRequest;
import com.tw.heima.exception.BadRequestException;
import com.tw.heima.exception.DataNotFoundException;
import com.tw.heima.exception.ExceptionType;
import com.tw.heima.exception.ExternalServerException;
import com.tw.heima.service.TravelContractService;
import com.tw.heima.service.model.FixedFeeInvoiceConfirmation;
import com.tw.heima.service.model.FixedFeeInvoiceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.tw.heima.exception.ExceptionType.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TravelContractControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private TravelContractController travelContractController;

    @Mock
    private TravelContractService travelContractService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(travelContractController)
                .setControllerAdvice(new CustomizedExceptionHandler())
                .build();
    }

    @Nested
    class RequestFixdFee {
        @Test
        void should_request_fixd_fee_success() throws Exception {
            RequestFixedFeeRequest request = new RequestFixedFeeRequest("cardNumber");
            when(travelContractService.requestFixdFee("123", "cardNumber")).thenReturn("1-2-3");

            mockMvc.perform(post("/travel-contracts/123/fixd-fee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.requestId").value("1-2-3"));
        }

        @Test
        void should_return_404_exception_response_when_cid_not_found() throws Exception {
            RequestFixedFeeRequest request = new RequestFixedFeeRequest("cardNumber");
            when(travelContractService.requestFixdFee("321", "cardNumber"))
                    .thenThrow(new DataNotFoundException(DATA_NOT_FOUND, "contract not found"));

            mockMvc.perform(post("/travel-contracts/321/fixd-fee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("000002"))
                    .andExpect(jsonPath("$.msg").value("data not found"))
                    .andExpect(jsonPath("$.detail").value("contract not found"));
        }

        @Test
        void should_return_400_exception_response_when_contract_has_finish_payment() throws Exception {
            RequestFixedFeeRequest request = new RequestFixedFeeRequest("cardNumber");
            when(travelContractService.requestFixdFee("123", "cardNumber"))
                    .thenThrow(new BadRequestException(INPUT_PARAM_INVALID, "contract has finish payment"));

            mockMvc.perform(post("/travel-contracts/123/fixd-fee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("000001"))
                    .andExpect(jsonPath("$.msg").value("input param invalid"))
                    .andExpect(jsonPath("$.detail").value("contract has finish payment"));
        }

        @Test
        void should_return_ExternalServerException_with_RETRY_LATER_response_when_business_payment_service_is_temporarily_unavailable() throws Exception {
            RequestFixedFeeRequest request = new RequestFixedFeeRequest("cardNumber");
            when(travelContractService.requestFixdFee("123", "cardNumber"))
                    .thenThrow(new ExternalServerException(RETRY_LATTER, "business payment service is temporarily unavailable"));

            mockMvc.perform(post("/travel-contracts/123/fixd-fee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value("000003"))
                    .andExpect(jsonPath("$.msg").value("please retry later"))
                    .andExpect(jsonPath("$.detail").value("business payment service is temporarily unavailable"));
        }

        @Test
        void should_return_ExternalServerException_with_CONTACT_IT_response_when_business_payment_service_is_unavailable() throws Exception {
            RequestFixedFeeRequest request = new RequestFixedFeeRequest("cardNumber");
            when(travelContractService.requestFixdFee("123", "cardNumber"))
                    .thenThrow(new ExternalServerException(ExceptionType.CONTACT_IT, "business payment service is unavailable"));

            mockMvc.perform(post("/travel-contracts/123/fixd-fee")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value("000004"))
                    .andExpect(jsonPath("$.msg").value("please contact with IT"))
                    .andExpect(jsonPath("$.detail").value("business payment service is unavailable"));
        }
    }

    @Nested
    class RequestFixdFeeInvoice {
        @Test
        void should_request_fixd_fee_invoice_return_processing() throws Exception {
            RequestFixedFeeInvoiceRequest request = new RequestFixedFeeInvoiceRequest("tax123");
            FixedFeeInvoiceRequest fixedFeeInvoiceRequest = FixedFeeInvoiceRequest.builder().requestId("1-2-3").build();
            when(travelContractService.requestFixdFeeInvoice("123", "tax123")).thenReturn(fixedFeeInvoiceRequest);

            mockMvc.perform(post("/travel-contracts/123/fixd-fee-invoice")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.requestId").value("1-2-3"))
                    .andExpect(jsonPath("$.status").value("PROCESSING"));
        }

        @Test
        void should_request_fixd_fee_invoice_return_completed() throws Exception {
            RequestFixedFeeInvoiceRequest request = new RequestFixedFeeInvoiceRequest("tax123");
            FixedFeeInvoiceRequest fixedFeeInvoiceRequest = FixedFeeInvoiceRequest.builder()
                    .requestId("1-2-3")
                    .fixedFeeInvoiceConfirmation(FixedFeeInvoiceConfirmation.builder().build())
                    .build();
            when(travelContractService.requestFixdFeeInvoice("123", "tax123")).thenReturn(fixedFeeInvoiceRequest);

            mockMvc.perform(post("/travel-contracts/123/fixd-fee-invoice")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.requestId").value("1-2-3"))
                    .andExpect(jsonPath("$.status").value("COMPLETED"));
        }

        @Test
        void should_return_404_exception_response_when_cid_not_found() throws Exception {
            RequestFixedFeeInvoiceRequest request = new RequestFixedFeeInvoiceRequest("tax123");
            when(travelContractService.requestFixdFeeInvoice("123", "tax123")).thenThrow(new DataNotFoundException(DATA_NOT_FOUND, "contract not found"));

            mockMvc.perform(post("/travel-contracts/123/fixd-fee-invoice")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(jsonPath("$.code").value("000002"))
                    .andExpect(jsonPath("$.msg").value("data not found"))
                    .andExpect(jsonPath("$.detail").value("contract not found"));
        }

        @Test
        void should_return_400_exception_response_when_contract_has_not_finished_fixed_fee_payment() throws Exception {
            RequestFixedFeeInvoiceRequest request = new RequestFixedFeeInvoiceRequest("tax123");
            when(travelContractService.requestFixdFeeInvoice("123", "tax123")).thenThrow(new BadRequestException(INPUT_PARAM_INVALID, "contract has not finished fixed fee payment"));

            mockMvc.perform(post("/travel-contracts/123/fixd-fee-invoice")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(jsonPath("$.code").value("000001"))
                    .andExpect(jsonPath("$.msg").value("input param invalid"))
                    .andExpect(jsonPath("$.detail").value("contract has not finished fixed fee payment"));
        }
    }
}