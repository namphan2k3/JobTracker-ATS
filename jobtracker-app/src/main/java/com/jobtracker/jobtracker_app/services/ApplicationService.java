package com.jobtracker.jobtracker_app.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jobtracker.jobtracker_app.dto.requests.application.*;
import com.jobtracker.jobtracker_app.dto.responses.application.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface ApplicationService {
    void ApplyToJob(ApplyToJobRequest request, String jobId) throws IOException;

    UploadAttachmentsResponse UploadAttachments(UploadAttachmentsRequest request, String applicationToken)
            throws IOException;

    TrackStatusResponse TrackStatus(String applicationToken);

    AssignApplicationResponse AssignApplication(String id, AssignApplicationRequest request);

    Page<ApplicationResponse> getApplications(ApplicationFilterRequest filter, Pageable pageable);

    ApplicationResponse getApplicationById(String id);

    ApplicationResponse createApplication(ApplicationCreateRequest request);

    UpdateApplicationStatusResponse updateStatus(String id, ApplicationUpdateStatusRequest request) throws JsonProcessingException;

    ApplicationResponse updateApplication(String id, ApplicationUpdateRequest request);

    void deleteApplication(String id);

    List<ApplicationStatusHistoryResponse> ApplicationStatusHistory(String id);
}
