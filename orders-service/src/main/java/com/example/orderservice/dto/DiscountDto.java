package com.example.orderservice.dto;

import java.math.BigDecimal;

public class DiscountDto {
    private BigDecimal discount;

    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
}
