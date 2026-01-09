package com.royalhalalmeats.royalmeats.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private Long meatId;
    private double quantity;
    private double totalPrice;

    @Column(length = 30)
    private String status = "PLACED";

    private String deliveryAddress;
    private String transactionId;
    private LocalDateTime orderDate;

    // 🆕 New delivery info fields
    private String mobile;
    private String community;
    private String deliveryDay;

    // 🔹 Transient fields for frontend display
    @Transient
    private String meatName;

    @Transient
    private double pricePerLB;

    @Transient
    private double subTotal;
}
