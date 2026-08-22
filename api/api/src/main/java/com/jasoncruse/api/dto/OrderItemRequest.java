package com.jasoncruse.api.dto;

import java.util.UUID;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotNull;

public class OrderItemRequest {

    @NotNull
    private UUID productId;

    @Positive
    private Integer quantity;

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
