package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.email.EmailHistoryDetailResponse;
import com.jobtracker.jobtracker_app.dto.responses.email.EmailHistoryResponse;
import com.jobtracker.jobtracker_app.enums.AggregateType;
import com.jobtracker.jobtracker_app.enums.EmailStatus;
import com.jobtracker.jobtracker_app.enums.EmailType;
import com.jobtracker.jobtracker_app.services.EmailOutboxService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/admin/email-history")
public class EmailHistoryController {
    EmailOutboxService emailOutboxService;
    LocalizationUtils localizationUtils;

    @GetMapping
    public ApiResponse<List<EmailHistoryResponse>> getAll(
            @RequestParam(required = false) EmailStatus status,
            @RequestParam(required = false) EmailType emailType,
            @RequestParam(required = false) AggregateType aggregateType,
            @RequestParam(required = false) String aggregateId,
            @RequestParam(required = false) String toEmail,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable
    ) {
        Page<EmailHistoryResponse> history = emailOutboxService.getEmailHistory(
                status, emailType, aggregateType, aggregateId, toEmail, startDate, endDate, pageable
        );
        return ApiResponse.<List<EmailHistoryResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_HISTORY_LIST_SUCCESS))
                .data(history.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(history.getNumber())
                        .size(history.getSize())
                        .totalElements(history.getTotalElements())
                        .totalPages(history.getTotalPages())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<EmailHistoryDetailResponse> getById(@PathVariable String id) {
        return ApiResponse.<EmailHistoryDetailResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_HISTORY_DETAIL_SUCCESS))
                .data(emailOutboxService.getEmailHistoryById(id))
                .build();
    }

    @PostMapping("/{id}/resend")
    public ApiResponse<Void> resend(@PathVariable String id) {
        emailOutboxService.resend(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.EMAIL_HISTORY_RESEND_SUCCESS))
                .build();
    }
}
