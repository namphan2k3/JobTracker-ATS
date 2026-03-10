package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.application_status.ApplicationStatusCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.application_status.ApplicationStatusUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.application_status.ApplicationStatusResponse;
import com.jobtracker.jobtracker_app.entities.ApplicationStatus;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ApplicationStatusMapper {
    ApplicationStatus toApplicationStatus(ApplicationStatusCreationRequest request);

    @Mapping(target = "companyId", source = "company.id")
    ApplicationStatusResponse toApplicationStatusResponse(ApplicationStatus applicationStatus);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateApplicationStatus(@MappingTarget ApplicationStatus applicationStatus, ApplicationStatusUpdateRequest request);
}

