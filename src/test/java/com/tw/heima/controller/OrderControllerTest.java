package com.tw.heima.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.heima.controller.dto.request.OrderCreateRequest;
import com.tw.heima.exception.BadRequestException;
import com.tw.heima.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static com.tw.heima.exception.ExceptionType.INPUT_PARAM_INVALID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new CustomizedExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateOrderSuccess() throws Exception {
        when(orderService.createOrder(eq("M123456"), any())).thenReturn(1);

        mockMvc.perform(post("/renting-orders")
                        .header("clientId", "M123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        OrderCreateRequest.builder()
                                                .agentId("agentId")
                                                .mouthPrice(BigDecimal.valueOf(3000))
                                                .rentalDelegationId("rentalDelegationId")
                                                .build()
                                )
                        ))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1));
    }

    @Test
    void shouldThrowBadRequestException() throws Exception {
        when(orderService.createOrder(eq("M123456"), any())).thenThrow(new BadRequestException(INPUT_PARAM_INVALID, "agent id invalid"));

        mockMvc.perform(post("/renting-orders")
                        .header("clientId", "M123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                objectMapper.writeValueAsString(
                                        OrderCreateRequest.builder()
                                                .agentId("invalid agentId")
                                                .mouthPrice(BigDecimal.valueOf(3000))
                                                .rentalDelegationId("rentalDelegationId")
                                                .build()
                                )
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("000001"))
                .andExpect(jsonPath("$.msg").value("input param invalid"))
                .andExpect(jsonPath("$.detail").value("agent id invalid"));
    }
}