import { Button } from "@/components/ui/button";
import { Icon } from "@/components/ui/icon";
import { Label } from "@/components/ui/label";
import { StatusButton } from "@/components/ui/status-button";
import { Textarea } from "@/components/ui/textarea";
import { ApiSchema } from "@/lib/api/apiSchema";
import { cn, getNoteImgSrc } from "@/lib/utils";

import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { zodResolver } from "@hookform/resolvers/zod";
import { useState } from "react";
import { useFieldArray, useForm, useFormContext } from "react-hook-form";
import { z } from "zod";

const titleMinLength = 1;
const titleMaxLength = 100;
const contentMinLength = 1;
const contentMaxLength = 10000;

const MAX_UPLOAD_SIZE = 1024 * 1024 * 1; // 1MB

const ImageFieldsetSchema = z.object({
  id: z.string().optional(),
  file: z
    .instanceof(File)
    .optional()
    .refine((file) => {
      return !file || file.size <= MAX_UPLOAD_SIZE;
    }, "File size must be less than 3MB"),
  altText: z.string().optional(),
});

const NoteEditorSchema = z.object({
  title: z.string().min(titleMinLength).max(titleMaxLength),
  content: z.string().min(contentMinLength).max(contentMaxLength),
  images: z.array(ImageFieldsetSchema).max(5, "shit man").optional(),
});

type NoteEditorForm = z.infer<typeof NoteEditorSchema>;

export default function NoteForm({
  note,
  handleSubmit,
  status,
}: {
  note?: ApiSchema["NoteSummaryResponse"];
  handleSubmit: (formData: FormData) => void;
  status: "pending" | "success" | "error" | "idle";
}) {
  const form = useForm<NoteEditorForm>({
    resolver: zodResolver(NoteEditorSchema),
    mode: "onChange",
    defaultValues: {
      title: note?.title,
      content: note?.content,
      images: note?.images.length ? note.images : [{}],
    },
  });

  const {
    fields: imageList,
    remove,
    append,
  } = useFieldArray({
    control: form.control,
    name: "images",
  });
  async function onSubmit(values: NoteEditorForm) {
    const result = NoteEditorSchema.parse(values);
    console.log(result);
    const formData = new FormData();
    formData.append("title", values.title);
    formData.append("content", values.content);
    values.images?.forEach((image, index) => {
      image.id && formData.append(`images[${index}].id`, image.id);
      image.file && formData.append(`images[${index}].file`, image.file);
      formData.append(`images[${index}].altText`, image.altText || "");
    });
    for (const pair of formData.entries()) {
      console.log(pair[0] + ", " + pair[1]);
    }
    handleSubmit(formData);
  }

  console.log(form.formState.errors);

  return (
    <div className="absolute inset-0">
      <Form {...form}>
        <form
          method="PUT"
          className="flex h-full flex-col gap-y-4 overflow-y-auto overflow-x-hidden px-10 pb-28 pt-12"
          onSubmit={form.handleSubmit(onSubmit)}
          id="note-editor"
        >
          <button type="submit" className="hidden" />
          <div className="flex flex-col gap-1">
            <FormField
              control={form.control}
              name="title"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Title</FormLabel>
                  <FormControl>
                    <Input {...field} autoFocus={true} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="content"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Content</FormLabel>
                  <FormControl>
                    <Textarea className="resize-none" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div>
              <Label>Images</Label>
              <ul className="flex flex-col gap-4">
                {imageList.map((image, index) => (
                  <li
                    key={image.id}
                    className="relative border-b-2 border-muted-foreground"
                  >
                    <button
                      className="text-foreground-destructive absolute right-0 top-0"
                      onClick={() => {
                        remove(index);
                      }}
                    >
                      <span aria-hidden>
                        <Icon name="x" />
                      </span>{" "}
                      <span className="sr-only">Remove image {index + 1}</span>
                    </button>
                    <ImageChooser meta={{ index }} />
                  </li>
                ))}
              </ul>
            </div>
            <Button
              className="mt-3"
              onClick={() => {
                append({});
              }}
              type="button"
            >
              <span aria-hidden>
                <Icon name="plus">Image</Icon>
              </span>
              <span className="sr-only">Add image</span>
            </Button>
            {/* <ErrorList
              id={fields.images.errorId}
              errors={fields.images.errors}
            /> */}
          </div>
          {/* <ErrorList id={form.errorId} errors={form.errors} /> */}
        </form>
      </Form>
      <div className="absolute bottom-3 left-3 right-3 flex items-center gap-2 rounded-lg bg-muted/80 p-4 pl-5 shadow-xl shadow-accent backdrop-blur-sm md:gap-4 md:pl-7 justify-end">
        <Button onClick={() => form.reset()} variant="destructive" type="reset">
          Reset
        </Button>
        <StatusButton
          form="note-editor"
          type="submit"
          disabled={status === "pending"}
          status={status}
        >
          Submit
        </StatusButton>
      </div>
    </div>
  );
}

