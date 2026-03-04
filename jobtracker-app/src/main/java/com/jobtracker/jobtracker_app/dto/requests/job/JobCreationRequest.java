package com.jobtracker.jobtracker_app.dto.requests.job;

import com.jobtracker.jobtracker_app.enums.JobStatus;
import com.jobtracker.jobtracker_app.enums.JobType;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobCreationRequest {

    @NotBlank(message = "{job.title.not_blank}")
    @Size(max = 255, message = "{job.title.size}")
    String title;

    @NotBlank(message = "{job.position.not_blank}")
    @Size(max = 255, message = "{job.position.size}")
    String position;

    @NotNull(message = "{job.job_type.not_null}")
    JobType jobType;

    @Size(max = 255, message = "{job.location.size}")
    String location;

    @DecimalMin(value = "0.0", message = "job.salary_min.min")
    BigDecimal salaryMin;

    @DecimalMin(value = "0.0", message = "job.salary_max.min")
    BigDecimal salaryMax;

    @Pattern(regexp = "USD|VND|EUR|GBP|JPY", message = "job.currency.pattern")
    String currency = "USD";

    JobStatus jobStatus = JobStatus.DRAFT;

    LocalDate deadlineDate;

    String jobDescription;

    String requirements;

    String benefits;

    @Size(max = 500, message = "{job.job_url.size}")
    @Pattern(regexp = "^(https?://).*$", message = "{job.job_url.pattern}")
    String jobUrl;

    Boolean isRemote = false;
}




