package com.jasoncruse.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderResponse {

    private UUID orderId;
    private String status;
    private BigDecimal totalAmount;

    public OrderResponse(UUID orderId, String status, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public UUID getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
}
