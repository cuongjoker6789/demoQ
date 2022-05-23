package com.example.demoQ.controller;

import com.example.demoQ.entity.Order;
import com.example.demoQ.model.OrderDTO;
import com.example.demoQ.model.PageResult;
import com.example.demoQ.model.Report;
import com.example.demoQ.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Tuple;
import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public PageResult<Order> get(OrderDTO orderDTO) {

        return orderService.getAll(orderDTO);
    }

    @GetMapping("/totalCash")
    public Report getTotalCash() {
        return orderService.getReport();
    }
    @GetMapping("/totalPayments")
    public List<Tuple> getNumberTotal(){
        return  orderService.getNumberTotal();
    }

}
