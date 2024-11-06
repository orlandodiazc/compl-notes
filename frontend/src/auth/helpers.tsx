import { redirect } from "@tanstack/react-router";
import { ApiSchema } from "@/lib/api/apiSchema";

type AuthUser = ApiSchema["AuthUserResponse"]["user"];

type Action = "CREATE" | "READ" | "UPDATE" | "DELETE";
type Entity = "USER" | "NOTE";
type Access = "OWN" | "ANY";
type PermissionString = `${Action}:${Entity}:${Access}`;

function parsePermissionString(permissionString: PermissionString) {
  const [action, entity, access] = permissionString.split(":") as [
    Action,
    Entity,
    Access | undefined,
  ];
  return {
    action,
    entity,
    access,
  };
}
export function userHasPermission(
  user: AuthUser,
  permission: PermissionString
) {
  if (!user) return false;
  const { action, entity, access } = parsePermissionString(permission);
  return user.roles?.some((role) =>
    role.permissions?.some(
      (permission) =>
        permission.entity === entity &&
        permission.action === action &&
        permission.access === access
    )
  );
}

export function requireAnonymous(user: AuthUser) {
  if (user) {
    throw redirect({ to: "/" });
  }
}

export function requireAuthenticated(
  user: AuthUser,
  redirectTo: string
): asserts user is NonNullable<AuthUser> {
  if (!user) {
    throw redirect({ to: "/login", search: { redirect: redirectTo } });
  }
}

export function requireUserId(user: AuthUser, redirectTo: string) {
  requireAuthenticated(user, redirectTo);
  return user.id;
}
