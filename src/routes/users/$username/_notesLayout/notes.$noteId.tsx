import { Button } from "@/components/ui/button";
import { noteQuery, useDeleteNoteMutation } from "@/lib/api/queryOptions";
import { getNoteImgSrc } from "@/lib/utils";
import { useSuspenseQuery } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";

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

  function handleDeleteClick() {
    mutate(params, {
      onSuccess: () => {
        navigate({
          to: "/users/$username/notes",
          params: { username: params.username },
        });
      },
    });
  }
  return (
    <div className="absolute inset-0 flex flex-col px-10">
      <h2 className="mb-2 pt-12 text-h2 lg:mb-6">{data.title}</h2>
      <div className="overflow-y-auto pb-24">
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
        <p className="whitespace-break-spaces text-sm md:text-lg">
          {data.content}
        </p>
      </div>
      <div className="absolute bottom-3 left-3 right-3 flex items-center gap-2 rounded-lg bg-muted/80 p-4 pl-5 shadow-xl shadow-accent backdrop-blur-sm md:gap-4 md:pl-7 justify-end">
        <Button onClick={handleDeleteClick} disabled={isPending}>
          Delete
        </Button>
        {/* <Button asChild>
          <Link to="edit">Edit</Link>
        </Button> */}
      </div>
    </div>
  );
}
