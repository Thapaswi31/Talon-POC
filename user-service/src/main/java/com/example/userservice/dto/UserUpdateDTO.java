package com.example.userservice.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.PositiveOrZero;

public class UserUpdateDTO {
    private String name;
    @Email(message = "Email should be valid")
    private String email;
    private String phone;
    @PositiveOrZero
    private Integer totalOrders;
    @PositiveOrZero
    private Double totalSpent;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
    public Double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Double totalSpent) { this.totalSpent = totalSpent; }
}
