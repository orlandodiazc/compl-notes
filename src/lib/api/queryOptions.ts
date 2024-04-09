import { queryOptions, useMutation } from "@tanstack/react-query";
import {
  deleteNote,
  fetchFilteredUsers,
  fetchNote,
  fetchNotes,
  fetchUser,
  putNote,
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

export const noteQuery = (params: { username: string; noteId: string }) =>
  queryOptions({
    queryKey: ["users", "notes", params],
    queryFn: () => fetchNote(params),
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

export const usePutNoteMutation = (params: {
  username: string;
  noteId: string;
}) => {
  return useMutation({
    mutationFn: (formData: FormData) => putNote({ params, formData }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: noteQuery(params).queryKey,
      });
    },
  });
};
