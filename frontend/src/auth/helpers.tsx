import { AuthContext } from ".";

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
  user: AuthContext["user"],
  permission: PermissionString,
) {
  if (!user) return false;
  const { action, entity, access } = parsePermissionString(permission);
  return user.roles?.some((role) =>
    role.permissions?.some(
      (permission) =>
        permission.entity === entity &&
        permission.action === action &&
        permission.access === access,
    ),
  );
}
