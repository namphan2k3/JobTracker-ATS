package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.job.PublicJobFilterRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.job.PublicJobDetailResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.PublicJobListResponse;
import com.jobtracker.jobtracker_app.services.JobService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/jobs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicJobController {
    JobService jobService;
    LocalizationUtils localizationUtils;

    @GetMapping
    public ApiResponse<List<PublicJobListResponse>> getAll(
            @ModelAttribute PublicJobFilterRequest filter,
            Pageable pageable) {
        Page<PublicJobListResponse> page = jobService.getPublicJobs(filter, pageable);
        return ApiResponse.<List<PublicJobListResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_LIST_SUCCESS))
                .data(page.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<PublicJobDetailResponse> getById(@PathVariable String id) {
        return ApiResponse.<PublicJobDetailResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.JOB_DETAIL_SUCCESS))
                .data(jobService.getPublicJobById(id))
                .build();
    }
}
