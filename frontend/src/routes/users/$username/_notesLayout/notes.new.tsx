import { requireAuthenticated } from "@/auth/helpers";
import { useNewNoteMutation } from "@/lib/api/mutations";
import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import NoteForm from "./-note-form";

export const Route = createFileRoute("/users/$username/_notesLayout/notes/new")(
  {
    beforeLoad: ({ context, location }) => {
      requireAuthenticated(context.authUser, location.href);
    },
    component: NoteNew,
  }
);

export default function NoteNew() {
  const { username } = Route.useParams();
  const navigate = Route.useNavigate();
  const { mutate, status } = useNewNoteMutation(username);
  function handleSubmit(formData: FormData) {
    mutate(formData, {
      onSuccess: () => {
        toast.success("Your note has been created!");
        navigate({
          to: "/users/$username/notes",
          params: { username },
        });
      },
    });
  }
  return <NoteForm handleSubmit={handleSubmit} status={status} />;
}
