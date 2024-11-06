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
import { useUpdateUserNamesMutation } from "@/lib/api/mutations";
import { useZodForm } from "@/lib/misc";
import { getUserImgSrc } from "@/lib/utils";
import { NameSchema, UsernameSchema } from "@/lib/validation/user";
import { useAuthUser } from "@/routes/__root";
import { createFileRoute, Link } from "@tanstack/react-router";
import { toast } from "sonner";
import { z } from "zod";

export const Route = createFileRoute("/settings/_profileLayout/profile/")({
  component: EditUserProfileIndex,
});

const ProfileFormSchema = z.object({
  name: NameSchema.optional(),
  username: UsernameSchema,
});

type ProfileForm = z.infer<typeof ProfileFormSchema>;

function EditUserProfileIndex() {
  const user = useAuthUser();
  return (
    <div className="flex flex-col gap-6 ">
      <div className="flex justify-center">
        <div className="relative h-52 w-52">
          <img
            src={getUserImgSrc(
              user?.image?.id + "?updatedAt=" + user?.image?.updatedAt
            )}
            alt={user?.username}
            className="h-full w-full rounded-full object-cover"
          />
          <Button
            asChild
            variant="outline"
            className="absolute -right-3 top-3 flex h-10 w-10 items-center justify-center rounded-full p-0"
          >
            <Link
              resetScroll={false}
              to="/settings/profile/photo"
              title="Change profile photo"
              aria-label="Change profile photo"
            >
              <Icon name="camera" className="h-4 w-4" />
            </Link>
          </Button>
        </div>
      </div>
      <UpdateProfile />

      <div className="col-span-6 my-6 h-1 border-b-[1.5px] border-foreground" />
      <div className="col-span-full flex flex-col gap-6">
        <div>
          <Link to="/settings/profile/change-email">
            <Icon name="envelope-closed">Change email from {user?.email}</Icon>
          </Link>
        </div>
        <div>
          <Link to="/settings/profile/password">
            <Icon name="dots-horizontal">Change Password</Icon>
          </Link>
        </div>
      </div>
    </div>
  );
}

function UpdateProfile() {
  const user = useAuthUser();
  const { mutate, status } = useUpdateUserNamesMutation();
  const navigate = Route.useNavigate();
  const form = useZodForm({
    schema: ProfileFormSchema,
    defaultValues: {
      name: user?.name,
      username: user?.username,
    },
  });

  function onSubmit(values: ProfileForm) {
    console.log(values);
    mutate(values, {
      onSuccess() {
        toast.success("Your username and email have been updated!");
        navigate({ to: "/settings/profile" });
      },
    });
  }
  const rootErrorMessage = form.formState.errors.root?.message;
  return (
    <Form {...form}>
      <form
        method="POST"
        onSubmit={form.handleSubmit(onSubmit)}
        id="note-editor"
      >
        <div className="grid grid-cols-6 gap-x-10">
          <FormField
            control={form.control}
            name="username"
            render={({ field }) => (
              <FormItem className="col-span-3">
                <FormLabel>Username</FormLabel>
                <FormControl>
                  <Input {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="name"
            render={({ field }) => (
              <FormItem className="col-span-3">
                <FormLabel>Name</FormLabel>
                <FormControl>
                  <Input {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {rootErrorMessage && <FormError>{rootErrorMessage}</FormError>}

        <div className="mt-8 flex justify-center">
          <StatusButton type="submit" size="lg" status={status}>
            Save changes
          </StatusButton>
        </div>
      </form>
    </Form>
  );
}
