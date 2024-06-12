import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/users/$username/_notesLayout/notes/")({
  component: NoteIndexRoute,
});

function NoteIndexRoute() {
  return (
    <div className="container pt-12">
      <p className="text-body-md">Select a note...</p>
    </div>
  );
}
