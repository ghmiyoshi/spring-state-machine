package com.state.machine.controller;

import com.state.machine.entity.Order;
import com.state.machine.service.OrderService;
import com.state.machine.states.OrderStates;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<String> createOrder() {
         orderService.createOrder(new Order(OrderStates.CREATED));
         return ResponseEntity.status(HttpStatus.CREATED).body("Order created");
    }

    @PostMapping("/pay/{orderId}")
    public ResponseEntity<String> payOrder(@PathVariable Long orderId) {
         orderService.payOrder(orderId);
         return ResponseEntity.ok("Order paid");
    }

    @PostMapping("/ship/{orderId}")
    public ResponseEntity<String> shipOrder(@PathVariable Long orderId) {
         orderService.shipOrder(orderId);
         return ResponseEntity.ok("Order shipped");
    }

    @PostMapping("/deliver/{orderId}")
    public ResponseEntity<String> deliverOrder(@PathVariable Long orderId) {
         orderService.deliverOrder(orderId);
         return ResponseEntity.ok("Order delivered");
    }

    @PostMapping("/complete/{orderId}")
    public ResponseEntity<String> completeOrder(@PathVariable Long orderId) {
         orderService.completeOrder(orderId);
         return ResponseEntity.ok("Order completed");
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
         orderService.cancelOrder(orderId);
         return ResponseEntity.ok("Order cancelled");
    }
}
