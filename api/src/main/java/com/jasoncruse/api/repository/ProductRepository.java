package com.jasoncruse.api.repository;

import com.jasoncruse.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
