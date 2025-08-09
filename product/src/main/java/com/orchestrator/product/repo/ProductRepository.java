package com.orchestrator.product.repo;

import com.orchestrator.product.model.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    @Query("SELECT * FROM products ORDER BY id LIMIT :size OFFSET :offset")
    Flux<Product> findAllPaged(int offset, int size);
}
