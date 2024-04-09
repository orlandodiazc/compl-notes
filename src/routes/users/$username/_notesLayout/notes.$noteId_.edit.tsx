import { ErrorList, Field, TextareaField } from "@/components/forms";
import { Button } from "@/components/ui/button";
import { Icon } from "@/components/ui/icon";
import { Label } from "@/components/ui/label";
import { StatusButton } from "@/components/ui/status-button";
import { Textarea } from "@/components/ui/textarea";
import { noteQuery, usePutNoteMutation } from "@/lib/api/queryOptions";
import { cn, getNoteImgSrc } from "@/lib/utils";
import {
  FieldMetadata,
  getFieldsetProps,
  getFormProps,
  getInputProps,
  getTextareaProps,
  useForm,
} from "@conform-to/react";
import { parseWithZod } from "@conform-to/zod";
import { useSuspenseQuery } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { z } from "zod";

export const Route = createFileRoute(
  "/users/$username/_notesLayout/notes/$noteId/edit",
)({
  component: NoteEdit,
  loader: ({ params, context: { queryClient } }) => {
    return queryClient.ensureQueryData(noteQuery(params));
  },
});

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

type ImageFieldset = z.infer<typeof ImageFieldsetSchema>;

const NoteEditorSchema = z.object({
  title: z.string().min(titleMinLength).max(titleMaxLength),
  content: z.string().min(contentMinLength).max(contentMaxLength),
  images: z.array(ImageFieldsetSchema).max(5),
});

export default function NoteEdit() {
  const params = Route.useParams();
  const { data } = useSuspenseQuery(noteQuery(params));
  const navigate = Route.useNavigate();
  const { mutate, status } = usePutNoteMutation(params);
  const [form, fields] = useForm({
    id: "note-editor",
    onValidate({ formData }) {
      return parseWithZod(formData, { schema: NoteEditorSchema });
    },
    shouldValidate: "onBlur",
    shouldRevalidate: "onInput",
    defaultValue: {
      title: data.title,
      content: data.content,
      images: data.images.length ? data.images : [{}],
    },
    onSubmit(event, { formData }) {
      event.preventDefault();
      mutate(formData, {
        onSuccess: () => {
          navigate({
            to: "/users/$username/notes/$noteId",
            params,
          });
        },
      });
    },
  });
  const imageList = fields.images.getFieldList();
  return (
    <div className="absolute inset-0">
      <form
        method="PUT"
        className="flex h-full flex-col gap-y-4 overflow-y-auto overflow-x-hidden px-10 pb-28 pt-12"
        {...getFormProps(form)}
        encType="multipart/form-data"
      >
        <button type="submit" className="hidden" />
        <div className="flex flex-col gap-1">
          <Field
            labelProps={{ children: "Title" }}
            inputProps={{
              autoFocus: true,
              ...getInputProps(fields.title, { type: "text" }),
            }}
            errors={fields.title.errors}
          />
          <TextareaField
            labelProps={{ children: "Content" }}
            textareaProps={{
              ...getTextareaProps(fields.content),
            }}
            errors={fields.content.errors}
          />
          <div>
            <Label>Images</Label>
            <ul className="flex flex-col gap-4">
              {imageList.map((image, index) => (
                <li
                  key={image.key}
                  className="relative border-b-2 border-muted-foreground"
                >
                  <button
                    className="text-foreground-destructive absolute right-0 top-0"
                    {...form.remove.getButtonProps({
                      name: fields.images.name,
                      index,
                    })}
                  >
                    <span aria-hidden>
                      <Icon name="x" />
                    </span>{" "}
                    <span className="sr-only">Remove image {index + 1}</span>
                  </button>
                  <ImageChooser meta={image} />
                </li>
              ))}
            </ul>
          </div>
          <Button
            className="mt-3"
            {...form.insert.getButtonProps({
              name: fields.images.name,
              defaultValue: {},
            })}
          >
            <span aria-hidden>
              <Icon name="plus">Image</Icon>
            </span>
            <span className="sr-only">Add image</span>
          </Button>
          <ErrorList id={fields.images.errorId} errors={fields.images.errors} />
        </div>
        <ErrorList id={form.errorId} errors={form.errors} />
      </form>
      <div className="absolute bottom-3 left-3 right-3 flex items-center gap-2 rounded-lg bg-muted/80 p-4 pl-5 shadow-xl shadow-accent backdrop-blur-sm md:gap-4 md:pl-7 justify-end">
        <Button form={form.id} variant="destructive" type="reset">
          Reset
        </Button>
        <StatusButton
          form={form.id}
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

function ImageChooser({ meta }: { meta: FieldMetadata<ImageFieldset> }) {
  const fields = meta.getFieldset();
  const existingImage = Boolean(fields.id.initialValue);
  const [previewImage, setPreviewImage] = useState<string | null>(
    fields.id.initialValue ? getNoteImgSrc(fields.id.initialValue) : null,
  );
  const [altText, setAltText] = useState(fields.altText.initialValue ?? "");

  return (
    <fieldset {...getFieldsetProps(meta)}>
      <div className="flex gap-3">
        <div className="w-32">
          <div className="relative h-32 w-32">
            <label
              htmlFor={fields.file.id}
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
                    alt={altText ?? ""}
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
              {existingImage ? (
                <input {...getInputProps(fields.id, { type: "hidden" })} />
              ) : null}
              <input
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
                }}
                accept="image/*"
                {...getInputProps(fields.file, { type: "file" })}
              />
            </label>
          </div>
          <div className="min-h-[32px] px-4 pb-3 pt-1">
            <ErrorList id={fields.file.errorId} errors={fields.file.errors} />
          </div>
        </div>
        <div className="flex-1">
          <Label htmlFor={fields.altText.id}>Alt Text</Label>
          <Textarea
            onChange={(e) => setAltText(e.currentTarget.value)}
            {...getTextareaProps(fields.altText)}
            className="aria-invalid:border-input-invalid"
          />
          <div className="min-h-[32px] px-4 pb-3 pt-1">
            <ErrorList
              id={fields.altText.errorId}
              errors={fields.altText.errors}
            />
          </div>
        </div>
      </div>
      <div className="min-h-[32px] px-4 pb-3 pt-1">
        <ErrorList id={meta.errorId} errors={meta.errors} />
      </div>
    </fieldset>
  );
}
