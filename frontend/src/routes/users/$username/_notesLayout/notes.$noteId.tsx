import { userHasPermission } from "@/auth/helpers";
import { Button } from "@/components/ui/button";
import { useDeleteNoteMutation } from "@/lib/api/mutations";
import { noteQuery } from "@/lib/api/queryOptions";
import { getNoteImgSrc } from "@/lib/utils";
import { useAuthUser } from "@/routes/__root";
import { useSuspenseQuery } from "@tanstack/react-query";
import { Link, createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";

export const Route = createFileRoute(
  "/users/$username/_notesLayout/notes/$noteId"
)({
  component: NoteRoute,
  loader: ({ params, context: { queryClient } }) => {
    return queryClient.ensureQueryData(noteQuery(params.noteId));
  },
});

export default function NoteRoute() {
  const params = Route.useParams();
  const navigate = Route.useNavigate();
  const { data } = useSuspenseQuery(noteQuery(params.noteId));
  const { mutate, isPending } = useDeleteNoteMutation(params.username);

  const user = useAuthUser();
  const isOwner = user?.id === data.owner.id;
  const canDelete = userHasPermission(
    user,
    isOwner ? "DELETE:NOTE:OWN" : "DELETE:NOTE:ANY"
  );
  const displayBar = canDelete || isOwner;
  function handleDeleteClick() {
    mutate(params.noteId, {
      onSuccess: () => {
        toast.success("Your note has been deleted!");
        navigate({
          to: "/users/$username/notes",
          params: { username: params.username },
          replace: true,
        });
      },
    });
  }
  return (
    <div className="absolute inset-0 flex flex-col px-3 sm:px-6 py-3 sm:py-6">
      <div className="overflow-y-auto break-words sm:break-normal hyphens-auto sm:hyphens-none">
        <h2 className="mb-2 text-h2 lg:mb-6">{data.title}</h2>
        <div className={`${displayBar ? "pb-24" : "pb-12"}`}>
          {data.images.length ? (
            <ul className="flex flex-wrap gap-5 py-5">
              {data.images.map((image) => (
                <li key={image.id}>
                  <a href={getNoteImgSrc(image.id)}>
                    <img
                      src={getNoteImgSrc(image.id + "?" + image.updatedAt)}
                      alt={image.altText}
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
          <div className="absolute bottom-3 left-3 right-3 flex items-center gap-2 rounded-lg bg-muted/80 p-4 pl-5 backdrop-blur-sm md:gap-4 md:pl-7 justify-end">
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
    </div>
  );
}
