import { noteQuery, usePutNoteMutation } from "@/lib/api/queryOptions";
import { useSuspenseQuery } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import NoteForm from "./-note-form";
import { toast } from "sonner";

export const Route = createFileRoute(
  "/users/$username/_notesLayout/notes/$noteId/edit",
)({
  component: NoteEdit,
  loader: ({ params, context: { queryClient } }) => {
    return queryClient.ensureQueryData(noteQuery(params));
  },
});

export default function NoteEdit() {
  const params = Route.useParams();
  const { data } = useSuspenseQuery(noteQuery(params));
  const navigate = Route.useNavigate();
  const { mutate, status } = usePutNoteMutation(params);
  function handleSubmit(formData: FormData) {
    mutate(formData, {
      onSuccess: () => {
        toast(`Succesfully edited ${params.username}'s note!`);
        navigate({
          to: "/users/$username/notes/$noteId",
          params,
        });
      },
    });
  }
  return <NoteForm handleSubmit={handleSubmit} status={status} note={data} />;
}
