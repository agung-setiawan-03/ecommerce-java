package com.yugungsetia.ecommerce_simple.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;


    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "shipping_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingFee;

    @Column(name = "tax_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxFee;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String status;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "awb_number")
    private String awbNumber;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
