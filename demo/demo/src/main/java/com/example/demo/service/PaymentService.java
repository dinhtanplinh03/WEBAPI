package com.example.demo.service;

import com.example.demo.model.Payment;
import com.example.demo.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public Optional<Payment> getPaymentByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}

