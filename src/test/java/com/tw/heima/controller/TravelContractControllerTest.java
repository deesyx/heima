package com.tw.heima.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.heima.controller.dto.request.RequestFixedFeeRequest;
import com.tw.heima.service.TravelContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
}