package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.application_status.ApplicationStatusCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.application_status.ApplicationStatusUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.application_status.ApplicationStatusResponse;

import java.util.List;

public interface ApplicationStatusService {
    ApplicationStatusResponse create(ApplicationStatusCreationRequest request);

    ApplicationStatusResponse getById(String id);

    List<ApplicationStatusResponse> getAll();

    ApplicationStatusResponse update(String id, ApplicationStatusUpdateRequest request);

    void delete(String id);
}

