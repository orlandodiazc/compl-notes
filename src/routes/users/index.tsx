import { SearchBar } from "@/components/search-bar";
import { usersQuery } from "@/lib/api/queryOptions";
import { cn, getUserImgSrc } from "@/lib/utils";
import { useSuspenseQuery } from "@tanstack/react-query";
import { Link, createFileRoute, useNavigate } from "@tanstack/react-router";
import { z } from "zod";

export const Route = createFileRoute("/users/")({
  component: Users,
  validateSearch: z.object({ filter: z.string().optional() }),
  loaderDeps: ({ search: { filter } }) => ({ filter }),
  loader: ({ deps: { filter }, context: { queryClient } }) => {
    return queryClient.ensureQueryData(usersQuery(filter));
  },
});

function Users() {
  const navigate = useNavigate({ from: Route.fullPath });
  const search = Route.useSearch();
  return (
    <main className="container my-8 flex flex-col items-center justify-center gap-6">
      <div className="md:w-96">
        <SearchBar
          handleChange={(term) => navigate({ search: { filter: term } })}
          defaultValue={search.filter}
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
        <ul
          className={cn(
            "flex w-full flex-wrap items-center justify-center gap-4 delay-200",
          )}
        >
          {data.map((user) => (
            <li key={user.id}>
              <Link
                to={user.username}
                className="flex h-36 w-44 flex-col items-center justify-center rounded-lg bg-muted px-5 py-3"
              >
                <img
                  alt={user.name ?? user.username}
                  src={getUserImgSrc(user.imageId)}
                  className="h-16 w-16 rounded-full"
                />
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
