import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  api,
  deleteNote,
  getCsrfTokenHeader,
  newNote,
  postChangeEmail,
  postLogin,
  postLogout,
  postOnboarding,
  putNote,
} from ".";
import { ApiSchema, type ApiProblemDetail } from "./apiSchema";
import { authUserQuery, noteQuery, notesQuery } from "./queryOptions";

export const useLoginMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: postLogin,
    onError(error: Response) {
      return error;
    },
    onSuccess(data) {
      queryClient.setQueryData(authUserQuery().queryKey, data);
    },
    throwOnError: false,
  });
};

export const useLogoutMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: postLogout,
    onSuccess() {
      queryClient.setQueryData(authUserQuery().queryKey, { user: undefined });
    },
  });
};

export const useOnboardingMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: postOnboarding,
    onSuccess(data) {
      queryClient.setQueryData(authUserQuery().queryKey, data);
    },
    onError(error: ApiProblemDetail) {
      return error;
    },
    throwOnError: false,
  });
};

export const useDeleteNoteMutation = (usernameInvalidateParam: string) => {
  const queryClient = useQueryClient();
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
  const queryClient = useQueryClient();
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
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (formData: FormData) => newNote({ username, formData }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: notesQuery(username).queryKey,
      });
    },
  });
};

export const useChangeEmailMutation = () => {
  return useMutation({
    mutationFn: postChangeEmail,
  });
};

export const useDeleteUserImageMutation = () => {
  return useMutation({
    mutationFn: (imageId?: string) =>
      api.delete<void>(`user-images/${imageId}`, {
        headers: getCsrfTokenHeader(),
      }),
  });
};

export const useCreateUserImageMutation = () => {
  return useMutation({
    mutationFn: (formData: FormData) =>
      api.post<void>("user-images/", {
        body: formData,
        headers: getCsrfTokenHeader(),
      }),
  });
};

export const useUpdatePasswordMutation = () => {
  return useMutation({
    mutationFn: (changePasswordRequest: ApiSchema["ChangePasswordRequest"]) =>
      api.post<void>("users/me/change-password", {
        json: changePasswordRequest,
        headers: getCsrfTokenHeader(),
      }),
  });
};

export const useUpdateUserNamesMutation = () => {
  return useMutation({
    mutationFn: (updateUserNames: ApiSchema["UpdateNamesRequest"]) =>
      api.patch<void>("users/me", {
        json: updateUserNames,
        headers: getCsrfTokenHeader(),
      }),
  });
};
