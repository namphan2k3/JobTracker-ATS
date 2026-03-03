package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.responses.email.EmailHistoryDetailResponse;
import com.jobtracker.jobtracker_app.dto.responses.email.EmailHistoryResponse;
import com.jobtracker.jobtracker_app.entities.EmailOutbox;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmailOutboxMapper {

    EmailHistoryResponse toHistoryResponse(EmailOutbox emailOutbox);

    EmailHistoryDetailResponse toHistoryDetailResponse(EmailOutbox emailOutbox);
}
