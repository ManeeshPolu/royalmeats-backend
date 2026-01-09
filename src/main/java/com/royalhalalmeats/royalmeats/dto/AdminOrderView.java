package com.royalhalalmeats.royalmeats.dto;

import com.royalhalalmeats.royalmeats.model.Order;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public class AdminOrderView {

    private Long summaryId;
    private String transactionId;
    private String customerName;
    private String deliveryAddress;
    private String status;
    private double totalAmount;
    private List<Order> items;

    private String paymentStatus;
    private String paymentMethod;

    // ✅ NEW FIELDS
    private String mobile;
    private String community;
    private String deliveryDay;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;

    // === Getters & Setters ===
    public Long getSummaryId() { return summaryId; }
    public void setSummaryId(Long summaryId) { this.summaryId = summaryId; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public List<Order> getItems() { return items; }
    public void setItems(List<Order> items) { this.items = items; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    // ✅ New getters/setters
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getCommunity() { return community; }
    public void setCommunity(String community) { this.community = community; }

    public String getDeliveryDay() { return deliveryDay; }
    public void setDeliveryDay(String deliveryDay) { this.deliveryDay = deliveryDay; }
}
