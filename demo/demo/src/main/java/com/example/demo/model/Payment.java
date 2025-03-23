package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    private Method paymentMethod;

    @Enumerated(EnumType.STRING)
    private Status paymentStatus = Status.PENDING;

    private Boolean blocked = false;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Method {
        COD, CREDIT_CARD, PAYPAL
    }

    public enum Status {
        PENDING, COMPLETED, FAILED
    }
}

