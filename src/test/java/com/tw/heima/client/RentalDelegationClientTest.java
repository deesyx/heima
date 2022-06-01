package com.tw.heima.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.trafficlistener.ConsoleNotifyingWiremockNetworkTrafficListener;
import com.tw.heima.client.dto.request.RentSeekingRequest;
import com.tw.heima.client.dto.response.RentSeekingResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableFeignClients(basePackages = "com.tw.heima.client.*")
class RentalDelegationClientTest {

    private static final WireMockConfiguration wireMockConfiguration = WireMockConfiguration
            .wireMockConfig().networkTrafficListener(
                    new ConsoleNotifyingWiremockNetworkTrafficListener()).port(8081);
    private static final WireMockServer wireMockServer = new WireMockServer(wireMockConfiguration);

    @Autowired
    private RentalDelegationClient rentalDelegationClient;

    @BeforeAll
    static void startWireMock() {
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @Test
    void shouldConfirmRentSeekingSuccess() {
        RentSeekingResponse response = rentalDelegationClient
                .confirmRentSeeking(
                        "rentalDelegationId",
                        RentSeekingRequest.builder().orderId(1).build());
        assertThat(response.getRentId(), is("123456"));
    }

    @Test
    void shouldGetRentSeekingSuccess() {
        RentSeekingResponse response = rentalDelegationClient
                .getRentSeeking("rentalDelegationId");
        assertThat(response.getRentId(), is("123456"));
    }

}