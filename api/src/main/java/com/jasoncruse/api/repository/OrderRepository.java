package com.jasoncruse.api.repository;

import com.jasoncruse.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomerEmail(String customerEmail);
}
