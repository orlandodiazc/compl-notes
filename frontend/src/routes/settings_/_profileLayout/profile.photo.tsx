import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormError,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Icon } from "@/components/ui/icon";
import { Input } from "@/components/ui/input";
import { StatusButton } from "@/components/ui/status-button";
import {
  useCreateUserImageMutation,
  useDeleteUserImageMutation,
} from "@/lib/api/mutations";
import { authUserQuery } from "@/lib/api/queryOptions";
import { useZodForm } from "@/lib/misc";
import { getUserImgSrc } from "@/lib/utils";
import { useQueryClient } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { toast } from "sonner";
import { z } from "zod";
import { useAuthUser } from "../../__root";

export const Route = createFileRoute("/settings/_profileLayout/profile/photo")({
  component: PhotoRoute,
});

export const handle = {
  breadcrumb: <Icon name="avatar">Photo</Icon>,
};

const MAX_SIZE = 1024 * 1024 * 3; // 3MB

const PhotoFormSchema = z.object({
  photoFile: z
    .instanceof(File)
    .refine((file) => file.size > 0, "Image is required")
    .refine(
      (file) => file.size <= MAX_SIZE,
      "Image size must be less than 3MB"
    ),
});

type PhotoForm = z.infer<typeof PhotoFormSchema>;

function PhotoRoute() {
  const queryClient = useQueryClient();
  const user = useAuthUser();
  const deleteMutation = useDeleteUserImageMutation();
  const createMutation = useCreateUserImageMutation();
  const navigate = Route.useNavigate();
  // manage statuses fix image not showing replacement, fix doubleclick
  // const doubleCheckDeleteImage = useDoubleCheck();
  const form = useZodForm({
    schema: PhotoFormSchema,
    defaultValues: { photoFile: undefined },
  });

  function onSubmit(values: PhotoForm) {
    const formData = new FormData();
    formData.append("image", values.photoFile);
    createMutation.mutate(formData, {
      async onSuccess() {
        queryClient.invalidateQueries({
          queryKey: authUserQuery().queryKey,
        });
        toast.success("You user image has been updated!");
        navigate({ to: "/settings/profile" });
      },
    });
  }

  function onDelete() {
    if (user?.image?.id) {
      deleteMutation.mutate(user.image.id, {
        onSuccess() {
          queryClient.invalidateQueries({
            queryKey: authUserQuery().queryKey,
          });
          toast.success("You user image has been deleted!");
          navigate({ to: "/settings/profile" });
        },
      });
    }
  }
  const [newImageSrc, setNewImageSrc] = useState<string | null>(null);
  const rootErrorMessage = form.formState.errors.root?.message;
  return (
    <div>
      <Form {...form}>
        <form
          method="POST"
          encType="multipart/form-data"
          className="flex flex-col items-center justify-center gap-10"
          onReset={() => setNewImageSrc(null)}
          onSubmit={form.handleSubmit(onSubmit)}
        >
          <img
            src={newImageSrc ?? (user ? getUserImgSrc(user.image?.id) : "")}
            className="h-52 w-52 rounded-full object-cover"
            alt={user?.name ?? user?.username}
          />
          <FormField
            control={form.control}
            name="photoFile"
            render={({ field: { value, onChange, ...fieldProps } }) => (
              <FormItem>
                {!newImageSrc && (
                  <Button asChild className="cursor-pointer">
                    <FormLabel>Change</FormLabel>
                  </Button>
                )}
                <FormControl>
                  <Input
                    {...fieldProps}
                    type="file"
                    accept="image/*"
                    className="peer sr-only"
                    tabIndex={newImageSrc ? -1 : 0}
                    onChange={(e) => {
                      const file = e.currentTarget.files?.[0];
                      if (file) {
                        const reader = new FileReader();
                        reader.onload = (event) => {
                          setNewImageSrc(
                            event.target?.result?.toString() ?? null
                          );
                        };
                        reader.readAsDataURL(file);
                      }
                      onChange(file);
                    }}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          {newImageSrc ? (
            <div className="flex gap-4">
              <StatusButton type="submit" status={"idle"} disabled={false}>
                Save Photo
              </StatusButton>
              <Button type="reset" variant="secondary">
                Reset
              </Button>
            </div>
          ) : (
            <div className="flex gap-4">
              {user?.image?.id ? (
                <Button
                  variant="destructive"
                  // {...doubleCheckDeleteImage.getButtonProps()}
                  type="button"
                  onClick={onDelete}
                >
                  <Icon name="trash">
                    {/* {doubleCheckDeleteImage.doubleCheck
                      ? "Are you sure?"
                      : "Delete"} */}
                  </Icon>
                </Button>
              ) : null}
            </div>
          )}
          {rootErrorMessage && <FormError>{rootErrorMessage}</FormError>}
        </form>
      </Form>
    </div>
  );
}
