import { ApiSchema } from "./apiSchema";

export const API_BASEURL = import.meta.env.VITE_API_BASEURL;

async function fetcher(...args: Parameters<typeof fetch>) {
  const [url, opts] = args;
  const response = await fetch(`${API_BASEURL}${url}`, opts);
  console.log(response);
  const data = await response.json();
  if (!response.ok) throw data;
  return data;
}

export function fetchFilteredUsers(
  filter?: string,
): Promise<ApiSchema["UserFilteredResponse"][]> {
  return fetcher("/users?" + new URLSearchParams({ filter: filter ?? "" }));
}

export function fetchUser(
  username: string,
): Promise<ApiSchema["UserSummaryResponse"]> {
  return fetcher("/users/" + username);
}

export function fetchNotes(
  username: string,
): Promise<ApiSchema["UserNotesResponse"]> {
  return fetcher("/users/" + username + "/notes");
}

export function fetchNote({
  username,
  noteId,
}: {
  username: string;
  noteId: string;
}): Promise<ApiSchema["NoteSummaryResponse"]> {
  return fetcher("/users/" + username + "/notes/" + noteId);
}

export async function deleteNote({
  username,
  noteId,
}: {
  username: string;
  noteId: string;
}) {
  await fetch(API_BASEURL + "/users/" + username + "/notes/" + noteId, {
    method: "DELETE",
  });
}

export function putNote({
  params: { username, noteId },
  formData,
}: {
  params: { username: string; noteId: string };
  formData: FormData;
}) {
  return fetcher("/users/" + username + "/notes/" + noteId, {
    method: "put",
    body: formData,
  });
}

export function newNote({
  username,
  formData,
}: {
  username: string;
  formData: FormData;
}) {
  return fetcher("/users/" + username + "/notes", {
    method: "POST",
    body: formData,
  });
}
