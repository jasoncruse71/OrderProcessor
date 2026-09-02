package com.jasoncruse.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;
import java.util.List;

public class OrderRequest {

    @Email
    private String customerEmail;

    @NotEmpty
    @Valid
    private List<OrderItemRequest> items;

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}
