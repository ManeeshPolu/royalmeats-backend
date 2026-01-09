package com.royalhalalmeats.royalmeats.controller;

import com.royalhalalmeats.royalmeats.model.Order;
import com.royalhalalmeats.royalmeats.model.User;
import com.royalhalalmeats.royalmeats.repository.UserRepository;
import com.royalhalalmeats.royalmeats.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepo;
    private final OrderService orderService;

    public AdminController(UserRepository userRepo, OrderService orderService) {
        this.userRepo = userRepo;
        this.orderService = orderService;
    }

    // 🔹 Admin login remains same (simple)
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> creds) {
        String email = creds.get("email");
        String password = creds.get("password");

        Optional<User> admin = userRepo.findByEmail(email);
        if (admin.isPresent() && admin.get().getPassword().equals(password)
                && "ADMIN".equals(admin.get().getRole())) {
            return ResponseEntity.ok(Map.of("token", "ADMIN-" + UUID.randomUUID(), "admin", admin.get()));
        }
        return ResponseEntity.status(401).body("Unauthorized");
    }

    // 🔹 Users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    // 🔹 Orders
    @GetMapping("/orders")
    public List<Order> getOrders(@RequestParam(required = false) String filter,
                                 @RequestParam(required = false) String status) {
        if (status != null) return orderService.getOrdersByStatus(status);
        if ("week".equalsIgnoreCase(filter)) return orderService.getOrdersForWeek();
        return orderService.getAllOrders();
    }

    // 🔹 Update order status
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> req) {
        Order order = orderService.updateStatus(id, req.get("status"));
        return ResponseEntity.ok(Map.of("message", "Status updated", "order", order));
    }

    // 🔹 Delivery
    @GetMapping("/delivery/orders")
    public List<Order> getReadyForDelivery() {
        return orderService.getOrdersByStatus("READY");
    }

    @PutMapping("/delivery/orders/{id}/deliver")
    public ResponseEntity<?> markDelivered(@PathVariable Long id) {
        Order order = orderService.markDelivered(id);
        return ResponseEntity.ok(Map.of("message", "Order delivered", "order", order));
    }
}
