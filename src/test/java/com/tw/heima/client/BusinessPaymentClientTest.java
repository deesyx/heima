package com.tw.heima.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.heima.client.dto.request.RequestPaymentRequest;
import com.tw.heima.client.dto.response.RequestPaymentResponse;
import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableFeignClients(basePackages = "com.tw.heima.client.*")
class BusinessPaymentClientTest {

    private ClientAndServer server;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private BusinessPaymentClient businessPaymentClient;

    @BeforeEach
    void setUp() {
        server = ClientAndServer.startClientAndServer(9001);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Nested
    class RequestPayment {
        @Test
        void should_request_payment_success() throws JsonProcessingException {
            RequestPaymentRequest request = RequestPaymentRequest.builder()
                    .requestId("1-2-3")
                    .amount(BigDecimal.valueOf(1000))
                    .destinationCardNumber("cardNumber")
                    .build();
            RequestPaymentResponse response = RequestPaymentResponse.builder().paymentId("paymentId").build();
            server.when(request()
                            .withMethod("POST")
                            .withPath("/union-pay/payments")
                            .withBody(objectMapper.writeValueAsString(request)))
                    .respond(response()
                            .withStatusCode(201)
                            .withHeader("Content-Type", "application/json")
                            .withBody(objectMapper.writeValueAsString(response)));

            RequestPaymentResponse requestPaymentResponse = businessPaymentClient.requestPayment(request);

            assertThat(requestPaymentResponse.getPaymentId(), is("paymentId"));
        }

        @Test
        void should_throw_GatewayTimeout_exception() throws JsonProcessingException {
            RequestPaymentRequest request = RequestPaymentRequest.builder()
                    .requestId("1-2-3")
                    .amount(BigDecimal.valueOf(1000))
                    .destinationCardNumber("cardNumber")
                    .build();
            server.when(request()
                            .withMethod("POST")
                            .withPath("/union-pay/payments")
                            .withBody(objectMapper.writeValueAsString(request)))
                    .respond(response()
                            .withStatusCode(504));

            assertThrows(FeignException.GatewayTimeout.class, () -> businessPaymentClient.requestPayment(request));
        }
    }

    @Nested
    class GetPaymentRequest {
        @Test
        void should_get_payment_request_success() throws JsonProcessingException {
            RequestPaymentResponse response = RequestPaymentResponse.builder().paymentId("paymentId").build();
            server.when(request()
                            .withMethod("GET")
                            .withPath("/union-pay/payments/1-2-3"))
                    .respond(response()
                            .withStatusCode(201)
                            .withHeader("Content-Type", "application/json")
                            .withBody(objectMapper.writeValueAsString(response)));

            RequestPaymentResponse requestPaymentResponse = businessPaymentClient.getPaymentRequest("1-2-3");

            assertThat(requestPaymentResponse.getPaymentId(), is("paymentId"));
        }
    }
}