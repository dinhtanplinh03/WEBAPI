package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    private String customerEmail;
    private Double totalPrice;
}

