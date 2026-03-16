package com.jobtracker.jobtracker_app.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.jobtracker_app.entities.Interview;
import com.jobtracker.jobtracker_app.entities.Job;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.enums.InterviewStatus;
import com.jobtracker.jobtracker_app.enums.NotificationPriority;
import com.jobtracker.jobtracker_app.enums.NotificationType;
import com.jobtracker.jobtracker_app.repositories.InterviewRepository;
import com.jobtracker.jobtracker_app.repositories.JobRepository;
import com.jobtracker.jobtracker_app.services.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationScheduler {

    JobRepository jobRepository;
    InterviewRepository interviewRepository;
    NotificationService notificationService;
    ObjectMapper objectMapper;

    @NonFinal
    @Value("${scheduler.notifications.deadline-reminder-days:3}")
    int deadlineReminderDays;

    @NonFinal
    @Value("${scheduler.notifications.interview-reminder-minutes:60}")
    int interviewReminderMinutes;

    // 9h sáng mỗi ngày
    @Scheduled(cron = "${scheduler.notifications.deadline-reminder-cron:0 0 9 * * ?}")
    @Transactional
    public void sendJobDeadlineReminders() {
        LocalDate targetDate = LocalDate.now().plusDays(deadlineReminderDays);
        List<Job> jobs = jobRepository.findByDeadlineDate(targetDate);
        for (Job job : jobs) {
            User owner = job.getUser();
            if (owner == null) {
                // bỏ qua job này vì ko biết owner là ai để gửi
                continue;
            }
            try {
                String metadataJson = objectMapper.writeValueAsString(
                        Map.of(
                                "jobId", job.getId(),
                                "jobTitle", job.getTitle(),
                                "deadlineDate", job.getDeadlineDate()
                        )
                );
                notificationService.sendNotification(
                        owner,
                        job.getCompany(),
                        job,
                        null,
                        NotificationType.DEADLINE_REMINDER,
                        NotificationPriority.MEDIUM,
                        metadataJson
                );
            } catch (Exception e) {
                log.warn("Failed to send deadline reminder notification for jobId={}", job.getId(), e);
            }
        }
    }

    // chạy mỗi 5 phút
    @Scheduled(cron = "${scheduler.notifications.interview-reminder-cron:0 */5 * * * ?}")
    @Transactional
    public void sendInterviewReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.plusMinutes(interviewReminderMinutes);
        LocalDateTime to = from.plusMinutes(5);
        List<Interview> interviews = interviewRepository.findRemindersWindow(
                InterviewStatus.SCHEDULED,
                from,
                to
        );
        for (Interview interview : interviews) {
            if (interview.getApplication() == null || interview.getApplication().getAssignedTo() == null) {
                continue;
            }
            User assignee = interview.getApplication().getAssignedTo();
            try {
                String metadataJson = objectMapper.writeValueAsString(
                        Map.of(
                                "interviewId", interview.getId(),
                                "applicationId", interview.getApplication().getId(),
                                "jobId", interview.getJob().getId(),
                                "scheduledDate", interview.getScheduledDate()
                        )
                );
                notificationService.sendNotification(
                        assignee,
                        interview.getCompany(),
                        interview.getJob(),
                        interview.getApplication(),
                        NotificationType.INTERVIEW_REMINDER,
                        NotificationPriority.HIGH,
                        metadataJson
                );
            } catch (Exception e) {
                log.warn("Failed to send interview reminder notification for interviewId={}", interview.getId(), e);
            }
        }
    }
}

