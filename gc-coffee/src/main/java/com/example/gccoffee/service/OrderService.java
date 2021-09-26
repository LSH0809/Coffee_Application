package com.example.gccoffee.service;

import com.example.gccoffee.model.Email;
import com.example.gccoffee.model.Order;
import com.example.gccoffee.model.OrderItem;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order createOrder(Email email, String address, String postcode, List<OrderItem> orderItems);

    List<Order> getAllOrders();

    void deleteById(UUID orderId);
}
