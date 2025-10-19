package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.model.ShippingOrderRequest;
import com.yugungsetia.ecommerce_simple.model.ShippingOrderResponse;
import com.yugungsetia.ecommerce_simple.model.ShippingRateRequest;
import com.yugungsetia.ecommerce_simple.model.ShippingRateResponse;

import java.math.BigDecimal;

public interface ShippingService {
    ShippingRateResponse calculateShippingRate(ShippingRateRequest request);

    ShippingOrderResponse createShippingOrder(ShippingOrderRequest request);

    String generateAwbNumber(Long orderId);

    BigDecimal calculateTotalWeight(Long orderId);
}
