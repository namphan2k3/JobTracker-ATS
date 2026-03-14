package com.jobtracker.jobtracker_app.dto.requests.interview;

import com.jobtracker.jobtracker_app.enums.InterviewStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InterviewFilterRequest {
    String applicationId;
    String jobId;
    String interviewerId;
    LocalDateTime from;
    LocalDateTime to;
    InterviewStatus status;
}
