import { queryOptions } from "@tanstack/react-query";
import {
  fetchAuthUser,
  fetchFilteredUsers,
  fetchNote,
  fetchNotes,
  fetchOnboardingEmail,
  fetchUser,
} from ".";

export const authUserQuery = () =>
  queryOptions({
    queryFn: fetchAuthUser,
    queryKey: ["auth", "user"],
    staleTime: Infinity,
    throwOnError: false,
  });

export const usersQuery = (filter?: string) =>
  queryOptions({
    queryKey: ["users", "search", filter],
    queryFn: () => fetchFilteredUsers(filter),
  });

export const userQuery = (username: string) =>
  queryOptions({
    queryKey: ["users", username],
    queryFn: () => fetchUser(username),
  });

export const notesQuery = (username: string) =>
  queryOptions({
    queryKey: ["users", username, "notes"],
    queryFn: () => fetchNotes(username),
  });

export const noteQuery = (params: { username: string; noteId: string }) =>
  queryOptions({
    queryKey: ["users", "notes", params],
    queryFn: () => fetchNote(params),
  });

export const onboardingEmailQuery = queryOptions({
  queryKey: ["onboardingEmail"],
  queryFn: fetchOnboardingEmail,
});
