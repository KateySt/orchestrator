package com.orchestrator.product.controller;

import com.orchestrator.product.model.EventPayload;
import com.orchestrator.product.model.Product;
import com.orchestrator.product.repo.ProductRepository;
import com.orchestrator.product.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Slf4j
public class ProductController {
    private final ProductRepository productRepository;
    private final KafkaProducerService kafkaProducerService;

    @PostMapping
    public Mono<Product> createProduct(@RequestBody Product product) {
        return productRepository.save(product)
                .flatMap(savedProduct -> {
                    EventPayload event = new EventPayload(
                            "product-service",
                            "PRODUCT_CREATED",
                            savedProduct.getId() + ":" + savedProduct.getName()
                    );
                    return kafkaProducerService.send("event-topic", event)
                            .thenReturn(savedProduct);
                });
    }

    @GetMapping("/{id}")
    public Mono<Product> getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")));
    }

    @PatchMapping("/{id}")
    public Mono<Product> patchProduct(@PathVariable Long id, @RequestBody Product updatedProduct) {
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")))
                .flatMap(existingProduct -> {
                    existingProduct.setName(updatedProduct.getName());
                    existingProduct.setDescription(updatedProduct.getDescription());
                    existingProduct.setPrice(updatedProduct.getPrice());
                    existingProduct.setQuantityAvailable(updatedProduct.getQuantityAvailable());

                    return productRepository.save(existingProduct);
                });
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteProduct(@PathVariable Long id) {
        return productRepository.deleteById(id);
    }

    @GetMapping
    public Flux<Product> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        int offset = page * size;
        return productRepository.findAllPaged(offset, size);
    }
}
