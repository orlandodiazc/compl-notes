import { queryClient } from "@/main";
import { queryOptions, useMutation } from "@tanstack/react-query";
import {
  deleteNote,
  fetchAuthUser,
  fetchFilteredUsers,
  fetchNote,
  fetchNotes,
  fetchUser,
  newNote,
  newUser,
  postLogin,
  putNote,
} from ".";

export const authUserQuery = () =>
  queryOptions({
    queryKey: ["auth", "user"],
    queryFn: () => fetchAuthUser(),
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

export const useLoginMutation = () => {
  return useMutation({
    mutationKey: ["auth", "login"],
    mutationFn: postLogin,
    onSuccess(data) {
      queryClient.setQueryData(authUserQuery().queryKey, { user: data });
    },
  });
};

export const useNewUserMutation = () => {
  return useMutation({
    mutationFn: newUser,
    onSuccess: (data) => {
      queryClient.setQueryData(authUserQuery().queryKey, { user: data });
    },
  });
};

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

export const useNewNoteMutation = (username: string) => {
  return useMutation({
    mutationFn: (formData: FormData) => newNote({ username, formData }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: notesQuery(username).queryKey,
      });
    },
  });
};
