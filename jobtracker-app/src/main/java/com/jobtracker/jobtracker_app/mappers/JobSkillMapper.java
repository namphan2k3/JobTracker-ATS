package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.job.JobSkillCreationRequest;
import com.jobtracker.jobtracker_app.dto.responses.job.JobSkillCreationResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.JobSkillResponse;
import com.jobtracker.jobtracker_app.dto.responses.job.PublicJobSkillResponse;
import com.jobtracker.jobtracker_app.entities.JobSkill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JobSkillMapper {
    @Mapping(source = "job.id", target = "jobId")
    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "skill.name", target = "name")
    @Mapping(source = "skill.category", target = "category")
    JobSkillResponse toJobSkillResponse(JobSkill jobSkill);

    @Mapping(target = "skill", ignore = true)
    @Mapping(target = "job", ignore = true)
    JobSkill toJobSkill(JobSkillCreationRequest jobSkillCreationRequest);

    @Mapping(source = "job.id", target = "jobId")
    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "skill.name", target = "name")
    @Mapping(source = "skill.category", target = "category")
    JobSkillCreationResponse toJobSkillCreationResponse(JobSkill jobSkill);

    @Mapping(source = "skill.name", target = "name")
    PublicJobSkillResponse toPublicJobSkillResponse(JobSkill jobSkill);

}
