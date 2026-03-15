package com.jobtracker.jobtracker_app.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.jobtracker_app.dto.requests.notification.NotificationRequest;
import com.jobtracker.jobtracker_app.dto.responses.notification.NotificationMarkAllReadResponse;
import com.jobtracker.jobtracker_app.dto.responses.notification.NotificationMarkReadResponse;
import com.jobtracker.jobtracker_app.dto.responses.notification.NotificationResponse;
import com.jobtracker.jobtracker_app.entities.*;
import com.jobtracker.jobtracker_app.enums.NotificationPriority;
import com.jobtracker.jobtracker_app.enums.NotificationType;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.NotificationMapper;
import com.jobtracker.jobtracker_app.repositories.*;
import com.jobtracker.jobtracker_app.services.NotificationService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements NotificationService {
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    UserRepository userRepository;
    CompanyRepository companyRepository;
    JobRepository jobRepository;
    ApplicationRepository applicationRepository;
    SecurityUtils securityUtils;
    ObjectMapper objectMapper;
    LocalizationUtils localizationUtils;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    public NotificationResponse create(NotificationRequest request) {
        User user = userRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(request.getUserId(), request.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Company company = companyRepository
                .findByIdAndDeletedAtIsNull(request.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_EXISTED));

        Notification notification = notificationMapper.toNotification(request);

        notification.setUser(user);
        notification.setCompany(company);
        
        if (request.getJobId() != null) {
            Job job = jobRepository
                    .findByIdAndCompany_IdAndDeletedAtIsNull(request.getJobId(), request.getCompanyId())
                    .orElseThrow(() -> new AppException(ErrorCode.JOB_NOT_EXISTED));

            notification.setJob(job);
        }
        if (request.getApplicationId() != null) {
            Application application = applicationRepository
                    .findByIdAndCompany_IdAndDeletedAtIsNull(request.getApplicationId(), request.getCompanyId())
                    .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_NOT_EXISTED));
            notification.setApplication(application);
        }
        
        return notificationMapper.toNotificationResponse(notificationRepository.save(notification), objectMapper);
    }

    @Override
    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    public NotificationResponse getById(String id) {
        Notification notification = getNotificationForCurrentCompanyUserOrThrow(id);

        return notificationMapper.toNotificationResponse(notification, objectMapper);
    }

    @Override
    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    public Page<NotificationResponse> getAll(String userId,
                                             String companyId,
                                             Boolean isRead,
                                             NotificationType type,
                                             String applicationId,
                                             Pageable pageable) {
        return notificationRepository
                .searchNotification(userId, companyId, isRead, type, applicationId, pageable)
                .map(noti -> notificationMapper.toNotificationResponse(noti, objectMapper));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('NOTIFICATION_DELETE')")
    public void delete(String id) {
        Notification notification = getNotificationForCurrentCompanyUserOrThrow(id);

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('NOTIFICATION_UPDATE')")
    public NotificationMarkReadResponse markNotification(String id) {
        Notification notification = getNotificationForCurrentCompanyUserOrThrow(id);

        notification.setIsRead(true);

        return NotificationMarkReadResponse.builder()
                .id(notification.getId())
                .isRead(notification.getIsRead())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('NOTIFICATION_UPDATE')")
    public NotificationMarkAllReadResponse markAllNotification() {
        User user = securityUtils.getCurrentUser();
        int updateCount  = notificationRepository
                .markAllAsRead(user.getCompany().getId(), user.getId());

        return NotificationMarkAllReadResponse.builder()
                .updateCount(updateCount)
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    public void sendNotification(User user,
                                 Company company,
                                 Job job,
                                 Application application,
                                 NotificationType type,
                                 NotificationPriority priority,
                                 String metadataJson) {
        String titleKey = resolveTitleKey(type);
        String messageKey = resolveMessageKey(type);
        Object[] titleArgs = resolveTitleArgs(type, job, application);
        Object[] messageArgs = resolveMessageArgs(type, job, application);
        String title = localizationUtils.getLocalizedMessage(titleKey, titleArgs);
        String message = localizationUtils.getLocalizedMessage(messageKey, messageArgs);

        Notification notification = Notification.builder()
                .user(user)
                .company(company)
                .job(job)
                .application(application)
                .type(type)
                .title(title)
                .message(message)
                .priority(priority)
                .metadata(metadataJson)
                .build();

        notificationRepository.save(notification);
    }

    private Notification getNotificationForCurrentCompanyUserOrThrow(String id){
        User user = securityUtils.getCurrentUser();

        return notificationRepository
                .findByIdAndCompany_IdAndUser_Id(id, user.getCompany().getId(), user.getId())
                .orElseThrow(()-> new AppException(ErrorCode.NOTIFICATION_NOT_EXISTED));
    }

    private String resolveTitleKey(NotificationType type) {
        if (type == NotificationType.APPLICATION_RECEIVED) {
            return MessageKeys.NOTIFICATION_APPLICATION_RECEIVED_TITLE;
        }
        if (type == NotificationType.STATUS_UPDATE) {
            return MessageKeys.NOTIFICATION_STATUS_UPDATE_TITLE;
        }
        if (type == NotificationType.INTERVIEW_SCHEDULED) {
            return MessageKeys.NOTIFICATION_INTERVIEW_SCHEDULED_TITLE;
        }
        if (type == NotificationType.INTERVIEW_REMINDER) {
            return MessageKeys.NOTIFICATION_INTERVIEW_REMINDER_TITLE;
        }
        if (type == NotificationType.DEADLINE_REMINDER) {
            return MessageKeys.NOTIFICATION_DEADLINE_REMINDER_TITLE;
        }
        return MessageKeys.NOTIFICATION_GENERIC_TITLE;
    }

    private String resolveMessageKey(NotificationType type) {
        if (type == NotificationType.APPLICATION_RECEIVED) {
            return MessageKeys.NOTIFICATION_APPLICATION_RECEIVED_MESSAGE;
        }
        if (type == NotificationType.STATUS_UPDATE) {
            return MessageKeys.NOTIFICATION_STATUS_UPDATE_MESSAGE;
        }
        if (type == NotificationType.INTERVIEW_SCHEDULED) {
            return MessageKeys.NOTIFICATION_INTERVIEW_SCHEDULED_MESSAGE;
        }
        if (type == NotificationType.INTERVIEW_REMINDER) {
            return MessageKeys.NOTIFICATION_INTERVIEW_REMINDER_MESSAGE;
        }
        if (type == NotificationType.DEADLINE_REMINDER) {
            return MessageKeys.NOTIFICATION_DEADLINE_REMINDER_MESSAGE;
        }
        return MessageKeys.NOTIFICATION_GENERIC_MESSAGE;
    }

    private Object[] resolveTitleArgs(NotificationType type, Job job, Application application) {
        if (type == NotificationType.APPLICATION_RECEIVED && job != null) {
            return new Object[]{job.getTitle()};
        }
        if (type == NotificationType.STATUS_UPDATE && application != null) {
            return new Object[]{application.getCandidateName()};
        }
        if (type == NotificationType.INTERVIEW_SCHEDULED && job != null) {
            return new Object[]{job.getTitle()};
        }
        if (type == NotificationType.DEADLINE_REMINDER && job != null) {
            return new Object[]{job.getTitle()};
        }
        return new Object[0];
    }

    private Object[] resolveMessageArgs(NotificationType type, Job job, Application application) {
        if (type == NotificationType.APPLICATION_RECEIVED && application != null && job != null) {
            return new Object[]{application.getCandidateName(), job.getTitle()};
        }
        if (type == NotificationType.STATUS_UPDATE && application != null && application.getStatus() != null) {
            return new Object[]{application.getCandidateName(), application.getStatus().getDisplayName()};
        }
        if (type == NotificationType.INTERVIEW_SCHEDULED && application != null && job != null) {
            return new Object[]{application.getCandidateName(), job.getTitle()};
        }
        if (type == NotificationType.INTERVIEW_REMINDER && application != null && job != null) {
            return new Object[]{application.getCandidateName(), job.getTitle()};
        }
        if (type == NotificationType.DEADLINE_REMINDER && job != null && job.getDeadlineDate() != null) {
            return new Object[]{job.getTitle(), job.getDeadlineDate()};
        }
        return new Object[0];
    }
}




