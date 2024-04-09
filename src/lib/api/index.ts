import { ApiSchema } from "./apiSchema";

export const API_BASEURL = "http://localhost:8080";

export async function fetchFilteredUsers(
  filter?: string,
): Promise<ApiSchema["UserFilteredResponse"][]> {
  const response = await fetch(
    API_BASEURL + "/users?" + new URLSearchParams({ filter: filter ?? "" }),
  );
  const data = await response.json();
  return data;
}

export async function fetchUser(
  username: string,
): Promise<ApiSchema["UserSummaryResponse"]> {
  const response = await fetch(API_BASEURL + "/users/" + username);
  const data = await response.json();
  if (!response.ok) throw data;
  return data;
}

export async function fetchNotes(
  username: string,
): Promise<ApiSchema["UserNotesResponse"]> {
  const response = await fetch(API_BASEURL + "/users/" + username + "/notes");
  const data = await response.json();
  if (!response.ok) throw data;
  return data;
}

export async function fetchNote({
  username,
  noteId,
}: {
  username: string;
  noteId: string;
}): Promise<ApiSchema["NoteSummaryResponse"]> {
  const response = await fetch(
    API_BASEURL + "/users/" + username + "/notes/" + noteId,
  );
  const data = await response.json();
  if (!response.ok) throw data;
  return data;
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

export async function putNote({
  params: { username, noteId },
  formData,
}: {
  params: { username: string; noteId: string };
  formData: FormData;
}) {
  const response = await fetch(
    API_BASEURL + "/users/" + username + "/notes/" + noteId,
    {
      method: "put",
      body: formData,
    },
  );
  //maybe add submission reply
  const data = await response.json();
  if (response.status === 400) {
    return data;
  }
  if (!response.ok) {
    throw data;
  }
}

export async function newNote({
  username,
  formData,
}: {
  username: string;
  formData: FormData;
}) {
  const response = await fetch(API_BASEURL + "/users/" + username + "/notes", {
    method: "POST",
    body: formData,
  });
  //maybe add submission reply
  const data = await response.json();
  if (response.status === 400) {
    return data;
  }
  if (!response.ok) {
    throw data;
  }
}
