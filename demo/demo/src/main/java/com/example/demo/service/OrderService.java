package com.example.demo.service;

import com.example.demo.model.Order;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    // Lấy danh sách tất cả đơn hàng (Admin)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Lấy danh sách đơn hàng theo User ID (User)
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // Tạo đơn hàng (User)
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    // Cập nhật trạng thái đơn hàng (Admin)
    public Order updateOrderStatus(Long id, Order updatedOrder) {
        Optional<Order> existingOrder = orderRepository.findById(id);
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            order.setStatus(updatedOrder.getStatus()); // Cập nhật trạng thái
            return orderRepository.save(order);
        }
        return null; // Hoặc ném ra Exception nếu đơn hàng không tồn tại
    }

    // Xóa đơn hàng (Admin)
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}
