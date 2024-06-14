import { useAuth } from "@/auth";
import { userHasPermission } from "@/auth/helpers";
import { Icon } from "@/components/ui/icon";
import UserAvatar from "@/components/user-avatar";
import { notesQuery } from "@/lib/api/queryOptions";
import { useSuspenseQuery } from "@tanstack/react-query";
import { Link, Outlet, createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/users/$username/_notesLayout")({
  component: NotesRoute,
  loader: ({ params: { username }, context: { queryClient } }) => {
    return queryClient.ensureQueryData(notesQuery(username));
  },
});

export default function NotesRoute() {
  const { username } = Route.useParams();
  const { data } = useSuspenseQuery(notesQuery(username));
  const { user } = useAuth();
  const isOwner = user?.id === data.id;
  const canCreate = userHasPermission(
    user,
    isOwner ? "CREATE:NOTE:OWN" : "CREATE:NOTE:ANY",
  );
  const ownerDisplayName = data.name ?? data.username;

  return (
    <main className="container flex h-full min-h-[400px] md:py-4 px-0 md:px-8">
      <div className="grid w-full grid-cols-4 bg-muted pl-2 md:container md:mx-2 md:rounded-3xl md:pr-0">
        <div className="relative col-span-1">
          <div className="absolute inset-0 flex flex-col">
            <Link
              to="/users/$username"
              params={{ username }}
              className="flex flex-col items-center gap-2 py-4 px-1 bg-muted lg:py-6"
            >
              <UserAvatar
                user={data}
                className="h-8 w-8 lg:h-24 lg:w-24 text-4xl"
              />
              <h1 className="text-center text-sm font-bold md:text-lg lg:text-left lg:text-2xl">
                {ownerDisplayName}'s Notes
              </h1>
            </Link>
            <ul className="overflow-y-auto overflow-x-hidden pb-12">
              {canCreate && (
                <li>
                  <Link
                    to="/users/$username/notes/new"
                    params={{ username }}
                    className="block data-[status=active]:bg-background p-2 rounded-l-sm md:rounded-l-full md:pl-6"
                  >
                    <Icon name="plus">
                      <span className="text-sm sm:text-base">New Note</span>
                    </Icon>
                  </Link>
                </li>
              )}

              {data.notes.map((note) => (
                <li key={note.id} className="">
                  <Link
                    to="/users/$username/notes/$noteId"
                    params={{ noteId: note.id, username }}
                    resetScroll={false}
                    className="block data-[status=active]:bg-background p-2 rounded-l-sm md:rounded-l-full md:pl-6"
                  >
                    <span className="line-clamp-2 text-sm sm:text-base">
                      {note.title}
                    </span>
                  </Link>
                </li>
              ))}
            </ul>
          </div>
        </div>
        <div className="relative col-span-3 bg-accent md:rounded-r-3xl">
          <Outlet />
        </div>
      </div>
    </main>
  );
}
