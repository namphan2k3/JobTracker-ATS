package com.jobtracker.jobtracker_app.services.impl;

import java.util.List;

import com.jobtracker.jobtracker_app.dto.requests.RoleCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.RolePermissionRequest;
import com.jobtracker.jobtracker_app.dto.requests.RolePermissionsRequest;
import com.jobtracker.jobtracker_app.dto.requests.RoleUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.RolePermissionsResponse;
import com.jobtracker.jobtracker_app.dto.responses.RolePermissionsUpdateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobtracker.jobtracker_app.dto.responses.RoleResponse;
import com.jobtracker.jobtracker_app.entities.Permission;
import com.jobtracker.jobtracker_app.entities.Role;
import com.jobtracker.jobtracker_app.entities.RolePermission;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.RoleMapper;
import com.jobtracker.jobtracker_app.repositories.PermissionRepository;
import com.jobtracker.jobtracker_app.repositories.RolePermissionRepository;
import com.jobtracker.jobtracker_app.repositories.RoleRepository;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.services.RoleService;
import com.jobtracker.jobtracker_app.services.cache.PermissionCacheService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;
    RolePermissionRepository rolePermissionRepository;
    PermissionCacheService permissionCacheService;
    UserRepository userRepository;

    @Override
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    @Transactional
    public RoleResponse create(RoleCreationRequest request) {

        String name = request.getName().trim().toUpperCase();

        validateNameUnique(name, null);

        Role role = roleMapper.toRole(request);
        role.setName(name);

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }


    @Override
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public RoleResponse getById(String id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        return roleMapper.toRoleResponse(role);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public Page<RoleResponse> getAll(Pageable pageable) {
        return roleRepository.findAll(pageable).map(roleMapper::toRoleResponse);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    @Transactional
    public RoleResponse update(String id, RoleUpdateRequest request) {
        Role role = roleRepository.findById(id)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        roleMapper.updateRole(role, request);

        return roleMapper.toRoleResponse(role);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    @Transactional
    public void delete(String id) {
        Role role = roleRepository.findById(id)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        role.softDelete();
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void addPermissionToRole(String roleId, RolePermissionRequest request) {
        Role role = roleRepository.findById(roleId)
                .filter(r -> Boolean.TRUE.equals(r.getIsActive()) && !r.isDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        Permission permission = permissionRepository.findById(request.getPermissionId())
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()) && !p.isDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));

        if (rolePermissionRepository.existsByRole_IdAndPermission_Id(role.getId(), permission.getId())) {
            throw new AppException(ErrorCode.ROLE_PERMISSION_EXISTED);
        }


        RolePermission rolePermission = RolePermission.builder()
                .role(role)
                .permission(permission)
                .build();

        rolePermissionRepository.save(rolePermission);
    }

    @Override
    @Transactional
    public void removePermissionFromRole(String roleId, String permissionId) {
        rolePermissionRepository.deleteByRole_IdAndPermission_Id(roleId, permissionId);
    }

    @Override
    public Page<RolePermissionsResponse> getRolePermissions(String roleId, Pageable pageable) {
        Role role = roleRepository.findById(roleId)
                .filter(Role::getIsActive)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        return rolePermissionRepository.findByRole_Id(role.getId(), pageable)
                .map(roleMapper::toRolePermissionResponse);
    }

    @Override
    @Transactional
    public RolePermissionsUpdateResponse updateRolePermissions(String roleId, RolePermissionsRequest request) {
        Role role = roleRepository.findById(roleId)
                .filter(r -> Boolean.TRUE.equals(r.getIsActive()) && !r.isDeleted())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        List<String> ids = request.getPermissionIds();

        List<Permission> permissions =
                permissionRepository.findAllByIdInAndIsActiveTrueAndDeletedAtIsNull(ids);

        if (permissions.size() != ids.size()) {
            throw new AppException(ErrorCode.PERMISSION_NOT_EXISTED);
        }

        rolePermissionRepository.deleteByRole_Id(role.getId());

        List<RolePermission> rolePermissions = permissions.stream()
                .map(p -> RolePermission.builder()
                        .role(role)
                        .permission(p)
                        .build())
                .toList();

        rolePermissionRepository.saveAll(rolePermissions);

        return RolePermissionsUpdateResponse.builder()
                .roleId(role.getId())
                .permissionIds(ids)
                .updatedAt(role.getUpdatedAt())
                .build();
    }


    private void validateNameUnique(String name, String excludeId) {
        boolean exists = excludeId == null
                ? roleRepository.existsByNameIgnoreCase(name)
                : roleRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId);
        if (exists) {
            throw new AppException(ErrorCode.NAME_EXISTED);
        }
    }
}