function ImageChooser({ meta: { index } }: { meta: { index: number } }) {
  const form = useFormContext<NoteEditorForm>();
  const defaultValues = form.formState.defaultValues?.images?.[index];
  const initialId = defaultValues?.id;
  const existingImage = Boolean(initialId);
  const [previewImage, setPreviewImage] = useState<string | null>(
    initialId ? getNoteImgSrc(initialId) : null,
  );

  return (
    <div>
      <div className="flex gap-3">
        <div className="flex w-full gap-3">
          <div className="relative h-32 w-32">
            <FormField
              control={form.control}
              name={`images.${index}.file`}
              render={({
                field: { onChange, name, onBlur, ref, disabled },
              }) => (
                <FormItem>
                  <FormLabel
                    className={cn("group absolute h-32 w-32 rounded-lg", {
                      "bg-accent opacity-40 focus-within:opacity-100 hover:opacity-100":
                        !previewImage,
                      "cursor-pointer focus-within:ring-4": !existingImage,
                    })}
                  >
                    {previewImage ? (
                      <div className="relative">
                        <img
                          src={previewImage}
                          alt={form.getValues().images?.[index].altText}
                          className="h-32 w-32 rounded-lg object-cover"
                        />
                        {existingImage ? null : (
                          <div className="pointer-events-none absolute -right-0.5 -top-0.5 rotate-12 rounded-sm bg-secondary px-2 py-1 text-xs text-secondary-foreground shadow-md">
                            new
                          </div>
                        )}
                      </div>
                    ) : (
                      <div className="flex h-32 w-32 items-center justify-center rounded-lg border border-muted-foreground text-4xl text-muted-foreground">
                        <Icon name="plus" />
                      </div>
                    )}
                  </FormLabel>
                  <FormControl>
                    <Input
                      aria-label="Image"
                      className="absolute left-0 top-0 z-0 h-32 w-32 cursor-pointer opacity-0"
                      onChange={(event) => {
                        const file = event.target.files?.[0];

                        if (file) {
                          const reader = new FileReader();
                          reader.onloadend = () => {
                            setPreviewImage(reader.result as string);
                          };
                          reader.readAsDataURL(file);
                        } else {
                          setPreviewImage(null);
                        }
                        onChange(file);
                      }}
                      accept="image/*"
                      type="file"
                      name={name}
                      onBlur={onBlur}
                      ref={ref}
                      disabled={disabled}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name={`images.${index}.id`}
              render={({ field }) => (
                <FormControl>
                  <Input {...field} type="hidden" />
                </FormControl>
              )}
            />
          </div>
          <div className="flex-1">
            <FormField
              control={form.control}
              name={`images.${index}.altText`}
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Alt text</FormLabel>
                  <FormControl>
                    <Textarea {...field} className="resize-none" />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>
        </div>
      </div>
      <div className="min-h-[32px] px-4 pb-3 pt-1">
        {/* <ErrorList id={meta.errorId} errors={meta.errors} /> */}
      </div>
    </div>
  );
}
