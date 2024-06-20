package com.ditod.notes.domain.user.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
public interface AuthUserDto extends UserBaseResponse {
    List<RoleSummary> getRoles();

    interface RoleSummary {
        String getName();

        List<PermissionSummary> getPermissions();

        interface PermissionSummary {
            String getEntity();

            String getAction();

            String getAccess();
        }
    }
}
