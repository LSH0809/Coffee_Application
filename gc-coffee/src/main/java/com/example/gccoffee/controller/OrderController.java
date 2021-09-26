package com.example.gccoffee.controller;

import com.example.gccoffee.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public String viewOrderPage(Model model) {
        var orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);

        return "order-list";
    }

    @GetMapping("/delete-order/{orderId}")
    public String deleteOrerPage(@PathVariable("orderId") UUID orderId) {
        orderService.deleteById(orderId);

        return "redirect:/orders";
    }

}
