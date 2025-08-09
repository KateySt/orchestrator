package com.orchestrator.product.model;

import lombok.Data;

@Data
public class PaymentForm {
    private String method;
    private String amount;
    private String currency;
    private String description;
    private String cancelUrl;
    private String successUrl;
}
