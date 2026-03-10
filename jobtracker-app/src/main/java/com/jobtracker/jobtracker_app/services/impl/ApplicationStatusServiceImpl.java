package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.application_status.ApplicationStatusRequest;
import com.jobtracker.jobtracker_app.dto.responses.application_status.ApplicationStatusResponse;
import com.jobtracker.jobtracker_app.entities.ApplicationStatus;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.ApplicationStatusMapper;
import com.jobtracker.jobtracker_app.repositories.ApplicationStatusRepository;
import com.jobtracker.jobtracker_app.services.ApplicationStatusService;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationStatusServiceImpl implements ApplicationStatusService {
    ApplicationStatusRepository applicationStatusRepository;
    ApplicationStatusMapper applicationStatusMapper;
    SecurityUtils securityUtils;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('APPLICATION_STATUS_CREATE')")
    public ApplicationStatusResponse create(ApplicationStatusRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        if (request.getStatusType() == null) {
            throw new AppException(ErrorCode.APPLICATION_STATUS_STATUS_TYPE_REQUIRED);
        }

        if (applicationStatusRepository
                .findByNameAndCompany_IdAndDeletedAtIsNull(request.getName(), currentUser.getCompany().getId())
                .isPresent()) {
            throw new AppException(ErrorCode.NAME_EXISTED);
        }

        ApplicationStatus applicationStatus = applicationStatusMapper.toApplicationStatus(request);
        applicationStatus.setCompany(currentUser.getCompany());
        if (applicationStatus.getIsTerminal() == null) {
            applicationStatus.setIsTerminal(request.getStatusType().isTerminal());
        }
        if (applicationStatus.getIsDefault() == null) {
            applicationStatus.setIsDefault(false);
        }

        return applicationStatusMapper.toApplicationStatusResponse(applicationStatusRepository.save(applicationStatus));
    }

    @Override
    public ApplicationStatusResponse getById(String id) {
        User currentUser = securityUtils.getCurrentUser();

        ApplicationStatus applicationStatus = applicationStatusRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_STATUS_NOT_EXISTED));
        return applicationStatusMapper.toApplicationStatusResponse(applicationStatus);
    }

    @Override
    @PreAuthorize("hasAuthority('APPLICATION_STATUS_READ')")
    public List<ApplicationStatusResponse> getAll() {
        User currentUser = securityUtils.getCurrentUser();

        return applicationStatusRepository
                .findActiveStatuses(currentUser.getCompany().getId())
                .stream()
                .map(applicationStatusMapper::toApplicationStatusResponse)
                .toList();
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('APPLICATION_STATUS_UPDATE')")
    public ApplicationStatusResponse update(String id, ApplicationStatusRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        ApplicationStatus applicationStatus = applicationStatusRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_STATUS_NOT_EXISTED));

        if (request.getName() != null && !request.getName().equals(applicationStatus.getName())) {
            if (applicationStatusRepository
                    .findByNameAndCompany_IdAndDeletedAtIsNull(request.getName(), currentUser.getCompany().getId())
                    .isPresent()) {
                throw new AppException(ErrorCode.NAME_EXISTED);
            }
        }

        applicationStatusMapper.updateApplicationStatus(applicationStatus, request);
        if (request.getStatusType() != null && request.getIsTerminal() == null) {
            applicationStatus.setIsTerminal(request.getStatusType().isTerminal());
        }
        return applicationStatusMapper.toApplicationStatusResponse(
                applicationStatusRepository.save(applicationStatus));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('APPLICATION_STATUS_DELETE')")
    public void delete(String id) {
        User currentUser = securityUtils.getCurrentUser();

        ApplicationStatus applicationStatus = applicationStatusRepository
                .findByIdAndCompany_IdAndDeletedAtIsNull(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPLICATION_STATUS_NOT_EXISTED));

        applicationStatus.softDelete();
        applicationStatusRepository.save(applicationStatus);
    }
}

