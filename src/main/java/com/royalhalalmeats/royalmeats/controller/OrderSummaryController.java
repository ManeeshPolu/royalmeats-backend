package com.royalhalalmeats.royalmeats.controller;

import com.royalhalalmeats.royalmeats.model.*;
import com.royalhalalmeats.royalmeats.repository.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/order-summary")
@CrossOrigin(origins = "*")
public class OrderSummaryController {

    private final OrderRepository orderRepo;
    private final OrderSummaryRepository summaryRepo;
    private final MeatItemRepository meatRepo;

    public OrderSummaryController(OrderRepository orderRepo,
                                  OrderSummaryRepository summaryRepo,
                                  MeatItemRepository meatRepo) {
        this.orderRepo = orderRepo;
        this.summaryRepo = summaryRepo;
        this.meatRepo = meatRepo;
    }

    // ✅ Fetch all summaries for a customer, each with its line items enriched
    @GetMapping("/{customerId}")
    public List<OrderSummary> getOrderSummaryForCustomer(@PathVariable Long customerId) {
        List<OrderSummary> summaries = summaryRepo.findByCustomerIdOrderByOrderDateDesc(customerId);

        for (OrderSummary summary : summaries) {
            // Ensure payment fields are never null
            if (summary.getPaymentStatus() == null) {
                summary.setPaymentStatus("PENDING");
            }
            if (summary.getPaymentMethod() == null) {
                summary.setPaymentMethod("CASH");
            }

            // Attach enriched order items
            List<Order> items = orderRepo.findByCustomerId(customerId).stream()
                    .filter(o -> summary.getTransactionId().equals(o.getTransactionId()))
                    .toList();

            for (Order item : items) {
                meatRepo.findById(item.getMeatId()).ifPresent(meat -> {
                    item.setMeatName(meat.getName());
                    item.setPricePerLB(meat.getPricePerLB());
                    item.setSubTotal(item.getPricePerLB() * item.getQuantity());
                });
            }

            summary.setItems(items);
        }

        return summaries;
    }

    // ✅ Optional: create summary directly (still works for manual creation)
    @PostMapping("/create")
    public OrderSummary createOrderSummary(@RequestBody Map<String, Object> request) {
        Long customerId = Long.parseLong(request.get("customerId").toString());
        String transactionId = request.get("transactionId").toString();

        List<Order> orders = orderRepo.findByCustomerId(customerId).stream()
                .filter(o -> transactionId.equals(o.getTransactionId()))
                .toList();

        double total = orders.stream().mapToDouble(Order::getTotalPrice).sum();

        OrderSummary summary = new OrderSummary();
        summary.setCustomerId(customerId);
        summary.setTransactionId(transactionId);
        summary.setTotalAmount(total);
        summary.setStatus("PLACED");
        summary.setDeliveryAddress(orders.isEmpty() ? null : orders.get(0).getDeliveryAddress());
        summary.setOrderDate(orders.isEmpty() ? null : orders.get(0).getOrderDate());
        return summaryRepo.save(summary);
    }
}
