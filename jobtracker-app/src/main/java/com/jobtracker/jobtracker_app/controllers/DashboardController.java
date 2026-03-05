package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.dashboard.SummaryResponse;
import com.jobtracker.jobtracker_app.services.DashboardService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    DashboardService dashboardService;
    LocalizationUtils localizationUtils;

    @GetMapping("/summary")
    public ApiResponse<SummaryResponse> getSummary() {
        return ApiResponse.<SummaryResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.DASHBOARD_SUMMARY_SUCCESS))
                .data(dashboardService.summary())
                .build();
    }
}
