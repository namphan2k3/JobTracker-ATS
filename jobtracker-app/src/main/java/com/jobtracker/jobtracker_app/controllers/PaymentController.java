package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.payment.PaymentRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.payment.InitPaymentResponse;
import com.jobtracker.jobtracker_app.dto.responses.payment.PaymentResponse;
import com.jobtracker.jobtracker_app.services.PaymentService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    @Value("${app.base-url}")
    @NonFinal
    String baseUrl;

    PaymentService paymentService;
    LocalizationUtils localizationUtils;

    @PostMapping("/admin/payments")
    public ApiResponse<InitPaymentResponse> create(@RequestBody @Valid PaymentRequest request, HttpServletRequest httpServletRequest) throws UnsupportedEncodingException {
        return ApiResponse.<InitPaymentResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PAYMENT_CREATE_SUCCESS))
                .data(paymentService.create(request, httpServletRequest))
                .build();
    }

    @GetMapping("/payments/vnpay/return")
    public void returnUrl(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        boolean success = paymentService.paymentReturn(request);

        if (success) {
            response.sendRedirect(baseUrl + "/payments/return?status=success");
        } else {
            response.sendRedirect(baseUrl + "/payments/return?status=failed");
        }
    }

    @GetMapping("/admin/payments/{id}")
    public ApiResponse<PaymentResponse> getById(@PathVariable String id) {
        return ApiResponse.<PaymentResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PAYMENT_DETAIL_SUCCESS))
                .data(paymentService.getById(id))
                .build();
    }

    @GetMapping("/admin/payments")
    public ApiResponse<List<PaymentResponse>> getAll(Pageable pageable) {
        Page<PaymentResponse> responses = paymentService.getAll(pageable);
        return ApiResponse.<List<PaymentResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PAYMENT_LIST_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(responses.getNumber())
                        .size(responses.getSize())
                        .totalElements(responses.getTotalElements())
                        .totalPages(responses.getTotalPages())
                        .build())
                .build();
    }

    @GetMapping("/companies/{companyId}/payments")
    public ApiResponse<List<PaymentResponse>> getByCompany(@PathVariable String companyId, Pageable pageable) {
        Page<PaymentResponse> responses = paymentService.getByCompany(companyId, pageable);
        return ApiResponse.<List<PaymentResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PAYMENT_LIST_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(responses.getNumber())
                        .size(responses.getSize())
                        .totalElements(responses.getTotalElements())
                        .totalPages(responses.getTotalPages())
                        .build())
                .build();
    }

    @GetMapping("/company-subscriptions/{companySubscriptionId}/payments")
    public ApiResponse<List<PaymentResponse>> getByCompanySubscription(@PathVariable String companySubscriptionId,
                                                                       Pageable pageable) {
        Page<PaymentResponse> responses = paymentService.getByCompanySubscription(companySubscriptionId, pageable);
        return ApiResponse.<List<PaymentResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.PAYMENT_LIST_SUCCESS))
                .data(responses.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(responses.getNumber())
                        .size(responses.getSize())
                        .totalElements(responses.getTotalElements())
                        .totalPages(responses.getTotalPages())
                        .build())
                .build();
    }
}


