import { useNewNoteMutation } from "@/lib/api/queryOptions";
import { createFileRoute } from "@tanstack/react-router";
import NoteForm from "./-NoteForm";

export const Route = createFileRoute("/users/$username/_notesLayout/notes/new")(
  {
    component: NoteNew,
  },
);

export default function NoteNew() {
  const { username } = Route.useParams();
  const navigate = Route.useNavigate();
  const { mutate, status } = useNewNoteMutation(username);
  function handleSubmit(formData: FormData) {
    mutate(formData, {
      onSuccess: () => {
        navigate({
          to: "/users/$username/notes",
          params: { username },
        });
      },
    });
  }
  return <NoteForm handleSubmit={handleSubmit} status={status} />;
}
