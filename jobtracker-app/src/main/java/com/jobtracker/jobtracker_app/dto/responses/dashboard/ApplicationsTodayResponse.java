package com.jobtracker.jobtracker_app.dto.responses.dashboard;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationsTodayResponse {
    Long count;
    Long countYesterday;
}
