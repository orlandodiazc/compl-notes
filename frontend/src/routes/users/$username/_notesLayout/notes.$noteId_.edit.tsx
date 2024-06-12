import { noteQuery, usePutNoteMutation } from "@/lib/api/queryOptions";
import { useSuspenseQuery } from "@tanstack/react-query";
import { createFileRoute, redirect } from "@tanstack/react-router";
import NoteForm from "./-note-form";
import { toast } from "sonner";

export const Route = createFileRoute(
  "/users/$username/_notesLayout/notes/$noteId/edit",
)({
  beforeLoad: ({ context: { auth }, location }) => {
    if (!auth?.isAuthenticated) {
      throw redirect({ to: "/login", search: { redirect: location.href } });
    }
  },
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
        toast.success("Your note has been edited successfully!");
        navigate({
          to: "/users/$username/notes/$noteId",
          params,
        });
      },
    });
  }
  return <NoteForm handleSubmit={handleSubmit} status={status} note={data} />;
}
