import { queryClient } from "@/main";
import { queryOptions, useMutation } from "@tanstack/react-query";
import {
  deleteNote,
  fetchFilteredUsers,
  fetchNote,
  fetchNotes,
  fetchUser,
  newNote,
  postLogin,
  postLogout,
  postSignup,
  putNote,
} from ".";
import { ApiProblemDetail } from "./apiSchema";

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
    onError(error: Response) {
      return error;
    },

    throwOnError: false,
  });
};

export const useLogoutMutation = () => {
  return useMutation({
    mutationKey: ["auth", "logout"],
    mutationFn: postLogout,
  });
};

export const useSignupMutation = () => {
  return useMutation({
    mutationFn: postSignup,
    onError(error: ApiProblemDetail) {
      return error;
    },
    throwOnError: false,
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
