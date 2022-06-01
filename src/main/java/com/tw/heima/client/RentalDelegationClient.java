package com.tw.heima.client;

import com.tw.heima.client.dto.request.RentSeekingRequest;
import com.tw.heima.client.dto.response.RentSeekingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "rental-delegation-service", url = "${services.rental-delegation.url}")
public interface RentalDelegationClient {
    @PostMapping("/letting-orders/{rentalDelegationId}/rent-seeking/confirmation")
    RentSeekingResponse confirmRentSeeking(
            @PathVariable("rentalDelegationId") String rentalDelegationId,
            @RequestBody RentSeekingRequest rentSeeking
    );

    @GetMapping("letting-orders/{rentalDelegationId}/rent-seeking")
    RentSeekingResponse getRentSeeking(@PathVariable("rentalDelegationId") String rentalDelegationId);
}
