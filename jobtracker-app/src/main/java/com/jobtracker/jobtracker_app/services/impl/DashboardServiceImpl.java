package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.responses.dashboard.*;
import com.jobtracker.jobtracker_app.entities.Interview;
import com.jobtracker.jobtracker_app.enums.InterviewStatus;
import com.jobtracker.jobtracker_app.enums.JobStatus;
import com.jobtracker.jobtracker_app.repositories.ApplicationRepository;
import com.jobtracker.jobtracker_app.repositories.ApplicationStatusRepository;
import com.jobtracker.jobtracker_app.repositories.InterviewRepository;
import com.jobtracker.jobtracker_app.repositories.JobRepository;
import com.jobtracker.jobtracker_app.services.DashboardService;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardServiceImpl implements DashboardService {

    JobRepository jobRepository;
    ApplicationRepository applicationRepository;
    ApplicationStatusRepository applicationStatusRepository;
    InterviewRepository interviewRepository;
    SecurityUtils securityUtils;

    @Override
    @PreAuthorize("hasAuthority('JOB_READ') or hasAuthority('APPLICATION_READ')")
    public SummaryResponse summary() {
        String companyId = securityUtils.getCurrentUser().getCompany().getId();

        ActiveJobsResponse activeJobs = buildActiveJobs(companyId);
        ApplicationsTodayResponse applicationsToday = buildApplicationsToday(companyId);
        List<ApplicationsByStatusResponse> applicationsByStatus = buildApplicationsByStatus(companyId);
        List<UpcomingInterviewsResponse> upcomingInterviews = buildUpcomingInterviews(companyId);

        return SummaryResponse.builder()
                .activeJobs(activeJobs)
                .applicationsToday(applicationsToday)
                .applicationsByStatus(applicationsByStatus)
                .upcomingInterviews(upcomingInterviews)
                .build();
    }

    private ActiveJobsResponse buildActiveJobs(String companyId) {
        long count = jobRepository.countByCompany_IdAndJobStatusAndDeletedAtIsNull(companyId, JobStatus.PUBLISHED);
        long thisMonth = jobRepository.countPublishedThisMonth(companyId);
        long lastMonth = jobRepository.countPublishedLastMonth(companyId);
        long diff = thisMonth - lastMonth;
        String changeFromLastMonth = diff > 0 ? "+" + diff : String.valueOf(diff);

        return ActiveJobsResponse.builder()
                .count(count)
                .changeFromLastMonth(changeFromLastMonth)
                .build();
    }

    private ApplicationsTodayResponse buildApplicationsToday(String companyId) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        long countToday = applicationRepository.countByCompany_IdAndAppliedDateAndDeletedAtIsNull(companyId, today);
        long countYesterday = applicationRepository.countByCompany_IdAndAppliedDateAndDeletedAtIsNull(companyId, yesterday);

        return ApplicationsTodayResponse.builder()
                .count(countToday)
                .countYesterday(countYesterday)
                .build();
    }

    private List<ApplicationsByStatusResponse> buildApplicationsByStatus(String companyId) {
        List<ApplicationsByStatusResponse> statusCounts = applicationRepository.countByStatusGroupByCompany(companyId);
        Map<String, Long> countByStatusId = statusCounts.stream()
                .collect(Collectors.toMap(ApplicationsByStatusResponse::getStatusId, ApplicationsByStatusResponse::getCount));

        return applicationStatusRepository.findActiveStatuses(companyId).stream()
                .map(status -> ApplicationsByStatusResponse.builder()
                        .statusId(status.getId())
                        .statusName(status.getName())
                        .displayName(status.getDisplayName())
                        .count(countByStatusId.getOrDefault(status.getId(), 0L))
                        .build())
                .toList();
    }

    private List<UpcomingInterviewsResponse> buildUpcomingInterviews(String companyId) {
        List<Interview> interviews = interviewRepository.findUpcomingByCompany(
                companyId,
                InterviewStatus.SCHEDULED,
                LocalDateTime.now(),
                PageRequest.of(0, 5)
        );

        return interviews.stream()
                .map(i -> UpcomingInterviewsResponse.builder()
                        .id(i.getId())
                        .candidateName(i.getApplication().getCandidateName())
                        .jobTitle(i.getJob().getTitle())
                        .scheduledDate(i.getScheduledDate())
                        .durationMinutes(i.getDurationMinutes() != null ? i.getDurationMinutes().longValue() : null)
                        .interviewType(i.getInterviewType())
                        .build())
                .toList();
    }
}
