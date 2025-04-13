package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonProperty("userId") // Trả về userId thay vì toàn bộ User object
    private User user;

    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Format ngày tháng khi trả về JSON
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public enum Status {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELED
    }
}
