package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.common.errors.ResourceNotFoundException;
import com.yugungsetia.ecommerce_simple.entity.*;
import com.yugungsetia.ecommerce_simple.model.CheckoutRequest;
import com.yugungsetia.ecommerce_simple.model.OrderItemResponse;
import com.yugungsetia.ecommerce_simple.model.ShippingRateRequest;
import com.yugungsetia.ecommerce_simple.model.ShippingRateResponse;
import com.yugungsetia.ecommerce_simple.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserAddressRepository userAddressRepository;
    private final ProductRepository productRepository;
    private final ShippingService shippingService;

    private final BigDecimal TAX_RATE = BigDecimal.valueOf(0.03);


    @Override
    @Transactional
    public Order checkout(CheckoutRequest checkoutRequest) {
        List<CartItem> selectedItems = cartItemRepository.findAllById(checkoutRequest.getSelectedCartItemIds());
        if (selectedItems.isEmpty()) {
            throw new ResourceNotFoundException("Tidak ada item di keranjang belanja untuk checkout");
        }

        UserAddress shippingAddress = userAddressRepository.findById(checkoutRequest.getUserAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Alamat pengiriman dengan id " + checkoutRequest.getUserAddressId() + " tidak ditemukan"));

        Order newOrder = Order
                .builder()
                .userId(checkoutRequest.getUserId())
                .status("PENDING")
                .orderDate(LocalDateTime.now())
                .totalAmount(BigDecimal.ZERO)
                .taxFee(BigDecimal.ZERO)
                .subTotal(BigDecimal.ZERO)
                .shippingFee(BigDecimal.ZERO)
                .build();

        Order saveOrder = orderRepository.save(newOrder);

        List<OrderItem> orderItems = selectedItems.stream()
                .map(cartItem -> {
                    return OrderItem.builder()
                            .orderId(saveOrder.getOrderId())
                            .productId(cartItem.getProductId())
                            .quantity(cartItem.getQuantity())
                            .price(cartItem.getPrice())
                            .userAddressId(shippingAddress.getUserAddressId())
                            .build();
                }).toList();

        orderItemRepository.saveAll(orderItems);

        cartItemRepository.deleteAll(selectedItems);

        BigDecimal subTotal = orderItems.stream()
                .map(orderItem -> orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingFee = orderItems.stream()
                .map(orderItem -> {
                    Optional<Product> product = productRepository.findById(orderItem.getProductId());
                    if (product.isEmpty()) {
                        return BigDecimal.ZERO;
                    }

                    Optional<UserAddress> sellerAddress = userAddressRepository.findByUserIdAndIsDefaultTrue(product.get().getUserId());
                    if (sellerAddress.isEmpty()) {
                        return BigDecimal.ZERO;
                    }

                    BigDecimal totalWeight = product.get().getWeight()
                            .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

                    ShippingRateRequest rateRequest = ShippingRateRequest
                            .builder()
                            .totalWeightInGrams(totalWeight)
                            .fromAddress(ShippingRateRequest.fromUserAddress(sellerAddress.get()))
                            .toAddress(ShippingRateRequest.fromUserAddress(shippingAddress))
                            .build();

                    ShippingRateResponse rateResponse = shippingService.calculateShippingRate(rateRequest);
                    return rateResponse.getShippingFee();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxFee = subTotal.multiply(TAX_RATE);
        BigDecimal totalAmount = subTotal.add(taxFee).add(shippingFee);

        saveOrder.setSubTotal(subTotal);
        saveOrder.setShippingFee(shippingFee);
        saveOrder.setTaxFee(taxFee);
        saveOrder.setTotalAmount(totalAmount);

        return orderRepository.save(saveOrder);
    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> findOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> findOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order dengan id " + orderId + " tidak ditemukan"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalStateException("Hanya status PENDING yang dapat di cancel");
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    @Override
    public List<OrderItemResponse> findOrderItemsByOrderId(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        if (orderItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> productIds = orderItems.stream()
                .map(OrderItem::getProductId)
                .toList();
        List<Long> shippingAddressIds = orderItems.stream()
                .map(OrderItem::getUserAddressId)
                .toList();

        List<Product> products = productRepository.findAllById(productIds);
        List<UserAddress> shippingAddress = userAddressRepository.findAllById(shippingAddressIds);

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));
        Map<Long, UserAddress> userAddressMap = shippingAddress.stream()
                .collect(Collectors.toMap(UserAddress::getUserAddressId, Function.identity()));

        return orderItems.stream()
                .map(orderItem -> {
                    Product product = productMap.get(orderItem.getProductId());
                    UserAddress userAddress = userAddressMap.get(orderItem.getUserAddressId());

                    if (product == null) {
                        throw new ResourceNotFoundException("Produk dengan id " + orderItem.getProductId() + " tidak ditemukan");
                    }

                    if (userAddress == null) {
                        throw new ResourceNotFoundException("Alamat user dengan id " + orderItem.getUserAddressId() + " tidak ditemukan");
                    }

                    return OrderItemResponse.fromOrderItemProductAndAddress(orderItem, product, userAddress);
                })
                .toList();
    }

    @Override
    public void updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order dengan id " + orderId + " tidak ditemukan"));

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    @Override
    public Double calculateOrderTotal(Long orderId) {
        return orderItemRepository.calculateTotalOrder(orderId);
    }
}
