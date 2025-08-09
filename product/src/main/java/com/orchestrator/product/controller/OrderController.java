package com.orchestrator.product.controller;

import com.orchestrator.product.model.Order;
import com.orchestrator.product.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
@Slf4j
public class OrderController {
    private final OrderRepository orderRepository;

    @PostMapping
    public Mono<Order> createOrder(@RequestBody Order product) {
        return orderRepository.save(product);
    }
}
