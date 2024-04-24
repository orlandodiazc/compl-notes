package com.ditod.notes.domain.user.dto;

import java.util.List;

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
