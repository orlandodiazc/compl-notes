import { CheckboxConformField, ErrorList, Field } from "@/components/forms";
import { StatusButton } from "@/components/ui/status-button";
import { useLoginMutation } from "@/lib/api/queryOptions";
import { PasswordSchema, UsernameSchema } from "@/lib/validation/user";
import { getFormProps, getInputProps, useForm } from "@conform-to/react";
import { getZodConstraint, parseWithZod } from "@conform-to/zod";
import { createFileRoute, redirect } from "@tanstack/react-router";
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
  remember: z.boolean().optional(),
});

export default function LoginPage() {
  const navigate = Route.useNavigate();
  const { redirect } = Route.useSearch();

  const { mutate, status } = useLoginMutation();
  const [form, fields] = useForm({
    id: "login-form",
    constraint: getZodConstraint(LoginFormSchema),
    onValidate({ formData }) {
      return parseWithZod(formData, { schema: LoginFormSchema });
    },
    shouldRevalidate: "onInput",
    shouldValidate: "onBlur",
    onSubmit(event, context) {
      event.preventDefault();
      mutate(
        parseWithZod(context.formData, {
          schema: LoginFormSchema,
        }).payload,
        {
          onSuccess() {
            navigate({ to: redirect });
          },
        },
      );
    },
  });

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
            <form method="POST" {...getFormProps(form)}>
              {/* <AuthenticityTokenInput />
							<HoneypotInputs /> */}
              <Field
                labelProps={{ children: "Username" }}
                inputProps={{
                  ...getInputProps(fields.username, { type: "text" }),
                  autoFocus: true,
                  className: "lowercase",
                }}
                errors={fields.username.errors}
              />

              <Field
                labelProps={{ children: "Password" }}
                inputProps={getInputProps(fields.password, {
                  type: "password",
                })}
                errors={fields.password.errors}
              />

              <div className="flex justify-between">
                <CheckboxConformField
                  labelProps={{
                    htmlFor: fields.remember.id,
                    children: "Remember me",
                  }}
                  meta={fields.remember}
                  errors={fields.remember.errors}
                />
              </div>
              <ErrorList errors={form.errors} id={form.errorId} />
              <div className="flex items-center justify-between gap-6 pt-3">
                <StatusButton
                  className="w-full"
                  status={status}
                  type="submit"
                  disabled={status === "pending"}
                >
                  Log in
                </StatusButton>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
