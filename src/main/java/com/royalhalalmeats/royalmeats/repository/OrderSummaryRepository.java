package com.royalhalalmeats.royalmeats.repository;

import com.royalhalalmeats.royalmeats.model.OrderSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderSummaryRepository extends JpaRepository<OrderSummary, Long> {
    List<OrderSummary> findByCustomerIdOrderByOrderDateDesc(Long customerId);
    OrderSummary findByTransactionId(String transactionId);
    List<OrderSummary> findAllByOrderByOrderDateDesc();
}
