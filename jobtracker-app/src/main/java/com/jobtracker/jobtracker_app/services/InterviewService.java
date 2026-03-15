package com.jobtracker.jobtracker_app.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewFilterRequest;
import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.interview.InterviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InterviewService {
    InterviewResponse create(InterviewCreationRequest request, String applicationId) throws JsonProcessingException;
    InterviewResponse getById(String id);
    List<InterviewResponse> getAll(String applicationId);
    Page<InterviewResponse> getAllInterviews(InterviewFilterRequest filter, Pageable pageable);
    InterviewResponse update(String id, InterviewUpdateRequest request) throws JsonProcessingException;
    void delete(String id);
    void cancel(String id);
}





