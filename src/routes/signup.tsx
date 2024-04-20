import { Checkbox } from "@/components/ui/checkbox";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { StatusButton } from "@/components/ui/status-button";
import { useNewUserMutation } from "@/lib/api/queryOptions";
import { useZodForm } from "@/lib/misc";
import {
  EmailSchema,
  NameSchema,
  PasswordSchema,
  UsernameSchema,
} from "@/lib/validation/user";
import { createFileRoute } from "@tanstack/react-router";
import { Helmet } from "react-helmet-async";
import { z } from "zod";

export const Route = createFileRoute("/signup")({
  component: SignupRoute,
});

const SignupFormSchema = z
  .object({
    username: UsernameSchema,
    name: NameSchema,
    email: EmailSchema,
    password: PasswordSchema,
    confirmPassword: PasswordSchema,
    agreeToTermsOfServiceAndPrivacyPolicy: z
      .boolean()
      .refine(
        (value) => value === true,
        "You must agree to the terms of service and privacy policy",
      ),
  })
  .superRefine(({ confirmPassword, password }, ctx) => {
    if (confirmPassword !== password) {
      ctx.addIssue({
        path: ["confirmPassword"],
        code: "custom",
        message: "The passwords must match",
      });
    }
  });

type SignupForm = z.infer<typeof SignupFormSchema>;

export default function SignupRoute() {
  const { mutate, status } = useNewUserMutation();
  const navigate = Route.useNavigate();

  const form = useZodForm({
    schema: SignupFormSchema,
    defaultValues: {
      email: "",
      username: "",
      name: "",
      password: "",
      confirmPassword: "",
      agreeToTermsOfServiceAndPrivacyPolicy: false,
    },
  });

  function onSubmit(values: SignupForm) {
    mutate(values, {
      onSuccess() {
        navigate({ to: "/" });
      },
      onError(error) {
        error.errors &&
          form.setError("username", { message: error.errors["username"] });
      },
    });
  }

  return (
    <div className="container flex min-h-full flex-col justify-center pb-32 pt-20">
      <Helmet>
        <title>Setup Epic Notes Account</title>
      </Helmet>
      <div className="mx-auto w-full max-w-lg">
        <div className="flex flex-col gap-3 text-center">
          <h1 className="text-h1">Welcome aboard!</h1>
          <p className="text-body-md text-muted-foreground">
            Please enter your details.
          </p>
        </div>
        <Form {...form}>
          <form
            method="POST"
            className="mx-auto min-w-[368px] max-w-sm mt-4 space-y-5"
            onSubmit={form.handleSubmit(onSubmit)}
          >
            <FormField
              control={form.control}
              name="email"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Email</FormLabel>
                  <FormControl>
                    <Input
                      {...field}
                      autoFocus={true}
                      autoComplete="email"
                      className="lowercase"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="username"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Username</FormLabel>
                  <FormControl>
                    <Input
                      {...field}
                      autoComplete="username"
                      className="lowercase"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Name</FormLabel>
                  <FormControl>
                    <Input {...field} autoComplete="name" />
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
                    <Input
                      {...field}
                      autoComplete="new-password"
                      type="password"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="confirmPassword"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Confirm password</FormLabel>
                  <FormControl>
                    <Input
                      {...field}
                      autoComplete="new-password"
                      type="password"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="agreeToTermsOfServiceAndPrivacyPolicy"
              render={({ field }) => (
                <FormItem>
                  <div className="flex items-center space-x-2 mb-3">
                    <FormControl>
                      <Checkbox
                        checked={field.value}
                        onCheckedChange={field.onChange}
                      />
                    </FormControl>
                    <FormLabel>
                      Do you agree to our Terms of Service and Privacy Policy?
                    </FormLabel>
                  </div>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* <ErrorList errors={form.errors} id={form.errorId} /> */}

            <div className="flex items-center justify-between gap-6">
              <StatusButton
                className="w-full"
                status={status}
                type="submit"
                disabled={status === "pending"}
              >
                Create an account
              </StatusButton>
            </div>
          </form>
        </Form>
      </div>
    </div>
  );
}
