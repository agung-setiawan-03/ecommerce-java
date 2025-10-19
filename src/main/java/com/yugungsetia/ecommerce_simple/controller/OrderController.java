package com.yugungsetia.ecommerce_simple.controller;

import com.yugungsetia.ecommerce_simple.entity.Order;
import com.yugungsetia.ecommerce_simple.model.*;
import com.yugungsetia.ecommerce_simple.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("orders")
@SecurityRequirement(name = "Bearer")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CheckoutRequest checkoutRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        checkoutRequest.setUserId(userInfo.getUser().getUserId());
        OrderResponse orderResponse = orderService.checkout(checkoutRequest);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> findOrderById(@PathVariable Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        return orderService.findOrderById(orderId)
                .map(order -> {
                    if (!order.getUserId().equals(userInfo.getUser().getUserId())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(OrderResponse.builder().build());
                    }
                    OrderResponse response = OrderResponse.fromOrder(order);
                    return ResponseEntity.ok(response);
                }).orElse(ResponseEntity.notFound().build());

    }

    @GetMapping("")
    public ResponseEntity<List<OrderResponse>> findOrderByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        List<Order> userOrders = orderService.findOrdersByUserId(userInfo.getUser().getUserId());
        List<OrderResponse> orderResponses =  userOrders.stream()
                .map(OrderResponse::fromOrder)
                .toList();

        return ResponseEntity.ok(orderResponses);
    }


    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemResponse>> findOrderItems(@PathVariable Long orderId) {
        List<OrderItemResponse> orderItemResponses = orderService.findOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(orderItemResponses);
    }


    @PutMapping("/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long orderId, @RequestParam String newStatus) {
        orderService.updateOrderStatus(orderId, newStatus);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{orderId}/total")
    public ResponseEntity<Double> calculateOrderTotal(@PathVariable Long orderId) {
        double total = orderService.calculateOrderTotal(orderId);
        return ResponseEntity.ok(total);
    }

}
