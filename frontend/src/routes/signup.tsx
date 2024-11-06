import { requireAnonymous } from "@/auth/helpers";
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
import { postSignup } from "@/lib/api";
import { VerifyRequestParamsType } from "@/lib/api/schema";
import { useZodForm } from "@/lib/misc";
import { useMutation } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { Helmet } from "react-helmet-async";
import { z } from "zod";

export const Route = createFileRoute("/signup")({
  beforeLoad: ({ context }) => {
    requireAnonymous(context.authUser);
  },
  component: Signup,
});

const EmailSchema = z
  .string({ required_error: "Email is required" })
  .email({ message: "Email is invalid" })
  .min(3, { message: "Email is too short" })
  .max(100, { message: "Email is too long" })
  // users can type the email in any case, but we store it in lowercase
  .transform((value) => value.toLowerCase());

const SignupSchema = z.object({
  email: EmailSchema,
});

type SignupForm = z.infer<typeof SignupSchema>;

export default function Signup() {
  const navigate = Route.useNavigate();

  const form = useZodForm({
    schema: SignupSchema,
    defaultValues: {
      email: "",
    },
  });

  const { mutate, status } = useMutation({
    mutationKey: ["signup"],
    mutationFn: postSignup,
  });

  function onSubmit(values: SignupForm) {
    mutate(values, {
      onSuccess() {
        navigate({
          to: "/verify",
          search: {
            target: values.email,
            type: VerifyRequestParamsType.ONBOARDING,
          },
        });
      },
      onError() {
        form.setError("root", { message: "Something went wrong" });
        // expected error is a email already exists exception
      },
    });
  }

  return (
    <div className="container flex flex-col justify-center pb-32 pt-20">
      <Helmet>
        <title>Sign Up | Epic Notes</title>
      </Helmet>
      <div className="text-center">
        <h1 className="text-h1">Let's start your journey!</h1>
        <p className="mt-3 text-body-md text-muted-foreground">
          Please enter your email.
        </p>
      </div>
      <div className="mx-auto mt-16 min-w-[368px] max-w-sm">
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
            <StatusButton
              className="w-full"
              status={status}
              type="submit"
              disabled={status === "pending"}
            >
              Submit
            </StatusButton>
          </form>
        </Form>
      </div>
    </div>
  );
}
