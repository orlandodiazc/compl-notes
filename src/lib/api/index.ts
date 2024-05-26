import Cookies from "js-cookie";
import { ApiSchema } from "./apiSchema";

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

function getCsrfTokenCookie():
  | Record<"X-XSRF-TOKEN", string>
  | Record<string, never> {
  const token = Cookies.get("XSRF-TOKEN");
  if (!token) return {};
  return { "X-XSRF-TOKEN": token };
}

export async function fetcher(...args: Parameters<typeof fetch>) {
  const [url, opts] = args;
  const response = await fetch(`${API_BASE_URL}${url}`, opts);
  let data;
  try {
    data = await response.json();
  } catch (e) {
    console.error(response);
    throw response;
  }
  if (!response.ok) throw data;
  return data;
}

export function fetchFilteredUsers(
  filter?: string,
): Promise<ApiSchema["UserFilteredResponse"][]> {
  return fetcher("/users?" + new URLSearchParams({ filter: filter ?? "" }));
}

export function fetchAuthUser(): Promise<ApiSchema["AuthUserResponse"]> {
  return fetcher("/auth/user", { credentials: "include" });
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
    API_BASE_URL + "/users/" + username + "/notes/" + noteId,
    {
      method: "DELETE",
      credentials: "include",
      headers: getCsrfTokenCookie(),
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
    method: "PUT",
    body: formData,
    credentials: "include",
    headers: getCsrfTokenCookie(),
  });
}

export function newNote({
  username,
  formData,
}: {
  username: string;
  formData: FormData;
}): Promise<ApiSchema["NoteSummaryResponse"]> {
  return fetcher("/users/" + username + "/notes", {
    method: "POST",
    body: formData,
    credentials: "include",
    headers: getCsrfTokenCookie(),
  });
}

export function postLogin(
  loginRequest: ApiSchema["LoginRequest"],
): Promise<ApiSchema["AuthUserResponse"]> {
  return fetcher("/auth/login", {
    method: "POST",
    body: JSON.stringify(loginRequest),
    credentials: "include",
    headers: { "Content-Type": "application/json", ...getCsrfTokenCookie() },
  });
}

export async function postLogout() {
  const response = await fetch(API_BASE_URL + "/auth/logout", {
    method: "POST",
    credentials: "include",
    headers: getCsrfTokenCookie(),
  });
  if (!response.ok) throw response;
}

export function postSignup(
  signupRequest: ApiSchema["SignupRequest"],
): Promise<ApiSchema["AuthUserResponse"]> {
  return fetcher("/auth/signup", {
    method: "POST",
    body: JSON.stringify(signupRequest),
    credentials: "include",
    headers: { "Content-Type": "application/json", ...getCsrfTokenCookie() },
  });
}
