package com.royalhalalmeats.royalmeats.repository;

import com.royalhalalmeats.royalmeats.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :start AND :end ORDER BY o.orderDate DESC")
    List<Order> findOrdersBetween(LocalDateTime start, LocalDateTime end);

    List<Order> findByStatusOrderByOrderDateDesc(String status);
    List<Order> findByTransactionId(String transactionId);

}
