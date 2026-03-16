package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.attachment.AttachmentUploadRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.attachment.AttachmentCreationResponse;
import com.jobtracker.jobtracker_app.dto.responses.attachment.AttachmentResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.services.AttachmentService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AttachmentController {
    AttachmentService attachmentService;
    LocalizationUtils localizationUtils;

    @PostMapping("/applications/{applicationId}/attachments")
    public ApiResponse<AttachmentCreationResponse> uploadAttachment(
            @PathVariable String applicationId,
            @ModelAttribute @Valid AttachmentUploadRequest request) throws IOException {
        return ApiResponse.<AttachmentCreationResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ATTACHMENT_CREATE_SUCCESS))
                .data(attachmentService.uploadAttachment(applicationId, request))
                .build();
    }

    @GetMapping("/applications/{applicationId}/attachments")
    public ApiResponse<List<AttachmentResponse>> getApplicationAttachments(@PathVariable String applicationId) {
        return ApiResponse.<List<AttachmentResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ATTACHMENT_LIST_SUCCESS))
                .data(attachmentService.getApplicationAttachments(applicationId))
                .build();
    }


    @GetMapping("/attachments/{id:.+}/download")
    public ResponseEntity<Void> downloadAttachment(@PathVariable String id) {
        URI downloadUri = attachmentService.downloadAttachment(id);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(downloadUri)
                .build();
    }

    @DeleteMapping("/attachments/{id:.+}")
    public ApiResponse<Void> delete(@PathVariable String id) throws IOException {
        attachmentService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.ATTACHMENT_DELETE_SUCCESS))
                .build();
    }
}





