import Cookies from "js-cookie";
import ky from "ky";
import { ApiSchema } from "./apiSchema";

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const api = ky.create({
  prefixUrl: API_BASE_URL,
  credentials: "include",
});

export function getCsrfTokenHeader() {
  const token = Cookies.get("XSRF-TOKEN");
  return token ? { "X-XSRF-TOKEN": token } : {};
}

export function fetchFilteredUsers(
  filter?: string
): Promise<ApiSchema["UserFilteredResponse"][]> {
  return api.get("users", { searchParams: { filter: filter ?? "" } }).json();
}

export function fetchAuthUser(): Promise<ApiSchema["AuthUserResponse"]> {
  return api.get("users/me").json();
}

export function fetchUser(
  username: string
): Promise<ApiSchema["UserSummaryResponse"]> {
  return api.get(`users/${username}`).json();
}

export function fetchNotes(
  username: string
): Promise<ApiSchema["UserNotesResponse"]> {
  return api.get(`users/${username}/notes`).json();
}

export function fetchNote({
  username,
  noteId,
}: {
  username: string;
  noteId: string;
}): Promise<ApiSchema["NoteSummaryResponse"]> {
  return api.get(`users/${username}/notes/${noteId}`).json();
}

export async function deleteNote({
  username,
  noteId,
}: {
  username: string;
  noteId: string;
}) {
  await api.delete(`users/${username}/notes/${noteId}`, {
    headers: getCsrfTokenHeader(),
  });
}

export function putNote({
  params: { username, noteId },
  formData,
}: {
  params: { username: string; noteId: string };
  formData: FormData;
}) {
  return api
    .put(`users/${username}/notes/${noteId}`, {
      body: formData,
      headers: getCsrfTokenHeader(),
    })
    .json();
}

export function newNote({
  username,
  formData,
}: {
  username: string;
  formData: FormData;
}): Promise<ApiSchema["NoteSummaryResponse"]> {
  return api
    .post(`users/${username}/notes`, {
      body: formData,
      headers: getCsrfTokenHeader(),
    })
    .json();
}

export function postLogin(
  loginRequest: ApiSchema["LoginRequest"]
): Promise<ApiSchema["AuthUserResponse"]> {
  return api
    .post("auth/login", {
      json: loginRequest,
      headers: getCsrfTokenHeader(),
    })
    .json();
}

export async function postLogout() {
  await api.post("auth/logout", {
    headers: getCsrfTokenHeader(),
  });
}

export function postSignup(
  onboardRequest: ApiSchema["SignupRequest"]
): Promise<ApiSchema["AuthUserResponse"]> {
  return api
    .post("auth/signup", {
      json: onboardRequest,
      headers: getCsrfTokenHeader(),
    })
    .json();
}
