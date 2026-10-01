package com.jasoncruse.api.service;

import com.jasoncruse.api.dto.OrderItemRequest;
import com.jasoncruse.api.dto.OrderRequest;
import com.jasoncruse.api.dto.OrderResponse;
import com.jasoncruse.api.model.*;
import com.jasoncruse.api.repository.OrderRepository;
import com.jasoncruse.api.repository.ProductRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final StringRedisTemplate redisTemplate;


    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, StringRedisTemplate redisTemplate) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
    }

    public OrderResponse createOrder(OrderRequest request) {
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemReq.getProductId()));

            if (product.getStockQuantity() < itemReq.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - itemReq.getQuantity());
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(product.getPrice());

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
            orderItems.add(item);
        }

        Order order = new Order();
        order.setCustomerEmail(request.getCustomerEmail());
        order.setStatus("PENDING");
        order.setTotalAmount(total);
        order.setItems(orderItems);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        Order saved = orderRepository.save(order);

        redisTemplate.opsForStream().add("order_queue", Map.of("order_id", saved.getId().toString()));

        return new OrderResponse(saved.getId(), saved.getStatus(), saved.getTotalAmount());
    }
}
