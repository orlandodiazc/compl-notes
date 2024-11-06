import {
  Form,
  FormControl,
  FormError,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { StatusButton } from "@/components/ui/status-button";
import { useChangeEmailMutation } from "@/lib/api/mutations";
import { VerifyRequestParamsType } from "@/lib/api/schema";
import { useZodForm } from "@/lib/misc";
import { EmailSchema } from "@/lib/validation/user";
import { createFileRoute } from "@tanstack/react-router";
import { z } from "zod";
import { useAuthUser } from "../../__root";

export const Route = createFileRoute(
  "/settings/_profileLayout/profile/change-email"
)({
  component: ChangeEmail,
});

const ChangeEmailSchema = z.object({
  email: EmailSchema,
});

type ChangeEmailForm = z.infer<typeof ChangeEmailSchema>;

function ChangeEmail() {
  const user = useAuthUser();
  const { mutate, status } = useChangeEmailMutation();
  const navigate = Route.useNavigate();

  const form = useZodForm({
    schema: ChangeEmailSchema,
    defaultValues: { email: "" },
  });

  function onSubmit(values: ChangeEmailForm) {
    mutate(values, {
      onSuccess() {
        navigate({
          to: "/verify",
          search: {
            type: VerifyRequestParamsType.CHANGE_EMAIL,
            target: values.email,
          },
        });
      },
    });
  }

  return (
    <div>
      <h1 className="text-h1">Change Email</h1>
      <p>You will receive an email at the new email address to confirm.</p>
      <p>An email notice will also be sent to your old address {user?.email}</p>
      <div className="mx-auto mt-5 max-w-sm">
        <Form {...form}>
          <form
            method="POST"
            className="flex h-full flex-col gap-y-4 overflow-y-auto overflow-x-hidden pb-24 px-2 md:px-4 md:pt-2"
            onSubmit={form.handleSubmit(onSubmit)}
          >
            <FormField
              control={form.control}
              name="email"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>New Email</FormLabel>
                  <FormControl>
                    <Input {...field} autoFocus={true} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            {form.formState.errors.root?.message && (
              <FormError>{form.formState.errors.root.message}</FormError>
            )}
            <div>
              <StatusButton status={status}>Send Confirmation</StatusButton>
            </div>
          </form>
        </Form>
      </div>
    </div>
  );
}
