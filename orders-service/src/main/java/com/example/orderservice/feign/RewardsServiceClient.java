package com.example.orderservice.feign;

import com.example.orderservice.dto.DiscountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "rewards-service", url = "${rewards-service.url}")
public interface RewardsServiceClient {
    @GetMapping("/rewards/discount")
    DiscountDto getDiscount(@RequestParam("userId") Long userId, @RequestParam("amount") java.math.BigDecimal amount);
}
