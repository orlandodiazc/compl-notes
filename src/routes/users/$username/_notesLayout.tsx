import { useAuth } from "@/auth";
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
  const ownerDisplayName = data.name ?? data.username;

  return (
    <main className="container flex h-full min-h-[400px] px-0 pb-12 md:px-8">
      <div className="grid w-full grid-cols-4 bg-muted pl-2 md:container md:mx-2 md:rounded-3xl md:pr-0">
        <div className="relative col-span-1">
          <div className="absolute inset-0 flex flex-col">
            <Link
              to="/users/$username"
              params={{ username }}
              className="flex flex-col items-center justify-center gap-2 bg-muted pb-4 pl-8 pr-4 pt-12 lg:flex-row lg:justify-start lg:gap-4"
            >
              <UserAvatar
                user={data}
                className="h-16 w-16 lg:h-24 lg:w-24 text-4xl"
              />
              <h1 className="text-center text-base font-bold md:text-lg lg:text-left lg:text-2xl">
                {ownerDisplayName}'s Notes
              </h1>
            </Link>
            <ul className="overflow-y-auto overflow-x-hidden pb-12">
              {isOwner && (
                <li className="p-1 pr-0">
                  <Link
                    to="/users/$username/notes/new"
                    params={{ username }}
                    className="line-clamp-2 block rounded-l-full py-2 pl-8 pr-6 text-base lg:text-xl data-[status=active]:bg-background"
                  >
                    <Icon name="plus">New Note</Icon>
                  </Link>
                </li>
              )}

              {data.notes.map((note) => (
                <li key={note.id} className="p-1 pr-0">
                  <Link
                    to="/users/$username/notes/$noteId"
                    params={{ noteId: note.id, username }}
                    resetScroll={false}
                    className="line-clamp-2 block rounded-l-full py-2 pl-8 pr-6 text-base lg:text-xl data-[status=active]:bg-background"
                  >
                    {note.title}
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
