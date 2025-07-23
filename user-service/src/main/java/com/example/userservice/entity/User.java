package com.example.userservice.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    private String phone;

    @NotNull
    @PositiveOrZero
    private Integer totalOrders = 0;

    @NotNull
    @PositiveOrZero
    private Double totalSpent = 0.0;

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public Integer getTotalOrders() {
        return totalOrders;
    }
    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }
    public Double getTotalSpent() {
        return totalSpent;
    }
    public void setTotalSpent(Double totalSpent) {
        this.totalSpent = totalSpent;
    }
}
