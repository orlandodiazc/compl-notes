import { Button } from "@/components/ui/button";
import { Icon } from "@/components/ui/icon";
import UserAvatar from "@/components/user-avatar";
import { useLogoutMutation } from "@/lib/api/mutations";
import { userQuery } from "@/lib/api/queryOptions";
import { useAuthUser } from "@/routes/__root";
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
  const user = useAuthUser();

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
      <div className="container flex flex-col items-center rounded-3xl bg-muted p-8 pt-4 sm:p-12 mt-4">
        <div className="relative w-36 sm:w-52">
          <div className="absolute -top-28 sm:-top-40">
            <div className="relative">
              <UserAvatar
                user={data}
                className="w-36 h-36 sm:w-52 sm:h-52 text-7xl"
              />
            </div>
          </div>
        </div>

        <div className="flex flex-col items-center mt-20">
          <div className="flex flex-wrap items-center justify-center">
            <h1 className="text-center text-h2">{userDisplayName}</h1>
          </div>
          <p className="text-center text-muted-foreground mb-2">
            Joined {new Date(data.createdAt).toLocaleDateString()}
          </p>
          {isLoggedInUser ? (
            <div>
              <Button variant="link" onClick={handleLogout}>
                <Icon
                  name="log-out"
                  className="scale-125 max-md:scale-150"
                  size="font"
                >
                  Logout
                </Icon>
              </Button>
            </div>
          ) : null}

          <div className="mt-2 flex gap-4">
            {isLoggedInUser ? (
              <>
                <Button asChild>
                  <Link to="/users/$username/notes" params={{ username }}>
                    My notes
                  </Link>
                </Button>
              </>
            ) : (
              <Button asChild>
                <Link to="/users/$username/notes" params={{ username }}>
                  {userDisplayName}'s notes
                </Link>
              </Button>
            )}
          </div>
        </div>
      </div>
    </main>
  );
}
