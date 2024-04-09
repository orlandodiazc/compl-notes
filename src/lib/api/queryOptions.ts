import { queryOptions, useMutation } from "@tanstack/react-query";
import {
  deleteNote,
  fetchFilteredUsers,
  fetchNote,
  fetchNotes,
  fetchUser,
} from ".";
import { queryClient } from "@/main";

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

export const noteQuery = (opts: { username: string; noteId: string }) =>
  queryOptions({
    queryKey: ["users", "notes", opts],
    queryFn: () => fetchNote(opts),
  });

export const useDeleteNoteMutation = (usernameInvalidateParam: string) => {
  return useMutation({
    mutationFn: deleteNote,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: notesQuery(usernameInvalidateParam).queryKey,
      });
    },
  });
};
