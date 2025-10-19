package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.common.errors.ResourceNotFoundException;
import com.yugungsetia.ecommerce_simple.entity.Order;
import com.yugungsetia.ecommerce_simple.entity.OrderItem;
import com.yugungsetia.ecommerce_simple.entity.Product;
import com.yugungsetia.ecommerce_simple.model.ShippingOrderRequest;
import com.yugungsetia.ecommerce_simple.model.ShippingOrderResponse;
import com.yugungsetia.ecommerce_simple.model.ShippingRateRequest;
import com.yugungsetia.ecommerce_simple.model.ShippingRateResponse;
import com.yugungsetia.ecommerce_simple.repository.OrderItemRepository;
import com.yugungsetia.ecommerce_simple.repository.OrderRepository;
import com.yugungsetia.ecommerce_simple.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockShippingServiceImpl implements ShippingService {

    private static final BigDecimal BASE_RATE = BigDecimal.valueOf(10000);
    private static final BigDecimal RATE_PER_KG = BigDecimal.valueOf(2500);
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Override
    public ShippingRateResponse calculateShippingRate(ShippingRateRequest request) {
        BigDecimal shippingFee = BASE_RATE.add(request.getTotalWeightInGrams()
                        .divide(BigDecimal.valueOf(1000)).multiply(RATE_PER_KG))
                .setScale(2, RoundingMode.HALF_UP);

        String estimatedDeliveryTime = "3 - 5 hari kerja";
        return ShippingRateResponse
                .builder()
                .shippingFee(shippingFee)
                .estimatedDeliveryTime(estimatedDeliveryTime)
                .build();
    }

    @Override
    public ShippingOrderResponse createShippingOrder(ShippingOrderRequest request) {
        String awbNumber = generateAwbNumber(request.getOrderId());

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order dengan id " + request.getOrderId() + " tidak ditemukan"));

        order.setStatus("SHIPPING");
        order.setAwbNumber(awbNumber);
        orderRepository.save(order);

        String estimatedDeliveryTime = "3 - 5 hari kerja";
        return ShippingOrderResponse
                .builder()
                .awbNumber(awbNumber)
                .estimatedDeliveryTime(estimatedDeliveryTime)
                .build();
    }

    @Override
    public String generateAwbNumber(Long orderId) {
        Random random = new Random();
        String prefix = "AWB";
        return String.format("%s%011d", prefix, random.nextInt(1000000000));
    }

    @Override
    public BigDecimal calculateTotalWeight(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItems.stream()
                .map(orderItem -> {
                    Product product = productRepository.findById(orderItem.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product dengan id " + orderItem.getProductId() + " tidak ditemukan"));

                    BigDecimal totalWeight = product.getWeight().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
                    return totalWeight;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
