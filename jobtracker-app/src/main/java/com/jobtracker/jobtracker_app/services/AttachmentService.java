package com.jobtracker.jobtracker_app.services;

import com.jobtracker.jobtracker_app.dto.requests.attachment.AttachmentUploadRequest;
import com.jobtracker.jobtracker_app.dto.responses.attachment.AttachmentCreationResponse;
import com.jobtracker.jobtracker_app.dto.responses.attachment.AttachmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public interface AttachmentService {
    AttachmentCreationResponse uploadAttachment(String applicationId, AttachmentUploadRequest request)
            throws IOException;

    URI downloadAttachment(String id);

    List<AttachmentResponse> getApplicationAttachments(String applicationId);

    void delete(String id) throws IOException;
}






