package com.jobtracker.jobtracker_app.dto.responses.job;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicJobSkillResponse {
    String name;
    Boolean isRequired;
    String proficiencyLevel;
}
