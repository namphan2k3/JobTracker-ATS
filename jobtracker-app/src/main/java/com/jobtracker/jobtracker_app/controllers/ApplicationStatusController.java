package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.application_status.ApplicationStatusCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.application_status.ApplicationStatusUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.application_status.ApplicationStatusResponse;
import com.jobtracker.jobtracker_app.services.ApplicationStatusService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/admin/application-statuses")
public class ApplicationStatusController {
    ApplicationStatusService applicationStatusService;
    LocalizationUtils localizationUtils;

    @PostMapping
    public ApiResponse<ApplicationStatusResponse> create(@RequestBody @Valid ApplicationStatusCreationRequest request) {
        return ApiResponse.<ApplicationStatusResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_STATUS_CREATE_SUCCESS))
                .data(applicationStatusService.create(request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ApplicationStatusResponse> getById(@PathVariable String id) {
        return ApiResponse.<ApplicationStatusResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_STATUS_DETAIL_SUCCESS))
                .data(applicationStatusService.getById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ApplicationStatusResponse>> getAll() {
        return ApiResponse.<List<ApplicationStatusResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_STATUS_LIST_SUCCESS))
                .data(applicationStatusService.getAll())
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ApplicationStatusResponse> update(@PathVariable String id,
                                                         @RequestBody @Valid ApplicationStatusUpdateRequest request) {
        return ApiResponse.<ApplicationStatusResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_STATUS_UPDATE_SUCCESS))
                .data(applicationStatusService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        applicationStatusService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.APPLICATION_STATUS_DELETE_SUCCESS))
                .build();
    }
}

