package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.dto.requests.user.UserCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.user.UserInviteRequest;
import com.jobtracker.jobtracker_app.dto.requests.user.UserUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.user.UserResponse;
import com.jobtracker.jobtracker_app.entities.Company;
import com.jobtracker.jobtracker_app.entities.Role;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.entities.UserInvitation;
import com.jobtracker.jobtracker_app.enums.SystemRole;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.UserMapper;
import com.jobtracker.jobtracker_app.repositories.RoleRepository;
import com.jobtracker.jobtracker_app.services.PlanLimitService;
import com.jobtracker.jobtracker_app.repositories.UserInvitationRepository;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.services.AdminUserService;
import com.jobtracker.jobtracker_app.services.EmailService;
import com.jobtracker.jobtracker_app.services.cache.PermissionCacheService;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class AdminUserServiceImpl implements AdminUserService {
    UserRepository userRepository;
    UserInvitationRepository userInvitationRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    PermissionCacheService permissionCacheService;
    SecurityUtils securityUtils;
    EmailService emailService;
    PlanLimitService planLimitService;

    @NonFinal
    @Value("${auth.invite-token-expiry-days:7}")
    int inviteTokenExpiryDays;

    @Override
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @Transactional
    public UserResponse addEmployee(UserCreationRequest request) {
        Company company = securityUtils.getCurrentUser().getCompany();

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User newUser = User.builder()
                .company(company)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .isActive(true)
                .isBillable(false)
                .emailVerified(false)
                .build();

        return userMapper.toUserResponse(userRepository.save(newUser));
    }

    @Override
    @PreAuthorize("hasAuthority('USER_READ')")
    public UserResponse getById(String id) {
        String companyId = securityUtils.getCurrentUser().getCompany().getId();
        User user = userRepository.findByIdAndCompany_IdAndDeletedAtIsNull(id, companyId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @Override
    @PreAuthorize("hasAuthority('USER_READ')")
    public Page<UserResponse> getAll(String keyword, String roleId,
                                    Boolean isActive, Boolean emailVerified, Pageable pageable) {
        String companyId = securityUtils.getCurrentUser().getCompany().getId();
        return userRepository.findAllAndSearch(companyId, keyword, roleId, isActive, emailVerified, pageable)
                .map(userMapper::toUserResponse);
    }

    @Override
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @Transactional
    public UserResponse update(String id, UserUpdateRequest request) {
        String companyId = securityUtils.getCurrentUser().getCompany().getId();
        User user = userRepository.findByIdAndCompany_IdAndDeletedAtIsNull(id, companyId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getRoleId() != null) {
            Role role = resolveAssignableRole(request.getRoleId());
            user.setRole(role);
            permissionCacheService.evict(user.getId());
        }

        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @Transactional
    public void delete(String id) {
        String companyId = securityUtils.getCurrentUser().getCompany().getId();
        User user = userRepository.findByIdAndCompany_IdAndDeletedAtIsNull(id, companyId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.softDelete();
        userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @Transactional
    public void restore(String id) {
        String companyId = securityUtils.getCurrentUser().getCompany().getId();
        User user = userRepository.findByIdAndCompany_IdAndDeletedAtIsNull(id, companyId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.restore();
        userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @Transactional
    public void inviteUser(UserInviteRequest request) {
        Company company = securityUtils.getCurrentUser().getCompany();
        planLimitService.enforceUserLimit(company.getId());

        if (userRepository.existsByEmailAndCompany_Id(request.getEmail(), company.getId())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role role = resolveInviteRole(request.getRoleId());

        User newUser = User.builder()
                .company(company)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(role)
                .emailVerified(false)
                .isActive(false)
                .isBillable(true)
                .build();

        newUser = userRepository.save(newUser);

        LocalDateTime now = LocalDateTime.now();
        UserInvitation userInvitation = UserInvitation.builder()
                .user(newUser)
                .token(UUID.randomUUID().toString())
                .company(company)
                .sentAt(now)
                .expiresAt(now.plusDays(inviteTokenExpiryDays))
                .build();

        userInvitationRepository.save(userInvitation);
        emailService.sendUserInvite(newUser, userInvitation);
    }

    @Override
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @Transactional
    public void resendInvite(String userId) {
        String companyId = securityUtils.getCurrentUser().getCompany().getId();
        User user = userRepository.findByIdAndCompany_IdAndDeletedAtIsNull(userId, companyId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (Boolean.TRUE.equals(user.getEmailVerified()) && Boolean.TRUE.equals(user.getIsActive())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        LocalDateTime now = LocalDateTime.now();
        UserInvitation userInvitation = UserInvitation.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .company(user.getCompany())
                .sentAt(now)
                .expiresAt(now.plusDays(inviteTokenExpiryDays))
                .build();

        userInvitationRepository.save(userInvitation);
        emailService.sendUserInviteResend(user, userInvitation);
    }

    private Role resolveInviteRole(String roleId) {
        if (roleId == null || roleId.isBlank()) {
            return roleRepository.findByName(SystemRole.RECRUITER.name())
                    .filter(r -> Boolean.TRUE.equals(r.getIsActive()) && !r.isDeleted())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        }
        return resolveAssignableRole(roleId);
    }

    private Role resolveAssignableRole(String roleId) {
        Role role = roleRepository.findById(roleId)
                .filter(r -> Boolean.TRUE.equals(r.getIsActive()) && !r.isDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        String name = role.getName();
        if (!SystemRole.ADMIN_COMPANY.name().equals(name) && !SystemRole.RECRUITER.name().equals(name)) {
            throw new AppException(ErrorCode.ROLE_NOT_EXISTED);
        }
        return role;
    }
}
