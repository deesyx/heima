package com.tw.heima.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.heima.client.dto.request.RequestInvoiceRequest;
import com.tw.heima.client.dto.response.RequestInvoiceResponse;
import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
class InvoiceClientTest {

    private ClientAndServer server;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private InvoiceClient invoiceClient;

    @BeforeEach
    void setUp() {
        server = ClientAndServer.startClientAndServer(9002);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void should_request_invoice_success() throws JsonProcessingException {
        RequestInvoiceRequest request = RequestInvoiceRequest.builder()
                .requestId("1-2-3")
                .amount(BigDecimal.valueOf(1000))
                .identifier("tax123")
                .build();
        RequestInvoiceResponse response = RequestInvoiceResponse.builder().invoiceId("invoiceId").build();
        server.when(request()
                        .withMethod("POST")
                        .withPath("/invoices")
                        .withBody(objectMapper.writeValueAsString(request)))
                .respond(response()
                        .withStatusCode(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(response)));

        RequestInvoiceResponse requestInvoiceResponse = invoiceClient.requestInvoice(request);

        assertThat(requestInvoiceResponse.getInvoiceId(), is("invoiceId"));
    }

    @Test
    void should_throw_GatewayTimeout_exception() throws JsonProcessingException {
        RequestInvoiceRequest request = RequestInvoiceRequest.builder()
                .requestId("1-2-3")
                .amount(BigDecimal.valueOf(1000))
                .identifier("tax123")
                .build();
        RequestInvoiceResponse response = RequestInvoiceResponse.builder().invoiceId("invoiceId").build();
        server.when(request()
                        .withMethod("POST")
                        .withPath("/invoices")
                        .withBody(objectMapper.writeValueAsString(request)))
                .respond(response()
                        .withStatusCode(504));

        assertThrows(FeignException.GatewayTimeout.class, () -> invoiceClient.requestInvoice(request));
    }
}