package com.royalhalalmeats.royalmeats.service;


import com.royalhalalmeats.royalmeats.model.Order;
import com.royalhalalmeats.royalmeats.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class OrderService {

    private final OrderRepository repo;

    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    /** ✅ Get all orders for the current week */
    public List<Order> getOrdersForWeek() {
        LocalDateTime startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).with(LocalTime.MAX);
        return repo.findOrdersBetween(startOfWeek, endOfWeek);
    }

    /** ✅ Get all orders by status */
    public List<Order> getOrdersByStatus(String status) {
        return repo.findByStatusOrderByOrderDateDesc(status);
    }

    /** ✅ Update order status safely */
    public Order updateStatus(Long orderId, String newStatus) {
        Optional<Order> opt = repo.findById(orderId);
        if (opt.isEmpty()) throw new RuntimeException("Order not found: " + orderId);

        Order order = opt.get();
        order.setStatus(newStatus);
        repo.save(order);
        return order;
    }

    /** ✅ Mark order delivered (helper) */
    public Order markDelivered(Long orderId) {
        return updateStatus(orderId, "DELIVERED");
    }

    /** ✅ Get all orders */
    public List<Order> getAllOrders() {
        return repo.findAll();
    }
}
