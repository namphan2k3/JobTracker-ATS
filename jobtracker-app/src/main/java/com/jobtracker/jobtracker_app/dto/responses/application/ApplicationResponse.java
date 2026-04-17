package com.jobtracker.jobtracker_app.dto.responses.application;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    String id;
    String jobId;
    String jobTitle;
    String companyId;
    String candidateName;
    String candidateEmail;
    String candidatePhone;
    String statusId;
    ApplicationStatusDetail status;
    String source;
    LocalDate appliedDate;
    String resumeFilePath;
    String coverLetter;
    String notes;
    Integer rating;
    String assignedTo;
    String assignedToName;
    Integer matchScore;
    ApplicationScoringResult matchScoreDetails;
    Boolean allowAdditionalUploads;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

