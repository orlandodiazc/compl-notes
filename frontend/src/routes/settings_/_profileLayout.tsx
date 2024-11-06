import { Icon } from "@/components/ui/icon";
import {
  createFileRoute,
  Link,
  Outlet,
  useMatches,
} from "@tanstack/react-router";
import { z } from "zod";
import { useAuthUser } from "../__root";
import { cn } from "@/lib/utils";
import { requireAuthenticated } from "@/auth/helpers";

export const Route = createFileRoute("/settings/_profileLayout")({
  beforeLoad: ({ context, location }) => {
    requireAuthenticated(context.authUser, location.href);
  },
  component: EditUserProfile,
});

export const handle = {
  breadcrumb: <Icon name="file-text">Edit Profile</Icon>,
};

const BreadcrumbHandleMatch = z.object({
  handle: z.object({ breadcrumb: z.any() }),
});

export default function EditUserProfile() {
  const user = useAuthUser();
  const matches = useMatches();

  const breadcrumbs = matches
    .map((m) => {
      const result = BreadcrumbHandleMatch.safeParse(m);
      if (!result.success || !result.data.handle.breadcrumb) return null;
      return (
        <Link key={m.id} to={m.pathname} className="flex items-center">
          {result.data.handle.breadcrumb}
        </Link>
      );
    })
    .filter(Boolean);

  return (
    <div className="m-auto mb-24 mt-16 max-w-3xl">
      <div className="container">
        <ul className="flex gap-3">
          <li>
            <Link
              className="text-muted-foreground"
              to={`/users/${user?.username}`}
            >
              Profile
            </Link>
          </li>
          {breadcrumbs.map((breadcrumb, i, arr) => (
            <li
              key={i}
              className={cn("flex items-center gap-3", {
                "text-muted-foreground": i < arr.length - 1,
              })}
            >
              ▶️ {breadcrumb}
            </li>
          ))}
        </ul>
      </div>
      <main className="mx-auto bg-muted px-6 py-8 md:container md:rounded-3xl">
        <Outlet />
      </main>
    </div>
  );
}
