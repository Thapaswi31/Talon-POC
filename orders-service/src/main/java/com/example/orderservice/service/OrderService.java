package com.example.orderservice.service;

import com.example.orderservice.dto.*;
import com.example.orderservice.entity.Order;
import com.example.orderservice.exception.OrderException;
import com.example.orderservice.feign.RewardsServiceClient;
import com.example.orderservice.feign.UserServiceClient;
import com.example.orderservice.kafka.OrderEventPublisher;
import com.example.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;
    private final RewardsServiceClient rewardsServiceClient;
    private final OrderEventPublisher orderEventPublisher;

    public OrderService(OrderRepository orderRepository,
                       UserServiceClient userServiceClient,
                       RewardsServiceClient rewardsServiceClient,
                       OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.userServiceClient = userServiceClient;
        this.rewardsServiceClient = rewardsServiceClient;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto requestDto) {
        try {
            // Fetch user details
            UserDto user = userServiceClient.getUserById(requestDto.getUserId());
            if (user == null) {
                throw new OrderException("User not found");
            }

            // Calculate discount
            DiscountDto discountDto = rewardsServiceClient.getDiscount(requestDto.getUserId(), requestDto.getAmount());
            BigDecimal discount = discountDto != null && discountDto.getDiscount() != null ? discountDto.getDiscount() : BigDecimal.ZERO;
            BigDecimal total = requestDto.getAmount().subtract(discount);

            // Create and save order
            Order order = new Order();
            order.setUserId(requestDto.getUserId());
            order.setProductCode(requestDto.getProductCode());
            order.setQuantity(requestDto.getQuantity());
            order.setAmount(requestDto.getAmount());
            order.setDiscount(discount);
            order.setTotal(total);
            order.setStatus("PLACED");
            order.setCreatedAt(LocalDateTime.now());
            Order savedOrder = orderRepository.save(order);

            // Publish event
            orderEventPublisher.publishOrderEvent(savedOrder);

            // Prepare response
            OrderResponseDto responseDto = new OrderResponseDto();
            responseDto.setOrderId(savedOrder.getId());
            responseDto.setUserId(savedOrder.getUserId());
            responseDto.setProductCode(savedOrder.getProductCode());
            responseDto.setQuantity(savedOrder.getQuantity());
            responseDto.setAmount(savedOrder.getAmount());
            responseDto.setDiscount(savedOrder.getDiscount());
            responseDto.setTotal(savedOrder.getTotal());
            responseDto.setStatus(savedOrder.getStatus());
            responseDto.setCreatedAt(savedOrder.getCreatedAt());
            return responseDto;
        } catch (Exception ex) {
            logger.error("Error placing order", ex);
            throw new OrderException("Failed to place order: " + ex.getMessage());
        }
    }
}
