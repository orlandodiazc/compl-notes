import { useAuth } from "@/auth";
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
import { Input } from "@/components/ui/input";
import { StatusButton } from "@/components/ui/status-button";
import { useLoginMutation } from "@/lib/api/queryOptions";
import { useZodForm } from "@/lib/misc";
import { PasswordSchema, UsernameSchema } from "@/lib/validation/user";
import { Link, createFileRoute, redirect } from "@tanstack/react-router";
import { Helmet } from "react-helmet-async";
import { z } from "zod";

export const Route = createFileRoute("/login")({
  validateSearch: z.object({
    redirect: z.string().catch("/"),
  }),
  beforeLoad: ({ context: { auth } }) => {
    if (auth?.isAuthenticated) {
      throw redirect({ to: "/" });
    }
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

  const { setUser } = useAuth();
  const rootError = form.formState.errors.root;

  function onSubmit(values: LoginForm) {
    mutate(values, {
      onSuccess(data) {
        setUser(data.user);
        navigate({ to: redirect });
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
        <div className="flex flex-col gap-3 text-center">
          <h1 className="text-h1">Welcome back!</h1>
          <p className="text-body-md text-muted-foreground">
            Please enter your details.
          </p>
        </div>
        <div className="mt-16">
          <div className="mx-auto w-full max-w-md px-8">
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
