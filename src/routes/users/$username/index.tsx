import { useAuth } from "@/auth";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { Icon } from "@/components/ui/icon";
import { useLogoutMutation, userQuery } from "@/lib/api/queryOptions";
import { getNameInitials, getUserImgSrc } from "@/lib/utils";
import { useSuspenseQuery } from "@tanstack/react-query";
import { Link, createFileRoute } from "@tanstack/react-router";
import { Helmet } from "react-helmet-async";

export const Route = createFileRoute("/users/$username/")({
  component: UserProfileRoute,
  loader: ({ params: { username }, context: { queryClient } }) => {
    return queryClient.ensureQueryData(userQuery(username));
  },
});

export default function UserProfileRoute() {
  const { username } = Route.useParams();
  const navigate = Route.useNavigate();
  const { data } = useSuspenseQuery(userQuery(username));
  const { user } = useAuth();

  const { mutate } = useLogoutMutation();
  function handleLogout() {
    mutate(undefined, {
      onSuccess() {
        navigate({ to: "/" });
      },
    });
  }
  const userDisplayName = data.name ?? data.username;
  const isLoggedInUser = data.id === user?.id;
  return (
    <main className="container my-20 flex flex-col items-center justify-center">
      <Helmet>
        <title> {`${userDisplayName} | Compl Notes`}</title>
        <meta
          name="description"
          content={`Profile of ${userDisplayName} on Compl Notes`}
        />
      </Helmet>
      <div className="container flex flex-col items-center rounded-3xl bg-muted p-12 mt-4">
        <div className="relative w-52">
          <div className="absolute -top-40">
            <div className="relative">
              <Avatar className="h-52 w-52">
                <AvatarImage
                  src={getUserImgSrc(data.image?.id)}
                  alt={userDisplayName}
                />
                <AvatarFallback>
                  {getNameInitials(data.name) ?? data.username.charAt(0)}
                </AvatarFallback>
              </Avatar>
            </div>
          </div>
        </div>

        <div className="flex flex-col items-center mt-20">
          <div className="flex flex-wrap items-center justify-center gap-4">
            <h1 className="text-center text-h2">{userDisplayName}</h1>
          </div>
          <p className="mt-2 text-center text-muted-foreground">
            Joined {new Date(data.createdAt).toLocaleDateString()}
          </p>
          {isLoggedInUser ? (
            <Button variant="link" onClick={handleLogout}>
              <Icon
                name="log-out"
                className="scale-125 max-md:scale-150"
                size="font"
              >
                Logout
              </Icon>
            </Button>
          ) : null}
          <div className="flex gap-4">
            <Button asChild>
              <Link to="/users/$username/notes" params={{ username }}>
                {userDisplayName}'s notes
              </Link>
            </Button>
          </div>
        </div>
      </div>
    </main>
  );
}
