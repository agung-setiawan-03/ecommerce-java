package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.entity.Order;
import com.yugungsetia.ecommerce_simple.model.CheckoutRequest;
import com.yugungsetia.ecommerce_simple.model.OrderItemResponse;
import com.yugungsetia.ecommerce_simple.model.OrderResponse;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    OrderResponse checkout(CheckoutRequest checkoutRequest);

    Optional<Order> findOrderById(Long orderId);

    List<Order> findOrdersByUserId(Long userId);

    List<Order> findOrdersByStatus(String status);

    void cancelOrder(Long orderId);

    List<OrderItemResponse> findOrderItemsByOrderId(Long orderId);

    void updateOrderStatus(Long orderId, String newStatus);

    Double calculateOrderTotal(Long orderId);
}
