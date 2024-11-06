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
import { postVerify } from "@/lib/api";
import { VerifyRequestParamsType } from "@/lib/api/schema";
import { useZodForm } from "@/lib/misc";
import { useMutation } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { toast } from "sonner";
import { z } from "zod";

export const Route = createFileRoute("/verify")({
  validateSearch: z.object({
    code: z.string().min(6).max(6).optional(),
    type: z.nativeEnum(VerifyRequestParamsType).optional(),
    target: z.string().optional(),
  }),
  component: VerifyRoute,
});

const VerifySchema = z.object({
  code: z.string().min(6).max(6),
});

type VerifyForm = z.infer<typeof VerifySchema>;

export default function VerifyRoute() {
  const navigate = Route.useNavigate();
  const searchParams = Route.useSearch();

  const form = useZodForm({
    schema: VerifySchema,
    defaultValues: { code: "" },
  });

  const { mutate, status } = useMutation({
    mutationKey: ["verify"],
    mutationFn: postVerify,
  });

  function onSubmit(values: VerifyForm) {
    mutate(
      { ...searchParams, code: values.code },
      {
        onSuccess() {
          if (searchParams.type === VerifyRequestParamsType.ONBOARDING) {
            navigate({ to: "/onboarding" });
          } else {
            navigate({ to: "/settings/profile" });
            toast.success(
              `Your email has been changed to ${searchParams.target}`
            );
          }
        },
        onError() {
          form.setError("root", { message: "Something went wrong" });
          // error may code failures or missing type
        },
      }
    );
  }

  return (
    <div className="container flex flex-col justify-center pb-32 pt-20">
      <div className="text-center">
        <h1 className="text-h1">Check your email</h1>
        <p className="mt-3 text-body-md text-muted-foreground">
          We've sent you a code to verify your email address.
        </p>
      </div>
      <div className="mx-auto flex w-72 max-w-full flex-col justify-center gap-1">
        <div className="flex w-full gap-2">
          <Form {...form}>
            <form
              method="POST"
              className="mx-auto min-w-[368px] max-w-sm mt-4 space-y-5"
              onSubmit={form.handleSubmit(onSubmit)}
            >
              <FormField
                control={form.control}
                name="code"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Code</FormLabel>
                    <FormControl>
                      <Input {...field} autoFocus={true} />
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
    </div>
  );
}
