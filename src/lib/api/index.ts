import { ApiSchema } from "./apiSchema";

export const API_BASEURL = "http://localhost:8080";

export async function fetchFilteredUsers(
  filter?: string,
): Promise<ApiSchema["UserFilteredResponse"][]> {
  await new Promise((r) => setTimeout(r, 2000));
  const response = await fetch(
    API_BASEURL + "/users?" + new URLSearchParams({ filter: filter ?? "" }),
  );
  const data = await response.json();
  return data;
}
