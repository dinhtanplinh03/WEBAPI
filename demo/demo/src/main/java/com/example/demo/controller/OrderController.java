package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.model.OrderRequest;
import com.example.demo.model.User;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    // Lấy tất cả đơn hàng (Admin)
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Lấy đơn hàng theo userId (User)
    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUser(@PathVariable Long userId) {
        return orderService.getOrdersByUser(userId);
    }

    // Tạo đơn hàng (User)
    @PostMapping()
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        Optional<User> userOpt = userRepository.findByEmail(orderRequest.getCustomerEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Người dùng không tồn tại!");
        }

        User user = userOpt.get(); // Tìm user theo email

        Order newOrder = new Order();
        newOrder.setUser(user); // GÁN USER
        newOrder.setTotalPrice(orderRequest.getTotalPrice());
        newOrder.setStatus(Order.Status.PENDING);

        orderRepository.save(newOrder); // LƯU ORDER

        return ResponseEntity.ok("Đặt hàng thành công!");
    }

    // Cập nhật trạng thái đơn hàng (Admin)
    @PutMapping("/{id}")
    public Order updateOrderStatus(@PathVariable Long id, @RequestBody Order order) {
        return orderService.updateOrderStatus(id, order);
    }

    // Xóa đơn hàng (Admin)
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
