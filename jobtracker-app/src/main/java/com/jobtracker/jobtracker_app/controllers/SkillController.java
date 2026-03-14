package com.jobtracker.jobtracker_app.controllers;

import com.jobtracker.jobtracker_app.dto.requests.skill.SkillRequest;
import com.jobtracker.jobtracker_app.dto.responses.common.ApiResponse;
import com.jobtracker.jobtracker_app.dto.responses.common.PaginationInfo;
import com.jobtracker.jobtracker_app.dto.responses.skill.SkillResponse;
import com.jobtracker.jobtracker_app.services.SkillService;
import com.jobtracker.jobtracker_app.utils.LocalizationUtils;
import com.jobtracker.jobtracker_app.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/skills")
public class SkillController {
    SkillService skillService;
    LocalizationUtils localizationUtils;

    @PostMapping
    public ApiResponse<SkillResponse> create(@RequestBody @Valid SkillRequest request) {
        return ApiResponse.<SkillResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.SKILL_CREATE_SUCCESS))
                .data(skillService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<SkillResponse>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            Pageable pageable) {
        Page<SkillResponse> skills = skillService.getAll(name, category, pageable);
        return ApiResponse.<List<SkillResponse>>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.SKILL_LIST_SUCCESS))
                .data(skills.getContent())
                .paginationInfo(PaginationInfo.builder()
                        .page(skills.getNumber())
                        .size(skills.getSize())
                        .totalElements(skills.getTotalElements())
                        .build())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<SkillResponse> getById(@PathVariable String id) {
        return ApiResponse.<SkillResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.SKILL_DETAIL_SUCCESS))
                .data(skillService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<SkillResponse> update(@PathVariable String id, @RequestBody @Valid SkillRequest request) {
        return ApiResponse.<SkillResponse>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.SKILL_UPDATE_SUCCESS))
                .data(skillService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        skillService.delete(id);
        return ApiResponse.<Void>builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.SKILL_DELETE_SUCCESS))
                .build();
    }
}





