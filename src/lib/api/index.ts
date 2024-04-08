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
