package com.jobtracker.jobtracker_app.dto.requests.job;

import com.jobtracker.jobtracker_app.enums.JobType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicJobFilterRequest {
    String companyId;
    String search;
    JobType jobType;
    Boolean isRemote;
    String location;
}
