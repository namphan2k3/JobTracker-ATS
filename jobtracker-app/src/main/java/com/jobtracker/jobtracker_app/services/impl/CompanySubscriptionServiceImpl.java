package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.subscription.CompanySubscriptionRequest;
import com.jobtracker.jobtracker_app.dto.responses.subscription.CompanySubscriptionResponse;
import com.jobtracker.jobtracker_app.entities.Company;
import com.jobtracker.jobtracker_app.entities.CompanySubscription;
import com.jobtracker.jobtracker_app.entities.SubscriptionPlan;
import com.jobtracker.jobtracker_app.enums.SubscriptionStatus;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.CompanySubscriptionMapper;
import com.jobtracker.jobtracker_app.repositories.CompanyRepository;
import com.jobtracker.jobtracker_app.repositories.CompanySubscriptionRepository;
import com.jobtracker.jobtracker_app.repositories.SubscriptionPlanRepository;
import com.jobtracker.jobtracker_app.services.CompanySubscriptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanySubscriptionServiceImpl implements CompanySubscriptionService {

    CompanySubscriptionRepository companySubscriptionRepository;
    CompanyRepository companyRepository;
    SubscriptionPlanRepository subscriptionPlanRepository;
    CompanySubscriptionMapper companySubscriptionMapper;

    @Override
    @Transactional
    public CompanySubscriptionResponse create(CompanySubscriptionRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_EXISTED));

        SubscriptionPlan plan = subscriptionPlanRepository.findById(request.getPlanId())
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_PLAN_NOT_EXISTED));

        CompanySubscription subscription = companySubscriptionMapper.toCompanySubscription(request);
        subscription.setCompany(company);
        subscription.setPlan(plan);

        if (subscription.getStartDate() == null) {
            subscription.setStartDate(LocalDateTime.now());
        }

        if (subscription.getStatus() == null) {
            subscription.setStatus(SubscriptionStatus.PENDING);
        }

        return companySubscriptionMapper.toCompanySubscriptionResponse(
                companySubscriptionRepository.save(subscription)
        );
    }

    @Override
    @PreAuthorize("hasAuthority('SUBSCRIPTION_READ')")
    public CompanySubscriptionResponse getById(String id) {
        CompanySubscription subscription = companySubscriptionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_SUBSCRIPTION_NOT_EXISTED));
        return companySubscriptionMapper.toCompanySubscriptionResponse(subscription);
    }

    @Override
    @PreAuthorize("hasAuthority('SUBSCRIPTION_READ')")
    public Page<CompanySubscriptionResponse> getAll(Pageable pageable) {
        return companySubscriptionRepository.findAll(pageable)
                .map(companySubscriptionMapper::toCompanySubscriptionResponse);
    }

    @Override
    @PreAuthorize("hasAuthority('SUBSCRIPTION_READ')")
    public Page<CompanySubscriptionResponse> getByCompany(String companyId, Pageable pageable) {
        return companySubscriptionRepository.findByCompany_Id(companyId, pageable)
                .map(companySubscriptionMapper::toCompanySubscriptionResponse);
    }

    @Override
    @PreAuthorize("hasAuthority('SUBSCRIPTION_READ')")
    public CompanySubscriptionResponse getActiveByCompany(String companyId) {
        CompanySubscription subscription = companySubscriptionRepository
                .findLatestSubscription(companyId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_SUBSCRIPTION_NOT_EXISTED));
        return companySubscriptionMapper.toCompanySubscriptionResponse(subscription);
    }
}


