import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute(
  "/settings/_profileLayout/profile/password"
)({
  component: ChangePasswordRoute,
});

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
import { StatusButton } from "@/components/ui/status-button";
import { useZodForm } from "@/lib/misc";
import { PasswordSchema } from "@/lib/validation/user";
import { Link } from "@tanstack/react-router";
import { z } from "zod";
import { Input } from "@/components/ui/input";
import { toast } from "sonner";
import { useUpdatePasswordMutation } from "@/lib/api/mutations";

export const handle = {
  breadcrumb: <Icon name="dots-horizontal">Password</Icon>,
};

const ChangePasswordSchema = z
  .object({
    currentPassword: PasswordSchema,
    newPassword: PasswordSchema,
    confirmNewPassword: PasswordSchema,
  })
  .superRefine(({ confirmNewPassword, newPassword }, ctx) => {
    if (confirmNewPassword !== newPassword) {
      ctx.addIssue({
        path: ["confirmNewPassword"],
        code: "custom",
        message: "The passwords must match",
      });
    }
  });

type ChangePasswordForm = z.infer<typeof ChangePasswordSchema>;

function ChangePasswordRoute() {
  const { mutate, status } = useUpdatePasswordMutation();
  const navigate = Route.useNavigate();
  const form = useZodForm({
    schema: ChangePasswordSchema,
    defaultValues: {
      confirmNewPassword: "",
      currentPassword: "",
      newPassword: "",
    },
  });

  function onSubmit(values: ChangePasswordForm) {
    console.log(values);
    mutate(values, {
      onSuccess() {
        toast.success("You password has been changed!");
        navigate({ to: "/settings/profile" });
      },
    });
  }
  const rootError = form.formState.errors.root?.message;
  return (
    <Form {...form}>
      <form method="POST" onSubmit={form.handleSubmit(onSubmit)}>
        <FormField
          control={form.control}
          name="currentPassword"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Current Password</FormLabel>
              <FormControl>
                <Input {...field} type="password" />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="newPassword"
          render={({ field }) => (
            <FormItem>
              <FormLabel>New Password</FormLabel>
              <FormControl>
                <Input {...field} type="password" />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="confirmNewPassword"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Confirm New Password</FormLabel>
              <FormControl>
                <Input {...field} type="password" />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        {rootError && <FormError>{rootError}</FormError>}
        <div className="grid w-full grid-cols-2 gap-6">
          <Button variant="secondary" asChild>
            <Link to="..">Cancel</Link>
          </Button>
          <StatusButton type="submit" status={status}>
            Change Password
          </StatusButton>
        </div>
      </form>
    </Form>
  );
}
