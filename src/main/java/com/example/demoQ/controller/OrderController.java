package com.example.demoQ.controller;

import com.example.demoQ.entity.Order;
import com.example.demoQ.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Order> get(Order order) {
        return orderService.getAll(order);
    }
}
