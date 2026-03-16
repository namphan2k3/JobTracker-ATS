package com.jobtracker.jobtracker_app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jobtracker.jobtracker_app.dto.requests.application.*;
import com.jobtracker.jobtracker_app.dto.responses.application.*;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.services.ApplicationService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationController {
    ApplicationService applicationService;
    LocalizationUtils localizationUtils;

    @PostMapping("/public/jobs/{jobId}/apply")
    public ApiResponse<Void> applyToJob(
            @PathVariable String jobId,
            @ModelAttribute @Valid ApplyToJobRequest request) throws IOException {
        applicationService.ApplyToJob(request, jobId);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_CREATE_SUCCESS))
                .build();
    }

    @PostMapping("/public/applications/{applicationToken}/attachments")
    public ApiResponse<UploadAttachmentsResponse> uploadAttachments(
            @PathVariable String applicationToken,
            @ModelAttribute @Valid UploadAttachmentsRequest request) throws IOException {
        UploadAttachmentsResponse response = applicationService.UploadAttachments(request, applicationToken);
        return ApiResponse.<UploadAttachmentsResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ATTACHMENT_CREATE_SUCCESS))
                .data(response)
                .build();
    }

    @GetMapping("/public/applications/{applicationToken}/status")
    public ApiResponse<TrackStatusResponse> trackStatus(@PathVariable String applicationToken) {
        TrackStatusResponse response = applicationService.TrackStatus(applicationToken);
        return ApiResponse.<TrackStatusResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_DETAIL_SUCCESS))
                .data(response)
                .build();
    }

    @GetMapping("/applications")
    public ApiResponse<List<ApplicationResponse>> getAll(
            @ModelAttribute ApplicationFilterRequest filter,
            Pageable pageable) {
        Page<ApplicationResponse> page = applicationService.getApplications(filter, pageable);
        return ApiResponse.<List<ApplicationResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_LIST_SUCCESS))
                .data(page.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .build())
                .build();
    }

    @GetMapping("/applications/{id}")
    public ApiResponse<ApplicationResponse> getById(@PathVariable String id) {
        return ApiResponse.<ApplicationResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_DETAIL_SUCCESS))
                .data(applicationService.getApplicationById(id))
                .build();
    }

    @PostMapping("/applications")
    public ApiResponse<ApplicationResponse> create(@RequestBody @Valid ApplicationCreateRequest request) {
        return ApiResponse.<ApplicationResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_CREATE_SUCCESS))
                .data(applicationService.createApplication(request))
                .build();
    }

    @PatchMapping("/applications/{id}/status")
    public ApiResponse<UpdateApplicationStatusResponse> updateStatus(
            @PathVariable String id,
            @RequestBody @Valid ApplicationUpdateStatusRequest request) throws JsonProcessingException {
        return ApiResponse.<UpdateApplicationStatusResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_STATUS_UPDATE_SUCCESS))
                .data(applicationService.updateStatus(id, request))
                .build();
    }

    @PatchMapping("/applications/{id}/assign")
    public ApiResponse<AssignApplicationResponse> assign(
            @PathVariable String id,
            @RequestBody AssignApplicationRequest request) {
        AssignApplicationResponse response = applicationService.AssignApplication(id, request);
        return ApiResponse.<AssignApplicationResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_ASSIGN_SUCCESS))
                .data(response)
                .build();
    }

    @PutMapping("/applications/{id}")
    public ApiResponse<ApplicationResponse> update(
            @PathVariable String id,
            @RequestBody @Valid ApplicationUpdateRequest request) {
        return ApiResponse.<ApplicationResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_UPDATE_SUCCESS))
                .data(applicationService.updateApplication(id, request))
                .build();
    }

    @DeleteMapping("/applications/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        applicationService.deleteApplication(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_DELETE_SUCCESS))
                .build();
    }

    @GetMapping("/applications/{id}/status-history")
    public ApiResponse<List<ApplicationStatusHistoryResponse>> getStatusHistory(@PathVariable String id) {
        List<ApplicationStatusHistoryResponse> histories = applicationService.ApplicationStatusHistory(id);
        return ApiResponse.<List<ApplicationStatusHistoryResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_STATUS_HISTORY_SUCCESS))
                .data(histories)
                .build();
    }
}

