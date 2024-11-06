import { requireAnonymous } from "@/auth/helpers";
import { Checkbox } from "@/components/ui/checkbox";
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
import { useLoginMutation } from "@/lib/api/mutations";
import { useZodForm } from "@/lib/misc";
import { PasswordSchema, UsernameSchema } from "@/lib/validation/user";
import { Link, createFileRoute } from "@tanstack/react-router";
import { Helmet } from "react-helmet-async";
import { z } from "zod";

export const Route = createFileRoute("/login")({
  validateSearch: z.object({
    redirect: z.string().catch("/"),
  }),
  beforeLoad: ({ context }) => {
    requireAnonymous(context.authUser);
  },
  component: LoginPage,
});

const LoginFormSchema = z.object({
  username: UsernameSchema,
  password: PasswordSchema,
  remember: z.boolean(),
});

type LoginForm = z.infer<typeof LoginFormSchema>;

export default function LoginPage() {
  const navigate = Route.useNavigate();
  const { redirect } = Route.useSearch();
  const { mutate, status } = useLoginMutation();

  const form = useZodForm({
    schema: LoginFormSchema,
    mode: "onSubmit",
    defaultValues: { username: "", password: "", remember: false },
  });

  const rootError = form.formState.errors.root;
  function onSubmit(values: LoginForm) {
    mutate(values, {
      onSuccess() {
        navigate({ to: redirect || "/" });
      },
      onError(response: Response) {
        if (response.status === 401) {
          form.setError("root", {
            message: "Invalid username or password",
          });
        }
      },
    });
  }

  return (
    <div className="flex min-h-full flex-col justify-center pb-32 pt-20">
      <Helmet>
        <title>Login to Compl Notes</title>
      </Helmet>
      <div className="mx-auto w-full max-w-md">
        <div className="flex flex-col gap-3 text-center mb-3">
          <h1 className="text-h1">Welcome back!</h1>
          <p className="text-body-md text-muted-foreground mb-1">
            Please enter your details.
          </p>
        </div>
        <div>
          <div className="mx-auto w-full max-w-md px-8">
            <div className="rounded-md text-left bg-muted px-4 py-3 mb-2 text-body-xs">
              <p className="mb-1 flex items-center gap-2">
                <Icon name="info" size="sm" />
                Login as admin using:
              </p>
              <ul className="ms-1 list-inside list-disc marker:w-3">
                <li>
                  Username: <span className="font-bold">admin</span>
                </li>
                <li>
                  Password: <span className="font-bold">123456</span>
                </li>
              </ul>
            </div>
            <Form {...form}>
              <form
                onSubmit={form.handleSubmit(onSubmit)}
                method="POST"
                className="space-y-5"
              >
                <FormField
                  control={form.control}
                  name="username"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Username</FormLabel>
                      <FormControl>
                        <Input
                          {...field}
                          autoFocus={true}
                          className="lowercase"
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="password"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Password</FormLabel>
                      <FormControl>
                        <Input {...field} type="password" />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="remember"
                  render={({ field }) => (
                    <FormItem className="flex flex-row items-start space-x-2 space-y-0">
                      <FormControl>
                        <Checkbox
                          checked={field.value}
                          onCheckedChange={field.onChange}
                        />
                      </FormControl>
                      <div className="space-y-1 leading-none">
                        <FormLabel>Remember me</FormLabel>
                      </div>
                    </FormItem>
                  )}
                />
                {rootError && <FormError>{rootError.message}</FormError>}
                <StatusButton
                  className="w-full"
                  status={status}
                  type="submit"
                  disabled={status === "pending"}
                >
                  Log in
                </StatusButton>
              </form>
            </Form>
          </div>
          <div className="flex items-center justify-center gap-2 pt-6">
            <span className="text-muted-foreground">New here?</span>
            <Link to="/signup">Create an account</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
