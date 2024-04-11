import { ApiSchema } from "./apiSchema";
import Cookies from "js-cookie";

export const API_BASEURL = import.meta.env.VITE_API_BASEURL;

function getCsrfToken():
  | Record<"X-XSRF-TOKEN", string>
  | Record<string, never> {
  const token = Cookies.get("XSRF-TOKEN");
  if (!token) return {};
  return { "X-XSRF-TOKEN": token };
}

async function fetcher(...args: Parameters<typeof fetch>) {
  const [url, opts] = args;
  const response = await fetch(`${API_BASEURL}${url}`, opts);
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
  const response = await fetch(
    API_BASEURL + "/users/" + username + "/notes/" + noteId,
    {
      method: "DELETE",
      credentials: "include",
      headers: { ...getCsrfToken() },
    },
  );
  if (!response.ok) throw response;
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
    credentials: "include",
    headers: { ...getCsrfToken() },
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
    credentials: "include",
    headers: { ...getCsrfToken() },
  });
}
