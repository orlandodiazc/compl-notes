import { requireAuthenticated } from "@/auth/helpers";
import { noteQuery } from "@/lib/api/queryOptions";
import { useSuspenseQuery } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import NoteForm from "./-note-form";
import { usePutNoteMutation } from "@/lib/api/mutations";

export const Route = createFileRoute(
  "/users/$username/_notesLayout/notes/$noteId/edit"
)({
  beforeLoad: ({ context, location }) => {
    requireAuthenticated(context.authUser, location.href);
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
        toast.success("Your note has been edited!");
        navigate({
          to: "/users/$username/notes/$noteId",
          params,
        });
      },
    });
  }
  return <NoteForm handleSubmit={handleSubmit} status={status} note={data} />;
}
