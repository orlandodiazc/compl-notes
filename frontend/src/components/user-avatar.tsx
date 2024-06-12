import { getNameInitials, getUserImgSrc } from "@/lib/utils";
import { Avatar, AvatarFallback, AvatarImage } from "./ui/avatar";
import { ApiSchema } from "@/lib/api/apiSchema";

export default function UserAvatar({
  user,
  className,
}: {
  user: ApiSchema["AuthUserResponse"]["user"] &
    ApiSchema["UserFilteredResponse"];
  className?: string;
}) {
  return (
    <Avatar className={className}>
      <AvatarImage
        src={getUserImgSrc(user.image?.id ?? user.imageId)}
        alt={user.name ?? user.username}
      />
      <AvatarFallback>
        {getNameInitials(user.name) ?? user.username.charAt(0).toUpperCase()}
      </AvatarFallback>
    </Avatar>
  );
}
