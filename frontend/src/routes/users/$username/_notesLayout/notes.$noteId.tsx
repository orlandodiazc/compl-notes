import { useAuth } from "@/auth";
import { userHasPermission } from "@/auth/helpers";
import { Button } from "@/components/ui/button";
import { noteQuery, useDeleteNoteMutation } from "@/lib/api/queryOptions";
import { getNoteImgSrc } from "@/lib/utils";
import { useSuspenseQuery } from "@tanstack/react-query";
import { Link, createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";

export const Route = createFileRoute(
  "/users/$username/_notesLayout/notes/$noteId",
)({
  component: NoteRoute,
  loader: ({ params, context: { queryClient } }) => {
    return queryClient.ensureQueryData(noteQuery(params));
  },
});

export default function NoteRoute() {
  const params = Route.useParams();
  const navigate = Route.useNavigate();
  const { data } = useSuspenseQuery(noteQuery(params));
  const { mutate, isPending } = useDeleteNoteMutation(params.username);

  const { user } = useAuth();
  const isOwner = user?.id === data.owner.id;
  const canDelete = userHasPermission(
    user,
    isOwner ? "DELETE:NOTE:OWN" : "DELETE:NOTE:ANY",
  );
  const displayBar = canDelete || isOwner;
  function handleDeleteClick() {
    mutate(params, {
      onSuccess: () => {
        toast.success("Your note has been deleted successfully!");
        navigate({
          to: "/users/$username/notes",
          params: { username: params.username },
          replace: true,
        });
      },
    });
  }
  return (
    <div className="absolute inset-0 flex flex-col px-10">
      <h2 className="mb-2 pt-12 text-h2 lg:mb-6">{data.title}</h2>
      <div className={`${displayBar ? "pb-24" : "pb-12"} overflow-y-auto`}>
        {data.images.length ? (
          <ul className="flex flex-wrap gap-5 py-5">
            {data.images.map((image) => (
              <li key={image.id}>
                <a href={getNoteImgSrc(image.id)}>
                  <img
                    src={getNoteImgSrc(image.id)}
                    alt={image.altText ?? ""}
                    className="h-32 w-32 rounded-lg object-cover"
                  />
                </a>
              </li>
            ))}
          </ul>
        ) : null}

        <p className="whitespace-break-spaces text-sm md:text-lg">
          {data.content}
        </p>
      </div>
      {displayBar && (
        <div className="absolute bottom-3 left-3 right-3 flex items-center gap-2 rounded-lg bg-muted/80 p-4 pl-5 shadow-xl shadow-accent backdrop-blur-sm md:gap-4 md:pl-7 justify-end">
          {canDelete && (
            <Button
              onClick={handleDeleteClick}
              disabled={isPending}
              variant="destructive"
            >
              Delete
            </Button>
          )}

          <Button asChild>
            <Link to="/users/$username/notes/$noteId/edit" params={params}>
              Edit
            </Link>
          </Button>
        </div>
      )}
    </div>
  );
}
