package com.royalhalalmeats.royalmeats.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "order_summary")
public class OrderSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private String transactionId;
    private double totalAmount;
    private String status;
    private String deliveryAddress;

    private String paymentStatus;   // PENDING | PAID | REFUNDED
    private String paymentMethod;   // CASH | ONLINE | CARD

    // 🆕 New delivery info fields
    private String mobile;
    private String community;
    private String deliveryDay;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "transactionId", referencedColumnName = "transactionId", insertable = false, updatable = false)
    private List<Order> items;
}
