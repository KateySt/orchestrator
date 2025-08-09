package com.orchestrator.product.controller;

import com.orchestrator.product.model.PaymentForm;
import com.orchestrator.product.service.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaypalController {

    private final PaypalService paypalService;

    @GetMapping("/")
    public Mono<String> home() {
        return Mono.just("index");
    }

    @PostMapping("/payment/create")
    public Mono<String> createPayment(@ModelAttribute PaymentForm form) {
        //todo user balance logic
        return Mono.fromCallable(() -> {
                    Payment payment = paypalService.createPayment(
                            Double.parseDouble(form.getAmount()),
                            form.getCurrency(),
                            form.getMethod(),
                            "sale",
                            form.getDescription(),
                            form.getCancelUrl() != null ? form.getCancelUrl() : "http://localhost:8080/payment/cancel",
                            form.getSuccessUrl() != null ? form.getSuccessUrl() : "http://localhost:8080/payment/success"
                    );

                    String approvalUrl = payment.getLinks().stream()
                            .filter(link -> "approval_url".equals(link.getRel()))
                            .findFirst()
                            .map(Links::getHref)
                            .orElse("/payment/error");

                    return "redirect:" + approvalUrl;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorReturn("redirect:/payment/error");
    }

    @GetMapping("/payment/success")
    public Mono<String> paymentSuccess(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId
    ) {
        return Mono.fromCallable(() -> {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            return "approved".equals(payment.getState()) ? "paymentSuccess" : "paymentError";
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/payment/cancel")
    public Mono<String> cancel() {
        return Mono.just("paymentCancel");
    }

    @GetMapping("/payment/error")
    public Mono<String> error() {
        return Mono.just("paymentError");
    }
}
