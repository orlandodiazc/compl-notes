import { useAuth } from "@/auth";
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
  postLogin,
  postLogout,
  postSignup,
  putNote,
} from ".";
import { ApiProblemDetail } from "./apiSchema";

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

export const useLoginMutation = () => {
  const { setUser } = useAuth();
  return useMutation({
    mutationKey: ["auth", "login"],
    mutationFn: postLogin,
    onError(error: Response) {
      return error;
    },
    onSuccess(data) {
      setUser(data.user);
    },
    throwOnError: false,
  });
};

export const useLogoutMutation = () => {
  const { setUser } = useAuth();
  return useMutation({
    mutationKey: ["auth", "logout"],
    mutationFn: postLogout,
    onSuccess() {
      setUser(undefined);
    },
  });
};

export const useSignupMutation = () => {
  const { setUser } = useAuth();
  return useMutation({
    mutationFn: postSignup,
    onSuccess(data) {
      setUser(data.user);
    },
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
