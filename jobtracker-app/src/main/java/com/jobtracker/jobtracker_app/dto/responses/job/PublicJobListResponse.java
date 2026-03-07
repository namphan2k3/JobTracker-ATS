package com.jobtracker.jobtracker_app.dto.responses.job;

import com.jobtracker.jobtracker_app.enums.JobType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicJobListResponse {
    String id;
    String title;
    String position;
    JobType jobType;
    String location;
    BigDecimal salaryMin;
    BigDecimal salaryMax;
    String currency;
    LocalDate deadlineDate;
    String companyId;
    String companyName;
    LocalDateTime publishedAt;
    Boolean isRemote;
    String jobUrl;
}
