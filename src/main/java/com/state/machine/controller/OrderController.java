package com.state.machine.controller;

import com.state.machine.entity.Order;
import com.state.machine.service.OrderService;
import com.state.machine.states.OrderStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/create")
    @PostMapping
    public ResponseEntity<String> createOrder() {
         orderService.createOrder(new Order(OrderStates.CREATED));
         return ResponseEntity.status(HttpStatus.CREATED).body("Order created");
    }

    @RequestMapping("/pay/{orderId}")
    @PostMapping
    public ResponseEntity<String> payOrder(@PathVariable Long orderId) {
         orderService.payOrder(orderId);
         return ResponseEntity.ok("Order paid");
    }
    @RequestMapping("/ship/{orderId}")
    @PostMapping
    public ResponseEntity<String> shipOrder(@PathVariable Long orderId) {
         orderService.shipOrder(orderId);
         return ResponseEntity.ok("Order shipped");
    }

    @RequestMapping("/deliver/{orderId}")
    @PostMapping
    public ResponseEntity<String> deliverOrder(@PathVariable Long orderId) {
         orderService.deliverOrder(orderId);
         return ResponseEntity.ok("Order delivered");
    }

    @RequestMapping("/complete/{orderId}")
    @PostMapping
    public ResponseEntity<String> completeOrder(@PathVariable Long orderId) {
         orderService.completeOrder(orderId);
         return ResponseEntity.ok("Order completed");
    }

    @RequestMapping("/cancel/{orderId}")
    @PostMapping
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
         orderService.cancelOrder(orderId);
         return ResponseEntity.ok("Order cancelled");
    }
}
