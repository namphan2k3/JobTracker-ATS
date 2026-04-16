package com.jobtracker.jobtracker_app.dto.requests.subscription;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPlanCreationRequest {
    @NotBlank(message = "{subscription_plan.code.not_blank}")
    @Size(max = 50, message = "{subscription_plan.code.size}")
    String code;

    @NotBlank(message = "{subscription_plan.name.not_blank}")
    @Size(max = 100, message = "{subscription_plan.name.size}")
    String name;

    @NotNull(message = "{subscription_plan.price.not_null}")
    @DecimalMin(value = "0.0", inclusive = true, message = "{subscription_plan.price.min}")
    BigDecimal price;

    @NotNull(message = "{subscription_plan.duration_days.not_null}")
    @Min(value = 0, message = "{subscription_plan.duration_days.min}")
    Integer durationDays;

    Integer maxJobs;

    Integer maxUsers;

    Integer maxApplications;

    Boolean isActive;
}


