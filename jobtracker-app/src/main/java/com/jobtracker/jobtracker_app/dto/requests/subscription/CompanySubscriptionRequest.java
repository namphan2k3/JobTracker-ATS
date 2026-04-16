package com.jobtracker.jobtracker_app.dto.requests.subscription;

import com.jobtracker.jobtracker_app.enums.SubscriptionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanySubscriptionRequest {

    @NotBlank(message = "{company_subscription.company_id.not_blank}")
    String companyId;

    @NotBlank(message = "{company_subscription.plan_id.not_blank}")
    String planId;

    @NotNull(message = "{company_subscription.start_date.not_null}")
    LocalDateTime startDate;

    LocalDateTime endDate;

    SubscriptionStatus status;
}


