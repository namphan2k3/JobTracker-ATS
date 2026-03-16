package com.jobtracker.jobtracker_app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewFilterRequest;
import com.jobtracker.jobtracker_app.dto.requests.interview.InterviewUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.interview.InterviewResponse;
import com.jobtracker.jobtracker_app.services.InterviewService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping
public class InterviewController {
    InterviewService interviewService;
    LocalizationUtils localizationUtils;

    @GetMapping("/interviews")
    public ApiResponse<List<InterviewResponse>> getAllInterviews(
            @ModelAttribute InterviewFilterRequest filter,
            Pageable pageable) {
        Page<InterviewResponse> page = interviewService.getAllInterviews(filter, pageable);
        return ApiResponse.<List<InterviewResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_LIST_SUCCESS))
                .data(page.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .build())
                .build();
    }

    @GetMapping("/applications/{applicationId}/interviews")
    public ApiResponse<List<InterviewResponse>> getByApplication(@PathVariable String applicationId) {
        return ApiResponse.<List<InterviewResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_LIST_SUCCESS))
                .data(interviewService.getAll(applicationId))
                .build();
    }

    @PostMapping("/applications/{applicationId}/interviews")
    public ApiResponse<InterviewResponse> create(
            @PathVariable String applicationId,
            @RequestBody @Valid InterviewCreationRequest request) throws JsonProcessingException {
        return ApiResponse.<InterviewResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_CREATE_SUCCESS))
                .data(interviewService.create(request, applicationId))
                .build();
    }

    @GetMapping("/interviews/{id}")
    public ApiResponse<InterviewResponse> getById(@PathVariable String id) {
        return ApiResponse.<InterviewResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_DETAIL_SUCCESS))
                .data(interviewService.getById(id))
                .build();
    }

    @PutMapping("/interviews/{id}")
    public ApiResponse<InterviewResponse> update(
            @PathVariable String id,
            @RequestBody @Valid InterviewUpdateRequest request) throws JsonProcessingException {
        return ApiResponse.<InterviewResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_UPDATE_SUCCESS))
                .data(interviewService.update(id, request))
                .build();
    }

    @DeleteMapping("/interviews/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        interviewService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_DELETE_SUCCESS))
                .build();
    }

    @PostMapping("/interviews/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable String id) {
        interviewService.cancel(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.INTERVIEW_CANCEL_SUCCESS))
                .build();
    }
}





