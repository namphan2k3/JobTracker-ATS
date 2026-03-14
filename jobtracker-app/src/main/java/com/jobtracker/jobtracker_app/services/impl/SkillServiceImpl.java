package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.skill.SkillRequest;
import com.jobtracker.jobtracker_app.dto.responses.skill.SkillResponse;
import com.jobtracker.jobtracker_app.entities.Skill;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.SkillMapper;
import com.jobtracker.jobtracker_app.repositories.SkillRepository;
import com.jobtracker.jobtracker_app.services.SkillService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SkillServiceImpl implements SkillService {
    SkillRepository skillRepository;
    SkillMapper skillMapper;

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('SKILL_CREATE')")
    public SkillResponse create(SkillRequest request) {
        String rawName = request.getName() != null ? request.getName().trim() : "";
        if (rawName.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        // Viết hoa chữ cái đầu, phần còn lại giữ nguyên
        String normalizedName = rawName.substring(0, 1).toUpperCase() + rawName.substring(1);

        // Nếu đã tồn tại skill cùng tên thì trả về skill đó, không tạo mới
        Skill existing = skillRepository.findByNameIgnoreCase(normalizedName).orElse(null);
        if (existing != null) {
            // Nếu skill đang inactive thì bật lại
            if (Boolean.FALSE.equals(existing.getIsActive())) {
                existing.setIsActive(true);
                skillRepository.save(existing);
            }
            return skillMapper.toSkillResponse(existing);
        }

        Skill skill = skillMapper.toSkill(request);
        skill.setName(normalizedName);
        return skillMapper.toSkillResponse(skillRepository.save(skill));
    }

    @Override
    @PreAuthorize("hasAuthority('SKILL_READ')")
    public SkillResponse getById(String id) {
        Skill skill = skillRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_EXISTED));
        return skillMapper.toSkillResponse(skill);
    }

    @Override
    @PreAuthorize("hasAuthority('SKILL_READ')")
    public Page<SkillResponse> getAll(String name, String category, Pageable pageable) {
        return skillRepository.searchByNameAndCategory(name, category, pageable)
                .map(skillMapper::toSkillResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('SKILL_UPDATE')")
    public SkillResponse update(String id, SkillRequest request) {
        Skill skill = skillRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_EXISTED));

        skillMapper.updateSkill(skill, request);

        if (skill.getName() != null) {
            String rawName = skill.getName().trim();
            if (rawName.isEmpty()) {
                throw new AppException(ErrorCode.INVALID_INPUT);
            }
            String normalizedName = rawName.substring(0, 1).toUpperCase() + rawName.substring(1);

            Skill duplicated = skillRepository.findByNameIgnoreCase(normalizedName).orElse(null);
            if (duplicated != null && !duplicated.getId().equals(skill.getId())) {
                // Không cho phép rename thành tên đã tồn tại
                throw new AppException(ErrorCode.NAME_EXISTED);
            }

            skill.setName(normalizedName);
        }

        return skillMapper.toSkillResponse(skillRepository.save(skill));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('SKILL_DELETE')")
    public void delete(String id) {
        Skill skill = skillRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.SKILL_NOT_EXISTED));

        skill.softDelete();
        skillRepository.save(skill);
    }
}




