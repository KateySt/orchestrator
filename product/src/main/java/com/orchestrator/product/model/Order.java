package com.orchestrator.product.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table("orders")
public class Order {

    @Id
    private Long id;

    private String buyerId;

    private Instant orderDate;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private List<OrderItem> items;
}
