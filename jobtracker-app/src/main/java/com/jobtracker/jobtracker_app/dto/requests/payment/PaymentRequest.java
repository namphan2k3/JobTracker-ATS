package com.jobtracker.jobtracker_app.dto.requests.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {

    @NotBlank(message = "{payment.company_id.not_blank}")
    String companyId;

    @NotBlank(message = "{payment.subscription_id.not_blank}")
    String companySubscriptionId;

    @NotNull(message = "{payment.amount.not_null}")
    @Min(value = 0, message = "{payment.amount.min}")
    BigDecimal amount;

    String currency;

    String gateway;

    String txnRef;
}


