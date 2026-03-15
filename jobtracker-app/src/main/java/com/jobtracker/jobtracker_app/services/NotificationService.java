package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.notification.NotificationRequest;
import com.jobtracker.jobtracker_app.dto.responses.notification.NotificationMarkAllReadResponse;
import com.jobtracker.jobtracker_app.dto.responses.notification.NotificationMarkReadResponse;
import com.jobtracker.jobtracker_app.dto.responses.notification.NotificationResponse;
import com.jobtracker.jobtracker_app.enums.NotificationType;
import com.jobtracker.jobtracker_app.enums.NotificationPriority;
import com.jobtracker.jobtracker_app.entities.Application;
import com.jobtracker.jobtracker_app.entities.Company;
import com.jobtracker.jobtracker_app.entities.Job;
import com.jobtracker.jobtracker_app.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    NotificationResponse create(NotificationRequest request);
    NotificationResponse getById(String id);
    Page<NotificationResponse> getAll(String userId,
                                      String companyId,
                                      Boolean isRead,
                                      NotificationType type,
                                      String applicationId,
                                      Pageable pageable);
    void delete(String id);
    NotificationMarkReadResponse markNotification(String id);
    NotificationMarkAllReadResponse markAllNotification();
    void sendNotification(User user,
                          Company company,
                          Job job,
                          Application application,
                          NotificationType type,
                          NotificationPriority priority,
                          String metadataJson);
}





