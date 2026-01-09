package com.royalhalalmeats.royalmeats.controller;

import com.royalhalalmeats.royalmeats.dto.AdminOrderView;
import com.royalhalalmeats.royalmeats.model.MeatItem;
import com.royalhalalmeats.royalmeats.model.Order;
import com.royalhalalmeats.royalmeats.model.OrderSummary;
import com.royalhalalmeats.royalmeats.model.User;
import com.royalhalalmeats.royalmeats.repository.MeatItemRepository;
import com.royalhalalmeats.royalmeats.repository.OrderRepository;
import com.royalhalalmeats.royalmeats.repository.OrderSummaryRepository;
import com.royalhalalmeats.royalmeats.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderRepository repo;
    private final OrderSummaryRepository summaryRepo;
    private final MeatItemRepository meatRepo;
    private final UserRepository userRepo;

    @Autowired
    public OrderController(OrderRepository repo,
                           OrderSummaryRepository summaryRepo,
                           MeatItemRepository meatRepo,
                           UserRepository userRepo) {
        this.repo = repo;
        this.summaryRepo = summaryRepo;
        this.meatRepo = meatRepo;
        this.userRepo = userRepo;
    }

    // ✅ Place a single order (used rarely)
    @PostMapping
    public Order placeOrder(@RequestBody Order order) {
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("RECEIVED");

        MeatItem meat = meatRepo.findById(order.getMeatId())
                .orElseThrow(() -> new RuntimeException("Meat not found for ID: " + order.getMeatId()));
        order.setTotalPrice(order.getQuantity() * meat.getPricePerLB());

        return repo.save(order);
    }

    // ✅ Get all orders for a specific customer
    @GetMapping("/{customerId}")
    public List<Order> getCustomerOrders(@PathVariable Long customerId) {
        return repo.findByCustomerId(customerId);
    }

    // ✅ Get all orders (raw list)
    @GetMapping
    public List<Order> getAllOrders() {
        return repo.findAll();
    }

    // ✅ Bulk order (checkout)
    @PostMapping("/bulk")
    public List<Order> placeBulkOrder(@RequestBody List<Order> orders) {
        String txnId = "TXN-" + System.currentTimeMillis();

        for (Order order : orders) {
            order.setTransactionId(txnId);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("RECEIVED");

            MeatItem meat = meatRepo.findById(order.getMeatId())
                    .orElseThrow(() -> new RuntimeException("Meat not found for ID: " + order.getMeatId()));
            order.setTotalPrice(order.getQuantity() * meat.getPricePerLB());
        }

        List<Order> savedOrders = repo.saveAll(orders);

        double totalAmount = savedOrders.stream()
                .mapToDouble(Order::getTotalPrice)
                .sum();

        Order first = savedOrders.get(0);
        OrderSummary summary = new OrderSummary();
        summary.setCustomerId(first.getCustomerId());
        summary.setTransactionId(txnId);
        summary.setTotalAmount(totalAmount);
        summary.setStatus("RECEIVED");
        summary.setDeliveryAddress(first.getDeliveryAddress());
        summary.setOrderDate(LocalDateTime.now());

        // 🔹 NEW FIELDS
        summary.setMobile(orders.get(0).getMobile());
        summary.setCommunity(orders.get(0).getCommunity());
        summary.setDeliveryDay(orders.get(0).getDeliveryDay());
        // ✅ Default Payment Fields
        summary.setPaymentStatus("PENDING");
        summary.setPaymentMethod("CASH");

        summaryRepo.save(summary);

        return savedOrders;
    }

    // ✅ Update weight, address, status, and payment info
    @PatchMapping("/{orderId}/update-weight")
    public Order updateOrderWeight(@PathVariable Long orderId, @RequestBody Map<String, Object> payload) {
        Order order = repo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        boolean updateSummaryStatus = false;

        // ✅ Quantity update
        if (payload.containsKey("quantity")) {
            double newQty = Double.parseDouble(payload.get("quantity").toString());
            order.setQuantity(newQty);
            MeatItem meat = meatRepo.findById(order.getMeatId())
                    .orElseThrow(() -> new RuntimeException("Meat not found"));
            order.setTotalPrice(newQty * meat.getPricePerLB());
        }

        // ✅ Status update
        if (payload.containsKey("status")) {
            order.setStatus(payload.get("status").toString());
            updateSummaryStatus = Boolean.parseBoolean(
                    payload.getOrDefault("updateSummaryStatus", "false").toString()
            );
        }

        // ✅ Address update
        if (payload.containsKey("deliveryAddress")) {
            order.setDeliveryAddress(payload.get("deliveryAddress").toString());
        }

        // ✅ Save updated order
        Order saved = repo.save(order);

        // ✅ Update summary totals and payment
        OrderSummary summary = summaryRepo.findByTransactionId(order.getTransactionId());
        double newTotal = repo.findByTransactionId(order.getTransactionId())
                .stream()
                .mapToDouble(Order::getTotalPrice)
                .sum();
        summary.setTotalAmount(newTotal);

        // ✅ Payment update (optional)
        if (payload.containsKey("paymentStatus")) {
            summary.setPaymentStatus(payload.get("paymentStatus").toString());
        }
        if (payload.containsKey("paymentMethod")) {
            summary.setPaymentMethod(payload.get("paymentMethod").toString());
        }

        // ✅ Update overall status (if whole order changed)
        if (updateSummaryStatus) {
            summary.setStatus(order.getStatus());
        }

        if (summary.getOrderDate() == null) {
            summary.setOrderDate(LocalDateTime.now());
        }

        summaryRepo.save(summary);

        return saved;
    }

    // ✅ Admin endpoint: view all orders with payment + meat info
    @GetMapping("/admin/all")
    public List<AdminOrderView> getAllOrdersForAdmin() {
        List<OrderSummary> summaries = summaryRepo.findAllByOrderByOrderDateDesc();
        List<AdminOrderView> views = new ArrayList<>();

        for (OrderSummary summary : summaries) {
            List<Order> items = repo.findByTransactionId(summary.getTransactionId());

            for (Order o : items) {
                MeatItem meat = meatRepo.findById(o.getMeatId()).orElse(null);
                o.setMeatName(meat != null ? meat.getName() : "Unknown Meat");
                o.setPricePerLB(meat != null ? meat.getPricePerLB() : 0.0);
            }

            User user = userRepo.findById(summary.getCustomerId()).orElse(null);

            AdminOrderView view = new AdminOrderView();
            view.setSummaryId(summary.getId());
            view.setTransactionId(summary.getTransactionId());
            view.setCustomerName(user != null ? user.getName() : "Unknown");
            view.setDeliveryAddress(summary.getDeliveryAddress());
            view.setStatus(summary.getStatus());
            view.setTotalAmount(summary.getTotalAmount());
            view.setOrderDate(summary.getOrderDate());
            view.setItems(items);

            // ✅ Payment Info
            view.setPaymentStatus(summary.getPaymentStatus());
            view.setPaymentMethod(summary.getPaymentMethod());

            // ✅ Add missing fields for frontend
            view.setMobile(summary.getMobile());
            view.setCommunity(summary.getCommunity());
            view.setDeliveryDay(summary.getDeliveryDay());

            views.add(view);
        }

        return views;
    }

}
