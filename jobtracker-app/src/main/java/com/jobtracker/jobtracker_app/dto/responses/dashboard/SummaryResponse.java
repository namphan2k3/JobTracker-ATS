package com.jobtracker.jobtracker_app.dto.responses.dashboard;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SummaryResponse {
    ActiveJobsResponse activeJobs;
    ApplicationsTodayResponse applicationsToday;
    List<ApplicationsByStatusResponse> applicationsByStatus;
    List<UpcomingInterviewsResponse> upcomingInterviews;
}
