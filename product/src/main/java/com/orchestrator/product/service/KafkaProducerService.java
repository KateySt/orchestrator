package com.orchestrator.product.service;

import com.orchestrator.product.model.EventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final ReactiveKafkaProducerTemplate<String, EventPayload> kafkaProducer;

    public Mono<Void> send(String topic, EventPayload payload) {
        return kafkaProducer.send(topic, payload.source(), payload)
                .doOnNext(senderResult -> System.out.println("Sent: " + payload))
                .then();
    }
}