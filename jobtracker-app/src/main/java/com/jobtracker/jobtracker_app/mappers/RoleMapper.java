package com.jobtracker.jobtracker_app.mappers;

import com.jobtracker.jobtracker_app.dto.requests.role.RoleCreationRequest;
import com.jobtracker.jobtracker_app.dto.requests.role.RolePermissionsRequest;
import com.jobtracker.jobtracker_app.dto.requests.role.RoleUpdateRequest;
import com.jobtracker.jobtracker_app.dto.responses.role.RolePermissionsResponse;
import com.jobtracker.jobtracker_app.entities.RolePermission;
import org.mapstruct.*;

import com.jobtracker.jobtracker_app.dto.responses.role.RoleResponse;
import com.jobtracker.jobtracker_app.entities.Role;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "rolePermissions", ignore = true)
    Role toRole(RoleCreationRequest request);

    @Mapping(target = "permissionIds", expression = "java(mapPermissionIds(role))")
    @Mapping(target = "permissions", expression = "java(mapPermissions(role))")
    RoleResponse toRoleResponse(Role role);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRole(@MappingTarget Role role, RoleUpdateRequest request);

    @Mapping(target = "permissionId", source = "permission.id")
    @Mapping(target = "name", source = "permission.name")
    @Mapping(target = "resource", source = "permission.resource")
    @Mapping(target = "action", source = "permission.action")
    RolePermissionsResponse toRolePermissionResponse(RolePermission rolePermission);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "rolePermissions", ignore = true)
    void updateRolePermission(@MappingTarget Role role, RolePermissionsRequest request);

    default Set<String> mapPermissionIds(Role role) {
        if (role.getRolePermissions() == null) {
            return Set.of();
        }
        return role.getRolePermissions().stream()
                .filter(rp -> !rp.getIsDeleted())
                .map(rp -> rp.getPermission().getId())
                .collect(Collectors.toSet());
    }

    default List<RolePermissionsResponse> mapPermissions(Role role) {
        if (role.getRolePermissions() == null) {
            return List.of();
        }
        return role.getRolePermissions().stream()
                .filter(rp -> !rp.getIsDeleted())
                .map(this::toRolePermissionResponse)
                .collect(Collectors.toList());
    }
}
