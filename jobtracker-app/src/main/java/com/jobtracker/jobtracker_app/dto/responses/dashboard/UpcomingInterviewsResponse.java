package com.jobtracker.jobtracker_app.dto.responses.dashboard;

import com.jobtracker.jobtracker_app.enums.InterviewType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpcomingInterviewsResponse {
    String id;
    String candidateName;
    String jobTitle;
    LocalDateTime scheduledDate;
    Long durationMinutes;
    InterviewType interviewType;
}
