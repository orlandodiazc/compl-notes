package com.ditod.notes.web.user.dto;

import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;

@Hidden
public interface AuthUserDto extends UserBaseDto {
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
