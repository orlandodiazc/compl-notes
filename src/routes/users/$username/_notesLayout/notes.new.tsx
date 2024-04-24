import { useNewNoteMutation } from "@/lib/api/queryOptions";
import { createFileRoute, redirect } from "@tanstack/react-router";
import NoteForm from "./-note-form";
import { toast } from "sonner";

export const Route = createFileRoute("/users/$username/_notesLayout/notes/new")(
  {
    beforeLoad: ({ context: { auth }, location }) => {
      if (!auth?.isAuthenticated) {
        throw redirect({ to: "/login", search: { redirect: location.href } });
      }
    },
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
        toast(`Succesfully created ${username}' note!`);
        navigate({
          to: "/users/$username/notes",
          params: { username },
        });
      },
    });
  }
  return <NoteForm handleSubmit={handleSubmit} status={status} />;
}
