import { SearchBar } from "@/components/search-bar";
import UserAvatar from "@/components/user-avatar";
import { usersQuery } from "@/lib/api/queryOptions";
import { useSuspenseQuery } from "@tanstack/react-query";
import { Link, createFileRoute, useNavigate } from "@tanstack/react-router";
import { z } from "zod";

export const Route = createFileRoute("/users/")({
  component: UsersRoute,
  validateSearch: z.object({ filter: z.string().optional() }),
  loaderDeps: ({ search: { filter } }) => ({ filter }),
  loader: ({ deps: { filter }, context: { queryClient } }) => {
    return queryClient.ensureQueryData(usersQuery(filter));
  },
});

function UsersRoute() {
  const navigate = useNavigate({ from: Route.fullPath });
  const search = Route.useSearch();
  return (
    <main className="container my-20 flex flex-col items-center justify-center gap-6">
      <h1 className="text-h1">Compl Notes Users</h1>
      <div className="w-full max-w-3xl ">
        <SearchBar
          handleChange={(term) => navigate({ search: { filter: term } })}
          autoFocus
          defaultValue={search.filter}
          placeholder="Search users..."
        />
      </div>
      <UserList />
    </main>
  );
}

function UserList() {
  const search = Route.useSearch();
  const { data, isLoading } = useSuspenseQuery(usersQuery(search.filter));
  if (isLoading) return <h1>loading</h1>;
  return (
    <>
      {data.length ? (
        <ul className="flex w-full flex-wrap items-center justify-center gap-4 delay-200">
          {data.map((user) => (
            <li key={user.id}>
              <Link
                to={user.username}
                className="flex h-36 w-44 flex-col items-center justify-center rounded-lg bg-muted px-5 py-3"
              >
                <UserAvatar user={user} className="h-16 w-16" />
                {user.name ? (
                  <span className="w-full overflow-hidden text-ellipsis whitespace-nowrap text-center text-body-md">
                    {user.name}
                  </span>
                ) : null}
                <span className="w-full overflow-hidden text-ellipsis text-center text-body-sm text-muted-foreground">
                  {user.username}
                </span>
              </Link>
            </li>
          ))}
        </ul>
      ) : (
        <p>No users found</p>
      )}
    </>
  );
}
