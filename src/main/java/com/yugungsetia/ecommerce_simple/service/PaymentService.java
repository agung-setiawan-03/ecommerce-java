package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.entity.Order;
import com.yugungsetia.ecommerce_simple.model.PaymentNotification;
import com.yugungsetia.ecommerce_simple.model.PaymentResponse;

public interface PaymentService {

    PaymentResponse create(Order order);

    PaymentResponse findByPaymentId(String paymentId);

    boolean verifyByPaymentId(String paymentId);

    void handleNotification(PaymentNotification paymentNotification);
}
